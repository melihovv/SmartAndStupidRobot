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

package melihovv.SmartAndStupidRobotGame.model.field.position;

import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The <code>CellPosition</code> class defines cell position.
 */
public class CellPosition {

    // Position on the field.
    private final Point _pos;
    // Offset which is used for calculation new position.
    private static final Map<Direction, int[]> _offset =
            new HashMap<Direction, int[]>() {{
                put(Direction.north(), new int[]{0, -1});
                put(Direction.south(), new int[]{0, 1});
                put(Direction.east(), new int[]{1, 0});
                put(Direction.west(), new int[]{-1, 0});
            }};

    /**
     * Constructs cell position.
     *
     * @param pos Position.
     */
    public CellPosition(final Point pos) {
        _pos = pos;
    }

    /**
     * Returns position.
     *
     * @return Position.
     */
    public Point pos() {
        return _pos;
    }

    /**
     * Returns new cell position which is located near the current one in
     * direction <code>dir</code>.
     *
     * @param dir Direction.
     * @return New cell position.
     */
    public CellPosition next(final Direction dir) {
        return new CellPosition(calcNewPos(_pos, dir));
    }

    /**
     * Calculates new position relative position <code>pos</code> in direction
     * <code>dir</code>.
     *
     * @param pos Position with respect to which the new position is calculated.
     * @param dir Direction in which new position is calculated.
     * @return Coordinates of the new position.
     */
    private static Point calcNewPos(final Point pos, final Direction dir) {
        final int[] offset = _offset.get(dir);
        return new Point(pos.x + offset[0], pos.y + offset[1]);
    }

    /**
     * Checks if two objects are equal. They are equal if <code>obj</code> is
     * instance of <code>CellPosition</code> and both have the same position.
     *
     * @param obj Object to check.
     * @return Result of checking.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CellPosition) {
            CellPosition other = (CellPosition) obj;
            return _pos.equals(other._pos);
        }
        return false;
    }

    /**
     * Returns hash code of the <code>CellPosition</code> instance.
     *
     * @return Hash code of the <code>CellPosition</code> instance.
     */
    @Override
    public int hashCode() {
        return _pos.x ^ _pos.y;
    }
}
