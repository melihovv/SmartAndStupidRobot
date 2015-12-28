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

import java.awt.Point;
import java.util.EventListener;
import java.util.EventObject;
import java.util.ArrayList;
import java.util.List;

import melihovv.SmartAndStupidRobotGame.model.navigation.CellPosition;
import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;
import melihovv.SmartAndStupidRobotGame.model.navigation.MiddlePosition;

// TODO add menu bar.
// TODO load situation from file.

// my mod.
// TODO add seasons.
// TODO add rain.
// TODO add frozen mire. any robot can slide on frozen mire.

/**
 * The <code>SmartRobot</code> class defines smart robot on the field.
 */
public class SmartRobot extends FieldObject<CellPosition> {

    private final ArrayList<SmartRobotActionListener> _listenerList;
    private final SmartRobotActionEvent _event;

    public SmartRobot(Field field) {
        super(field);
        _listenerList = new ArrayList<>();
        _event = new SmartRobotActionEvent(this);
    }

    public void makeMove(Direction dir) {
        if (isMovePossible(dir)) {
            if (setPos(_pos.next(dir))) {
                fireRobotAction();
            }
        }
    }

    private boolean isMovePossible(Direction dir) {
        List<FieldObject> objs = _field.objects(Wall.class,
                new MiddlePosition(dir.opposite(), _pos.next(dir)));
        List<FieldObject> objs2 = _field.objects(Wall.class,
                new MiddlePosition(dir, _pos));
        if (!objs.isEmpty() || !objs2.isEmpty()) {
            return false;
        }

        Point nextPos = _pos.next(dir).pos();
        boolean isPosValid = _field.contains(nextPos);
        return isPosValid;
    }

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
     * The <code>SmartRobotActionEvent</code> defines event of smart robot.
     */
    public class SmartRobotActionEvent extends EventObject {
        public SmartRobotActionEvent(Object source) {
            super(source);
        }
    }

    public interface SmartRobotActionListener extends EventListener {
        void smartRobotMadeMove(SmartRobotActionEvent e);
    }

    public void addListener(SmartRobotActionListener l) {
        _listenerList.add(l);
    }

    public void removeListener(SmartRobotActionListener l) {
        _listenerList.remove(l);
    }

    public void clearListeners() {
        _listenerList.clear();
    }

    protected void fireRobotAction() {
        for (Object listener : _listenerList) {
            ((SmartRobotActionListener) listener).smartRobotMadeMove(_event);
        }
    }
}
