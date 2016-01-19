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

import melihovv.SmartAndStupidRobotGame.model.field.position.MiddlePosition;
import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;

import java.awt.*;
import java.util.Map;

/**
 * The <code>Wall</code> class defines a wall on the field.
 */
public class Wall extends ImmovableObject<MiddlePosition> {

    /**
     * Constructs a wall.
     *
     * @param field A field on which a wall is placed.
     */
    public Wall(final Field field) {
        super(field);
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
        g.setColor(colors.get("wall"));

        Direction dir = _pos.direct();

        if (dir.equals(Direction.north())) {
            g.drawLine(
                    ltc.x + 1,
                    ltc.y,
                    ltc.x + constants.get("cell size") - 1,
                    ltc.y
            );
        } else if (dir.equals(Direction.south())) {
            g.drawLine(
                    ltc.x + 1,
                    ltc.y + constants.get("cell size"),
                    ltc.x + constants.get("cell size") - 1,
                    ltc.y + constants.get("cell size")
            );
        } else if (dir.equals(Direction.west())) {
            g.drawLine(
                    ltc.x,
                    ltc.y + 1,
                    ltc.x,
                    ltc.y + constants.get("cell size") - 1
            );
        } else if (dir.equals(Direction.east())) {
            g.drawLine(
                    ltc.x + constants.get("cell size"),
                    ltc.y + 1,
                    ltc.x + constants.get("cell size"),
                    ltc.y + constants.get("cell size") - 1
            );
        } else {
            throw new IllegalArgumentException(
                    "Direction must be north, south, west or east");
        }

        g.setColor(preserved);
    }
}
