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

package melihovv.SmartAndStupidRobotGame.model.navigation;

/**
 * The <code>MiddlePosition</code> defines position between two cells.
 */
public class MiddlePosition {

    // Cell position.
    private final CellPosition _cellPos;
    // Direction which is corresponding the side of the cell.
    private final Direction _direct;

    /**
     * Constructs middle position.
     *
     * @param direct  Direction which is corresponding the side of the cell.
     * @param cellPos Cell position.
     */
    public MiddlePosition(Direction direct, CellPosition cellPos) {
        _direct = direct;
        _cellPos = cellPos;
    }

    /**
     * Returns cell position.
     *
     * @return Cell position.
     */
    public CellPosition cellPos() {
        return _cellPos;
    }

    /**
     * Returns direction.
     * @return Direction.
     */
    public Direction direct() {
        return _direct;
    }

    /**
     * Checks if two objects are equal. They are equal if <code>obj</code> is
     * instance of <code>MiddlePosition</code> and both have the same cell
     * position and direction.
     *
     * @param obj Object to check.
     * @return Result of checking.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MiddlePosition) {
            final MiddlePosition other = (MiddlePosition) obj;
            return _cellPos.equals(other._cellPos) &&
                    _direct.equals(other._direct);
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
        return _cellPos.hashCode() ^ _direct.hashCode();
    }
}
