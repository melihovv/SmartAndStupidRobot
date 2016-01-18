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
import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;

import java.util.List;

/**
 * The <code>Mire</code> class defines mire on the field.
 */
public class Mire extends ImmovableObject<CellPosition>
        implements CanMoveFieldObject<CellPosition> {

    // If mire is frozen or not.
    private boolean _isFrozen;

    /**
     * Constructs mire.
     *
     * @param field A field on which mire is placed.
     */
    public Mire(final Field field) {
        super(field);
        _isFrozen = false;
    }

    /**
     * Returns true if mire is frozen, otherwise - false.
     *
     * @return True if mire is frozen, otherwise - false.
     */
    public boolean isFrozen() {
        return _isFrozen;
    }

    /**
     * Freezes mire.
     */
    public void freeze() {
        _isFrozen = true;
    }

    /**
     * Unfreezes mire.
     */
    public void unfreeze() {
        _isFrozen = false;
    }

    /**
     * Move movable object <code>object</code> in the direction of
     * <code>dir</code>.
     *
     * @param object Object to move.
     * @param dir    Direction in which object is moved.
     * @return True if object was moved, otherwise - false.
     */
    @Override
    public boolean move(final MovableObject<CellPosition> object,
                        final Direction dir) {
        if (_isFrozen) {
            final List<FieldObject> objectsAhead = _field.objects(
                    Mire.class,
                    _pos.next(dir)
            );

            if (objectsAhead.size() != 0) {
                return object.setPos(_pos.next(dir));
            }

            final List<FieldObject> objectsBehind = _field.objects(
                    Mire.class,
                    _pos.next(dir.opposite())
            );

            if (objectsBehind.size() != 0) {
                return object.setPos(_pos.next(dir));
            }
        }

        return object.setPos(_pos);
    }

    /**
     * Sets object position to <code>pos</code> if there are not any other mires
     * on the same position.
     *
     * @param pos The position to which object will be placed.
     * @return True if position was set, otherwise — false.
     */
    @Override
    public boolean setPos(final CellPosition pos) {
        if (pos != null && _field.objects(Mire.class, pos).isEmpty()) {
            _pos = pos;
            return true;
        }
        return false;
    }
}
