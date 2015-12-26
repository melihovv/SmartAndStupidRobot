/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Alexander Melihov
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

import java.awt.*;
import java.util.EventListener;
import java.util.EventObject;
import java.util.ArrayList;

import melihovv.SmartAndStupidRobotGame.model.navigation.CellPosition;
import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;
import melihovv.SmartAndStupidRobotGame.model.navigation.MiddlePosition;

/**
 * The <code>SmartRobot</code> class defines smart robot on the field.
 */
public class SmartRobot extends FieldObject<CellPosition> {

    private ArrayList<SmartRobotActionListener> _listenerList;
    private SmartRobotActionEvent _event;

    public SmartRobot(Field field) {
        super(field);
        _listenerList = new ArrayList<>();
        _event = new SmartRobotActionEvent(this);
    }

    public void makeMove(Direction dir) {
        if (isMovePossible(dir)) {
            setPos(_pos.next(dir));
            fireRobotAction();
        }
    }

    private boolean isMovePossible(Direction dir) {
        return _field.contains(_pos.next(dir).pos()) &&
                _field.objects(new MiddlePosition(dir, _pos)).isEmpty();
    }


    ////////////////////////////////////////////////////////////////////////////
    // Events.
    ////////////////////////////////////////////////////////////////////////////

    /**
     * The <code>SmartRobotActionEvent</code> defines event of smart robot.
     */
    public class SmartRobotActionEvent extends EventObject {
        public SmartRobotActionEvent(Object source) {
            super(source);
        }
    }

    public interface SmartRobotActionListener extends EventListener {
        void smartRobotMakedMove(SmartRobotActionEvent e);
    }

    public void addListener(SmartRobotActionListener l) {
        _listenerList.add(l);
    }

    public void removeListener(SmartRobotActionListener l) {
        _listenerList.remove(l);
    }

    protected void fireRobotAction() {
        for (Object listener : _listenerList) {
            ((SmartRobotActionListener) listener).smartRobotMakedMove(_event);
        }
    }
}
