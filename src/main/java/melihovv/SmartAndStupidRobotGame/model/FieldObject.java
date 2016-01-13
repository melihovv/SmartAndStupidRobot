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

/**
 * The <code>FieldObject</code> class defines game field object. It has
 * position on the game field.
 */
public abstract class FieldObject<Position> {

    // A field.
    protected final Field _field;
    // Position on a field.
    protected Position _pos;

    /**
     * Constructs field object.
     *
     * @param field A field on which object is placed.
     */
    public FieldObject(Field field) {
        _field = field;
    }

    /**
     * Returns object's position.
     *
     * @return Object's position.
     */
    public Position pos() {
        return _pos;
    }

    /**
     * Sets object position to <code>pos</code> if it isn't occupied by other
     * object.
     *
     * @param pos The position to which object will be placed.
     * @return True if position was set, otherwise — false.
     */
    public boolean setPos(Position pos) {
        if (pos != null && _field.isPosFree(pos)) {
            _pos = pos;
            return true;
        }

        return false;
    }
}
