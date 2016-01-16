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

import melihovv.SmartAndStupidRobotGame.model.field.*;
import melihovv.SmartAndStupidRobotGame.model.field.position.CellPosition;
import melihovv.SmartAndStupidRobotGame.model.field.position.MiddlePosition;
import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;
import melihovv.SmartAndStupidRobotGame.model.seasons.SeasonsManager;
import melihovv.SmartAndStupidRobotGame.model.seasons.Summer;
import melihovv.SmartAndStupidRobotGame.model.seasons.Winter;

import java.awt.*;
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

    /**
     * Constructs game model.
     */
    public Model() {
        _field = new Field(new Dimension(10, 10));
        _target = new Target(_field);
        _isGameFinished = false;
        _manager = new SeasonsManager();
    }

    /**
     * Starts new game.
     */
    public void start() {
        _isGameFinished = false;

        _manager.clearListeners();
        _manager.addSeason(new Summer("summer", 25, "rain"));
        _manager.addSeason(new Winter("winter", -20, ""));
        _manager.start();

        generateField();
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
        CellPosition smRobPos = smartRobot().pos();
        if (smRobPos.equals(_target.pos())) {
            _isGameFinished = true;
            log.info("Smart robot has reached target position");
        }

        for (FieldObject mire : _field.objects(Mire.class)) {
            if (smRobPos.equals(mire.pos())) {
                _isGameFinished = true;
                log.info("Smart robot in mire");
                break;
            }
        }
    }

    /**
     * Generates game field.
     */
    private void generateField() {
        _field.clear();

        // Add target.
        _field.addObject(
                new CellPosition(new Point(9, 6)),
                _target
        );

        // Add smart robot.
        _field.addObject(
                new CellPosition(new Point(5, 5)),
                new SmartRobot(_field)
        );
        // Add stupid robot.
        _field.addObject(
                new CellPosition(new Point(2, 2)),
                new StupidRobot(_field)
        );

        // Add mires.
        _field.addObject(
                new CellPosition(new Point(4, 3)),
                new Mire(_field)
        );
        _field.addObject(
                new CellPosition(new Point(5, 3)),
                new Mire(_field)
        );
        _field.addObject(
                new CellPosition(new Point(5, 4)),
                new Mire(_field)
        );

        // Add walls.
        _field.addObject(
                new MiddlePosition(
                        Direction.east(),
                        new CellPosition(new Point(6, 1))
                ),
                new Wall(_field)
        );
        _field.addObject(
                new MiddlePosition(
                        Direction.east(),
                        new CellPosition(new Point(6, 2))
                ),
                new Wall(_field)
        );
    }

    /**
     * Makes one game step: first the smart robot makes move, then the stupid
     * one.
     *
     * @param dir Direction in which smart robot make movement.
     */
    public void makeMove(Direction dir) {
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
     * @return The smart robot.
     */
    public SmartRobot smartRobot() {
        List<FieldObject> objects = _field.objects(SmartRobot.class);
        return objects.isEmpty() ? null : (SmartRobot) objects.get(0);
    }

    /**
     * Returns the stupid robot.
     * @return The stupid robot.
     */
    public StupidRobot stupidRobot() {
        List<FieldObject> objects = _field.objects(StupidRobot.class);
        return objects.isEmpty() ? null : (StupidRobot) objects.get(0);
    }

    /**
     * Returns the seasons manager.
     * @return The seasons manager.
     */
    public SeasonsManager seasonsManager() {
        return _manager;
    }

    /**
     * Returns the target of the smart robot.
     * @return The target of the smart robot.
     */
    public Target target() {
        return _target;
    }

    /**
     * The target of the smart robot.
     */
    public static class Target extends FieldObject<CellPosition> {
        public Target(Field field) {
            super(field);
        }

        /**
         * Sets the target of the smart robot position to <code>pos</code>.
         *
         * @param pos The position to which object will be placed.
         * @return True if position was not null, otherwise — false.
         */
        @Override
        public boolean setPos(CellPosition pos) {
            if (pos != null) {
                _pos = pos;
                return true;
            }
            return false;
        }
    }

    /**
     * Smart robot listener.
     */
    private class SmartRobotListener
            implements SmartRobot.SmartRobotActionListener {

        @Override
        public void smartRobotMadeMove(SmartRobot.SmartRobotActionEvent e) {
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
        public void stupidRobotMadeMove(StupidRobot.StupidRobotActionEvent e) {
        }

        @Override
        public void smartRobotIsCaught(StupidRobot.StupidRobotActionEvent e) {
            log.info("Smart robot is caught");
            _isGameFinished = true;
        }
    }
}
