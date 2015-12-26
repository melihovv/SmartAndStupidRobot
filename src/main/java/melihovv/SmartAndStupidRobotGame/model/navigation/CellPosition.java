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

import java.awt.Point;
import java.util.HashMap;

/**
 * The <code>CellPosition</code> class defines cell position.
 */
public class CellPosition {

    private Point _pos;
    private static HashMap<Direction, int[]> _offset =
            new HashMap<Direction, int[]>() {{
                put(Direction.north(), new int[]{0, -1});
                put(Direction.south(), new int[]{0, 1});
                put(Direction.east(), new int[]{1, 0});
                put(Direction.west(), new int[]{-1, 0});
            }};

    public CellPosition(Point pos) {
        _pos = pos;
    }

    public Point pos() {
        return _pos;
    }

    public CellPosition next(Direction dir) {
        return new CellPosition(calcNewPos(_pos, dir));
    }

    private static Point calcNewPos(Point pos, Direction dir) {
        int[] offset = _offset.get(dir);
        return new Point(pos.x + offset[0], pos.y + offset[1]);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CellPosition) {
            CellPosition other = (CellPosition) obj;
            return _pos.equals(other._pos);
        }
        return false;
    }
}
