/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Alexander Melihov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package melihovv.SmartAndStupidRobotGame.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import melihovv.SmartAndStupidRobotGame.model.field.*;
import melihovv.SmartAndStupidRobotGame.model.field.SmartRobot.SmartRobotActionEvent;
import melihovv.SmartAndStupidRobotGame.model.field.StupidRobot.StupidRobotActionEvent;
import melihovv.SmartAndStupidRobotGame.model.field.position.CellPosition;
import melihovv.SmartAndStupidRobotGame.model.field.position.MiddlePosition;
import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;
import melihovv.SmartAndStupidRobotGame.model.seasons.SeasonsManager;
import melihovv.SmartAndStupidRobotGame.model.seasons.Summer;
import melihovv.SmartAndStupidRobotGame.model.seasons.Winter;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * The <code>Model</code> class defines game model.
 */
public class Model {

    // The game field.
    private final Field _field;
    // The target of the smart robots.
    private final Target _target;
    // If game is finished.
    private boolean _isGameFinished;
    // Logger.
    private static final Logger log = Logger.getLogger(Model.class.getName());
    // Seasons manager.
    private final SeasonsManager _manager;
    // List of the listeners.
    private final List<ModelListener> _listenerList;
    // Seasons event.
    private final ModelEvent _event;

    /**
     * Constructs game model.
     */
    public Model() {
        _field = new Field(new Dimension(10, 10));
        _target = new Target(_field);
        _isGameFinished = false;
        _manager = new SeasonsManager(_field);
        _event = new ModelEvent(this);
        _listenerList = new ArrayList<>();
    }

    /**
     * Starts new game.
     *
     * @throws IOException|IllegalArgumentException If json file is invalid or
     *                                              it is impossible to read
     *                                              file.
     */
    public void start(final String path)
            throws IOException, IllegalArgumentException {

        _isGameFinished = false;

        try {
            loadSituation(path);
        } catch (Exception e) {
            _manager.stop();
            _manager.clearListeners();
            _manager.removeAllSeasons();
            smartRobot().clearListeners();
            stupidRobot().clearListeners();
            _field.clear();
            throw e;
        }

        _manager.stop();
        _manager.clearListeners();
        _manager.removeAllSeasons();
        _manager.addSeason(new Winter("winter", -20, ""));
        _manager.addSeason(new Summer("summer", 25, "rain"));
        _manager.start();
        _manager.addListener(new SeasonsListener());

        identifyGameOver();
        smartRobot().clearListeners();
        stupidRobot().clearListeners();
        smartRobot().addListener(new SmartRobotListener());
        stupidRobot().addListener(new StupidRobotListener());
    }

    /**
     * Identifies game over.
     */
    private void identifyGameOver() {
        if (_isGameFinished) {
            return;
        }

        CellPosition smRobPos = smartRobot().pos();
        if (smRobPos.equals(_target.pos())) {
            _isGameFinished = true;
            log.info("Smart robot has reached target position");
            _event.setMessage("Smart robot wins");
            fireGameIsOver();
        }

        for (FieldObject mire : _field.objects(Mire.class)) {
            if (smRobPos.equals(mire.pos()) && !((Mire) mire).isFrozen()) {
                _isGameFinished = true;
                log.info("Smart robot in mire");
                _event.setMessage("Smart robot in mire, he lose");
                fireGameIsOver();
                break;
            }
        }
    }

    /**
     * Load game situation from file.
     *
     * @throws IOException|InvalidArgumentException If json file is invalid or
     *                                              it is impossible to read
     *                                              file.
     */
    private void loadSituation(final String path)
            throws IllegalArgumentException, IOException {

        _field.clear();

        final String input = new String(Files.readAllBytes(Paths.get(path)));
        final JsonParser parser = new JsonParser();
        final JsonObject json = parser.parse(input).getAsJsonObject();

        if (!json.has("field") ||
                !json.has("target") ||
                !json.has("smart robot") ||
                !json.has("stupid robot")) {
            throw new IllegalArgumentException("Invalid situation file");
        }

        // Add field size.
        final JsonObject field = json.get("field").getAsJsonObject();
        if (!field.has("size")) {
            throw new IllegalArgumentException("Invalid situation file");
        }

        final int width;
        final int height;
        {
            final JsonArray size = field.get("size").getAsJsonArray();
            width = size.get(0).getAsInt();
            height = size.get(1).getAsInt();
            if (width < 2 || height < 2) {
                throw new IllegalArgumentException("Invalid situation file");
            }
            _field.setSize(new Dimension(width, height));
        }

        // Add target.
        final JsonObject target = json.get("target").getAsJsonObject();
        if (!target.has("pos")) {
            throw new IllegalArgumentException("Invalid situation file");
        }
        {
            final JsonArray pos = target.get("pos").getAsJsonArray();
            final int x = pos.get(0).getAsInt();
            final int y = pos.get(1).getAsInt();
            if (!_field.contains(new Point(x, y))) {
                throw new IllegalArgumentException("Invalid situation file");
            }
            _field.addObject(
                    new CellPosition(new Point(x, y)),
                    _target
            );
        }

        // Add smart robot.
        final JsonObject smRobot = json.get("smart robot").getAsJsonObject();
        if (!smRobot.has("pos")) {
            throw new IllegalArgumentException("Invalid situation file");
        }
        {
            final JsonArray pos = smRobot.get("pos").getAsJsonArray();
            final int x = pos.get(0).getAsInt();
            final int y = pos.get(1).getAsInt();
            if (!_field.contains(new Point(x, y))) {
                throw new IllegalArgumentException("Invalid situation file");
            }
            _field.addObject(
                    new CellPosition(new Point(x, y)),
                    new SmartRobot(_field)
            );
        }

        // Add stupid robot.
        final JsonObject stRobot = json.get("stupid robot").getAsJsonObject();
        if (!stRobot.has("pos")) {
            throw new IllegalArgumentException("Invalid situation file");
        }
        {
            final JsonArray pos = stRobot.get("pos").getAsJsonArray();
            final int x = pos.get(0).getAsInt();
            final int y = pos.get(1).getAsInt();
            if (!_field.contains(new Point(x, y))) {
                throw new IllegalArgumentException("Invalid situation file");
            }
            _field.addObject(
                    new CellPosition(new Point(x, y)),
                    new StupidRobot(_field)
            );
        }

        // Add walls.
        if (json.has("walls")) {
            final JsonArray walls = json.get("walls").getAsJsonArray();
            for (JsonElement temp : walls) {
                JsonObject wall = temp.getAsJsonObject();

                if (!wall.has("pos") || !wall.has("direction")) {
                    throw new IllegalArgumentException(
                            "Invalid situation file");
                }

                final JsonArray pos = wall.get("pos").getAsJsonArray();
                final int x = pos.get(0).getAsInt();
                final int y = pos.get(1).getAsInt();
                if (!_field.contains(new Point(x, y))) {
                    throw new IllegalArgumentException(
                            "Invalid situation file");
                }

                Direction dir;
                switch (wall.get("direction").getAsString()) {
                    case "north":
                        dir = Direction.north();
                        break;
                    case "south":
                        dir = Direction.south();
                        break;
                    case "west":
                        dir = Direction.west();
                        break;
                    case "east":
                        dir = Direction.east();
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Invalid situation file");
                }

                _field.addObject(
                        new MiddlePosition(
                                dir,
                                new CellPosition(new Point(x, y))
                        ),
                        new Wall(_field)
                );
            }
        }

        // Add mires.
        if (json.has("mires")) {
            final JsonArray mires = json.get("mires").getAsJsonArray();
            for (JsonElement temp : mires) {
                JsonObject mire = temp.getAsJsonObject();

                if (!mire.has("pos")) {
                    throw new IllegalArgumentException(
                            "Invalid situation file");
                }

                final JsonArray pos = mire.get("pos").getAsJsonArray();
                final int x = pos.get(0).getAsInt();
                final int y = pos.get(1).getAsInt();
                if (!_field.contains(new Point(x, y))) {
                    throw new IllegalArgumentException(
                            "Invalid situation file");
                }

                _field.addObject(
                        new CellPosition(new Point(x, y)),
                        new Mire(_field)
                );
            }
        }
    }

    /**
     * Makes one game step: first the smart robot makes move, then the stupid
     * one.
     *
     * @param dir Direction in which smart robot make movement.
     */
    public void makeMove(final Direction dir) {
        if (!_isGameFinished) {
            smartRobot().makeMove(dir);
        }
    }

    /**
     * Returns game field.
     *
     * @return Game field.
     */
    public Field field() {
        return _field;
    }

    /**
     * Returns the smart robot.
     *
     * @return The smart robot.
     */
    public SmartRobot smartRobot() {
        return (SmartRobot) _field.object(SmartRobot.class);
    }

    /**
     * Returns the stupid robot.
     *
     * @return The stupid robot.
     */
    public StupidRobot stupidRobot() {
        return (StupidRobot) _field.object(StupidRobot.class);
    }

    /**
     * Returns true if game is finished, otherwise - false.
     *
     * @return true if game is finished, otherwise - false
     */
    public boolean isGameFinished() {
        return _isGameFinished;
    }

    /**
     * Returns the seasons manager.
     *
     * @return The seasons manager.
     */
    public SeasonsManager seasonsManager() {
        return _manager;
    }

    /**
     * Returns the target of the smart robot.
     *
     * @return The target of the smart robot.
     */
    public Target target() {
        return _target;
    }


    ////////////////////////////////////////////////////////////////////////////
    // Events.
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Adds listener <code>l</code> to the list of listeners.
     *
     * @param l The seasons listener.
     */
    public void addListener(final ModelListener l) {
        _listenerList.add(l);
    }

    /**
     * Removes listener <code>l</code> from the list of listeners.
     *
     * @param l The seasons listener.
     */
    public void removeListener(final ModelListener l) {
        _listenerList.remove(l);
    }

    /**
     * Removes all the listeners.
     */
    public void clearListeners() {
        _listenerList.clear();
    }

    /**
     * Notifies all the listers that the game is over.
     */
    private void fireGameIsOver() {
        for (Object listener : _listenerList) {
            ((ModelListener) listener).gameIsOver(_event);
        }
    }

    /**
     * The target of the smart robot.
     */
    public static class Target extends FieldObject<CellPosition> {
        public Target(final Field field) {
            super(field);
        }

        /**
         * Sets the target of the smart robot position to <code>pos</code>.
         *
         * @param pos The position to which object will be placed.
         * @return True if position was not null, otherwise — false.
         */
        @Override
        public boolean setPos(final CellPosition pos) {
            if (pos != null) {
                _pos = pos;
                return true;
            }
            return false;
        }

        /**
         * Draws field object.
         *
         * @param g         Graphics context.
         * @param ltc       Left top corner of cell where to draw.
         * @param constants Such constants as font size, cell size, etc.
         * @param colors    Colors.
         */
        @Override
        public void draw(
                final Graphics g,
                final Point ltc,
                final Map<String, Integer> constants,
                final Map<String, Color> colors
        ) {
            Color preserved = g.getColor();
            g.setColor(colors.get("font"));

            g.drawString(
                    "T",
                    ltc.x + constants.get("cell size") / 3,
                    ltc.y + constants.get("cell size") / 5 +
                            constants.get("font size")
            );

            g.setColor(preserved);
        }
    }

    /**
     * Smart robot listener.
     */
    private class SmartRobotListener
            implements SmartRobot.SmartRobotActionListener {

        @Override
        public void smartRobotMadeMove(final SmartRobotActionEvent e) {
            log.fine("Smart robot made move");
            identifyGameOver();

            if (!_isGameFinished) {
                stupidRobot().makeMove();
            }
        }
    }

    /**
     * Stupid robot listener.
     */
    private class StupidRobotListener
            implements StupidRobot.StupidRobotActionListener {

        @Override
        public void stupidRobotMadeMove(final StupidRobotActionEvent e) {
        }

        @Override
        public void smartRobotIsCaught(final StupidRobotActionEvent e) {
            log.info("Smart robot is caught");
            _isGameFinished = true;
        }
    }

    /**
     * Model event.
     */
    public static class ModelEvent extends EventObject {

        private String _message;

        /**
         * Constructs a prototypical Event.
         *
         * @param source The object on which the Event initially occurred.
         * @throws IllegalArgumentException if source is null.
         */
        public ModelEvent(final Object source) {
            super(source);
        }

        /**
         * Returns game event message.
         *
         * @return Game event message.
         */
        public String message() {
            return _message;
        }

        /**
         * Sets game event message.
         *
         * @param message Game event message.
         */
        public void setMessage(String message) {
            _message = message;
        }
    }

    /**
     * Model listener interface.
     */
    public interface ModelListener extends EventListener {

        /**
         * This method is invoked when game is over.
         *
         * @param e Model event.
         */
        void gameIsOver(final ModelEvent e);
    }

    /**
     * Season listener.
     */
    private class SeasonsListener implements SeasonsManager.SeasonsListener {

        @Override
        public void seasonIsChanged(final SeasonsManager.SeasonsEvent e) {
            identifyGameOver();
        }
    }
}
