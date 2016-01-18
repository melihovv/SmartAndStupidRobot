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
import java.util.List;

/**
 * The <code>AbstractRobot</code> class defines abstract robot on the field.
 */
public abstract class AbstractRobot extends MovableObject<CellPosition> {

    /**
     * Constructs field object.
     *
     * @param field A field on which object is placed.
     */
    public AbstractRobot(Field field) {
        super(field);
    }

    /**
     * Check if movement is possible in the direction <code>dir</code>.
     *
     * @param dir The direction in which it is checked.
     * @return Result of checking.
     */
    protected boolean isMovePossible(final Direction dir) {
        List<FieldObject> objs = _field.objects(Wall.class,
                new MiddlePosition(dir, _pos));
        if (!objs.isEmpty()) {
            return false;
        }

        Point nextPos = _pos.next(dir).pos();
        return _field.contains(nextPos);
    }

    /**
     * Sets the robot position to <code>pos</code>.
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
}
