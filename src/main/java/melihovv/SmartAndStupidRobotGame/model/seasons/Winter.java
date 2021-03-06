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

package melihovv.SmartAndStupidRobotGame.model.seasons;

import melihovv.SmartAndStupidRobotGame.model.field.Field;
import melihovv.SmartAndStupidRobotGame.model.field.FieldObject;
import melihovv.SmartAndStupidRobotGame.model.field.Mire;
import melihovv.SmartAndStupidRobotGame.model.field.StupidRobot;
import melihovv.SmartAndStupidRobotGame.model.seasons.downfall.Downfall;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The <code>Winter</code> class defines the winter season.
 */
public class Winter extends Season {

    // Colors to draw field objects.
    private static Map<String, Color> _colors =
            new HashMap<String, Color>() {{
                put("field", new Color(83, 201, 239));
                put("grid", Color.BLUE);
                put("font", Color.RED);
                put("mire", Color.WHITE);
                put("wall", Color.BLACK);
            }};

    /**
     * Constructs winter season.
     *
     * @param temperature Temperature.
     * @param downfall    Downfall.
     */
    public Winter(final int temperature, final List<Downfall> downfall) {
        super(temperature, downfall);
    }

    /**
     * Influences on the field and on the field objects.
     *
     * Freezes all mires on the field.
     *
     * @param field The game field.
     */
    @Override
    public void influence(final Field field) {
        for (FieldObject mire : field.objects(Mire.class)) {
            ((Mire) mire).freeze();
        }
        ((StupidRobot) field.object(StupidRobot.class)).checkIfRobotIsInMire();
    }

    /**
     * Cleans result of influence on the field and on the field objects.
     *
     * Unfreezes all mires on the field.
     *
     * @param field The game field.
     */
    @Override
    public void cleanInfluence(final Field field) {
        for (FieldObject mire : field.objects(Mire.class)) {
            ((Mire) mire).unfreeze();
        }
        ((StupidRobot) field.object(StupidRobot.class)).checkIfRobotIsInMire();
    }

    /**
     * Returns colors for field objects draw.
     *
     * @return Colors for field objects draw.
     */
    @Override
    public Map<String, Color> colors() {
        return _colors;
    }
}
