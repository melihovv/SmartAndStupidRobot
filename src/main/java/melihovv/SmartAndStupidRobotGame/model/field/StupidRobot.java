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

package melihovv.SmartAndStupidRobotGame.model.field;

import melihovv.SmartAndStupidRobotGame.model.field.position.CellPosition;
import melihovv.SmartAndStupidRobotGame.model.field.position.MiddlePosition;
import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * The <code>StupidRobot</code> class defines stupid robot on the field.
 */
public class StupidRobot extends AbstractRobot {

    // List of the stupid robot's listeners.
    private final List<StupidRobotActionListener> _listenerList;
    // The stupid robot action event.
    private final StupidRobotActionEvent _event;
    // The number of steps to skip.
    private int _stepsToSkip = 0;
    // Logger.
    private static final Logger log = Logger.getLogger(
            StupidRobot.class.getName()
    );

    /**
     * Constructs the stupid robot.
     *
     * @param field A field on which the stupid robot is placed.
     */
    public StupidRobot(final Field field) {
        super(field);
        _listenerList = new ArrayList<>();
        _event = new StupidRobotActionEvent(this);
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
                "St",
                ltc.x + constants.get("cell size") / 3,
                ltc.y + constants.get("cell size") / 5 +
                        constants.get("font size")
        );

        g.setColor(preserved);
    }

    /**
     * Makes step by the stupid robot.
     */
    public void makeMove() {
        if (_stepsToSkip != 0) {
            --_stepsToSkip;
            return;
        }

        CellPosition smRobPos = ((SmartRobot) _field.objects(SmartRobot.class)
                .get(0)).pos();

        // Smart robot is in the same cell.
        if (_pos.equals(smRobPos)) {
            fireSmartRobotIsCatched();
            return;
        }

        // Check if smart robot is near.
        boolean isSmartRobotNear = isSmartRobotNear(smRobPos);
        log.fine("Is smart robot near? " + isSmartRobotNear);

        if (isSmartRobotNear) {
            if (smRobPos.pos().getX() == _pos.pos().getX()) {
                // Robots are in the same column.
                List<FieldObject> nearWall = _field.objects(
                        Wall.class,
                        new MiddlePosition(
                                smRobPos.pos().getY() < _pos.pos().getY() ?
                                        Direction.north() :
                                        Direction.south(),
                                _pos
                        )
                );

                if (nearWall.isEmpty()) {
                    setPos(smRobPos);
                    fireSmartRobotIsCatched();
                }
            } else if (smRobPos.pos().getY() == _pos.pos().getY()) {
                // Robots are in the same row.
                List<FieldObject> nearWall = _field.objects(
                        Wall.class,
                        new MiddlePosition(
                                smRobPos.pos().getX() < _pos.pos().getX() ?
                                        Direction.west() :
                                        Direction.east(),
                                _pos
                        )
                );

                if (nearWall.isEmpty()) {
                    setPos(smRobPos);
                    fireSmartRobotIsCatched();
                }
            }
        } else {
            Direction dir;

            // Robots are in the same column.
            if (smRobPos.pos().getX() == _pos.pos().getX()) {
                // Smart robot is on the top.
                if (smRobPos.pos().getY() < _pos.pos().getY()) {
                    dir = Direction.north();
                } else {
                    dir = Direction.south();
                }
            } else {
                // Come closer horizontally.
                // Smart robot is on the right.
                if (smRobPos.pos().getX() > _pos.pos().getX()) {
                    dir = Direction.east();
                } else {
                    dir = Direction.west();
                }
            }

            if (isMovePossible(dir)) {
                if (_field.move(this, dir)) {
                    checkIfRobotIsInMire();
                    fireRobotMadeMove();
                }
            }
        }
    }

    /**
     * Checks is stupid robot in not frozen mire.
     */
    public void checkIfRobotIsInMire() {
        boolean isInMire = false;
        for (FieldObject mire : _field.objects(Mire.class)) {
            if (_pos.equals(mire.pos()) &&
                    !((Mire) mire).isFrozen()) {

                log.info("Stupid robot in mire, skip 3 steps");
                _stepsToSkip = 3;
                isInMire = true;
                break;
            }
        }

        if (!isInMire) {
            _stepsToSkip = 0;
        }
    }

    /**
     * Checks if the smart robot is near.
     *
     * @param smRobPos Position of the smart robot.
     * @return Result of checking.
     */
    private boolean isSmartRobotNear(final CellPosition smRobPos) {
        return _pos.next(Direction.north()).equals(smRobPos) ||
                _pos.next(Direction.south()).equals(smRobPos) ||
                _pos.next(Direction.east()).equals(smRobPos) ||
                _pos.next(Direction.west()).equals(smRobPos);
    }


    ////////////////////////////////////////////////////////////////////////////
    // Events.
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Adds the stupid robot action listener <code>l</code> to the list of
     * listeners.
     *
     * @param l The stupid robot action listener.
     */
    public void addListener(final StupidRobotActionListener l) {
        _listenerList.add(l);
    }

    /**
     * Removes the stupid robot action listener <code>l</code> from the list of
     * listeners.
     *
     * @param l The stupid robot action listener.
     */
    public void removeListener(final StupidRobotActionListener l) {
        _listenerList.remove(l);
    }

    /**
     * Removes all the stupid robot action listeners.
     */
    public void clearListeners() {
        _listenerList.clear();
    }

    /**
     * Notifies all the listeners that the stupid robot is made movement.
     */
    private void fireRobotMadeMove() {
        for (Object listener : _listenerList) {
            ((StupidRobotActionListener) listener).stupidRobotMadeMove(_event);
        }
    }

    /**
     * Notifies all the listeners that the stupid robot has caught the smart
     * one.
     */
    private void fireSmartRobotIsCatched() {
        for (Object listener : _listenerList) {
            ((StupidRobotActionListener) listener).smartRobotIsCaught(_event);
        }
    }

    /**
     * The <code>StupidRobotActionEvent</code> defines the stupid robot event.
     */
    public static class StupidRobotActionEvent extends EventObject {

        /**
         * Constructs the stupid robot action event.
         *
         * @param source Source of event.
         */
        public StupidRobotActionEvent(final Object source) {
            super(source);
        }
    }

    /**
     * The <code>SmartRobotActionListener</code> defines the stupid robot action
     * listener.
     */
    public interface StupidRobotActionListener extends EventListener {

        /**
         * This method is invoked after the stupid robot made movement.
         *
         * @param e The smart robot action event.
         */
        void stupidRobotMadeMove(final StupidRobotActionEvent e);

        /**
         * This method is invoked when stupid robot has caught the smart one.
         *
         * @param e The smart robot action event.
         */
        void smartRobotIsCaught(final StupidRobotActionEvent e);
    }
}
