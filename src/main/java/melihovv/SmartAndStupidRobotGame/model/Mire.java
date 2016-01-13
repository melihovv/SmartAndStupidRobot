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

import melihovv.SmartAndStupidRobotGame.model.navigation.CellPosition;

/**
 * The <code>Mire</code> class defines mire on the field.
 */
public class Mire extends FieldObject<CellPosition> {

    /**
     * Constructs mire.
     *
     * @param field A field on which mire is placed.
     */
    public Mire(Field field) {
        super(field);
    }

    /**
     * Sets object position to <code>pos</code> if there are not any other mires
     * on the same position.
     *
     * @param pos The position to which object will be placed.
     * @return True if position was set, otherwise — false.
     */
    @Override
    public boolean setPos(CellPosition pos) {
        if (pos != null && _field.objects(Mire.class, pos).isEmpty()) {
            _pos = pos;
            return true;
        }
        return false;
    }
}
