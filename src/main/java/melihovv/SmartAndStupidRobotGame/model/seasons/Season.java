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
import melihovv.SmartAndStupidRobotGame.model.seasons.downfall.Downfall;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * The abstract <code>Season</code> class defines the abstract season.
 */
public abstract class Season {

    // Temperature.
    private final int _temperature;
    // Downfall.
    private final List<Downfall> _downfall;

    /**
     * Constructs season.
     *
     * @param temperature Temperature.
     * @param downfall    Downfall.
     */
    public Season(final int temperature, final List<Downfall> downfall) {
        _temperature = temperature;
        _downfall = downfall;
    }

    /**
     * Influences on the field and on the field objects.
     *
     * @param field The game field.
     */
    public abstract void influence(final Field field);

    /**
     * Cleans result of influence on the field and on the field objects.
     *
     * @param field The game field.
     */
    public abstract void cleanInfluence(final Field field);

    /**
     * Returns temperature.
     *
     * @return Temperature.
     */
    public int temperature() {
        return _temperature;
    }

    /**
     * Returns downfall.
     *
     * @return Downfall.
     */
    public List<Downfall> downfall() {
        return _downfall;
    }

    /**
     * Returns colors for field objects draw.
     *
     * @return Colors for field objects draw.
     */
    public abstract Map<String, Color> colors();
}
