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
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

/**
 * The <code>SmartRobot</code> class defines the smart robot on the field.
 */
public class SmartRobot extends MovableObject<CellPosition> {

    // List of the smart robot's listeners.
    private final List<SmartRobotActionListener> _listenerList;
    // The smart robot action event.
    private final SmartRobotActionEvent _event;

    /**
     * Constructs the smart robot.
     *
     * @param field A field on which the smart robot is placed.
     */
    public SmartRobot(Field field) {
        super(field);
        _listenerList = new ArrayList<>();
        _event = new SmartRobotActionEvent(this);
    }

    /**
     * Makes step by the smart robot in direction <code>dir</code>.
     *
     * @param dir The direction in which the motion is made.
     */
    public void makeMove(Direction dir) {
        if (isMovePossible(dir)) {
            if (_field.move(this, dir)) {
                fireRobotMadeMove();
            }
        }
    }

    /**
     * Check if movement is possible in the direction <code>dir</code>.
     *
     * @param dir The direction in which it is checked.
     * @return Result of checking.
     */
    private boolean isMovePossible(Direction dir) {
        List<FieldObject> objs = _field.objects(Wall.class,
                new MiddlePosition(dir, _pos));
        if (!objs.isEmpty()) {
            return false;
        }

        Point nextPos = _pos.next(dir).pos();
        return _field.contains(nextPos);
    }

    /**
     * Sets the smart robot position to <code>pos</code>.
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


    ////////////////////////////////////////////////////////////////////////////
    // Events.
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Adds the smart robot action listener <code>l</code> to the list of
     * listeners.
     *
     * @param l The smart robot action listener.
     */
    public void addListener(SmartRobotActionListener l) {
        _listenerList.add(l);
    }

    /**
     * Removes the smart robot action listener <code>l</code> from the list of
     * listeners.
     *
     * @param l The smart robot action listener.
     */
    public void removeListener(SmartRobotActionListener l) {
        _listenerList.remove(l);
    }

    /**
     * Removes all the smart robot action listeners.
     */
    public void clearListeners() {
        _listenerList.clear();
    }

    /**
     * Notifies all the listeners that the smart robot is made movement.
     */
    private void fireRobotMadeMove() {
        for (Object listener : _listenerList) {
            ((SmartRobotActionListener) listener).smartRobotMadeMove(_event);
        }
    }

    /**
     * The <code>SmartRobotActionEvent</code> defines the smart robot event.
     */
    public static class SmartRobotActionEvent extends EventObject {

        /**
         * Constructs the smart robot action event.
         *
         * @param source Source of event.
         */
        public SmartRobotActionEvent(Object source) {
            super(source);
        }
    }

    /**
     * The <code>SmartRobotActionListener</code> defines the smart robot action
     * listener.
     */
    public interface SmartRobotActionListener extends EventListener {

        /**
         * This method is invoked after the smart robot made movement.
         *
         * @param e The smart robot action event.
         */
        void smartRobotMadeMove(SmartRobotActionEvent e);
    }
}
