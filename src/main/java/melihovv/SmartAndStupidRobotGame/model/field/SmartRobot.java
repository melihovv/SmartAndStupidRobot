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

import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * The <code>SmartRobot</code> class defines the smart robot on the field.
 */
public class SmartRobot extends AbstractRobot {

    // List of the smart robot's listeners.
    private final List<SmartRobotActionListener> _listenerList;
    // The smart robot action event.
    private final SmartRobotActionEvent _event;

    /**
     * Constructs the smart robot.
     *
     * @param field A field on which the smart robot is placed.
     */
    public SmartRobot(final Field field) {
        super(field);
        _listenerList = new ArrayList<>();
        _event = new SmartRobotActionEvent(this);
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
                "Sm",
                ltc.x + constants.get("cell size") / 5,
                ltc.y + constants.get("cell size") / 5 +
                        constants.get("font size")
        );

        g.setColor(preserved);
    }

    /**
     * Makes step by the smart robot in direction <code>dir</code>.
     *
     * @param dir The direction in which the motion is made.
     */
    public void makeMove(final Direction dir) {
        if (isMovePossible(dir)) {
            if (_field.move(this, dir)) {
                fireRobotMadeMove();
            }
        }
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
    public void addListener(final SmartRobotActionListener l) {
        _listenerList.add(l);
    }

    /**
     * Removes the smart robot action listener <code>l</code> from the list of
     * listeners.
     *
     * @param l The smart robot action listener.
     */
    public void removeListener(final SmartRobotActionListener l) {
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
        public SmartRobotActionEvent(final Object source) {
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
        void smartRobotMadeMove(final SmartRobotActionEvent e);
    }
}
