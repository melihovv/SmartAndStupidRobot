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

/**
 * The <code>Winter</code> class defines the winter season.
 */
public class Winter extends Season {

    /**
     * Constructs winter season.
     *
     * @param name        Season name.
     * @param temperature Temperature.
     * @param downfall    Downfall.
     */
    public Winter(String name, int temperature, String downfall) {
        super(name, temperature, downfall);
    }

    /**
     * Influences on the field and on the field objects.
     *
     * Freezes all mires on the field.
     *
     * @param field The game field.
     */
    @Override
    public void influence(Field field) {
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
    public void cleanInfluence(Field field) {
        for (FieldObject mire : field.objects(Mire.class)) {
            ((Mire) mire).unfreeze();
        }
        ((StupidRobot) field.object(StupidRobot.class)).checkIfRobotIsInMire();
    }
}
