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

package melihovv.SmartAndStupidRobotGame.model.seasons.downfall;

import melihovv.SmartAndStupidRobotGame.model.field.Field;
import melihovv.SmartAndStupidRobotGame.model.field.FieldObject;
import melihovv.SmartAndStupidRobotGame.model.field.Mire;
import melihovv.SmartAndStupidRobotGame.model.field.StupidRobot;
import melihovv.SmartAndStupidRobotGame.model.field.position.CellPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>Rain</code> class defines rain.
 */
public class Rain extends Downfall {

    // New mires which are created when it is raining.
    private List<Mire> _mires = new ArrayList<>();

    /**
     * Influences on the field and on the field objects.
     *
     * @param field The game field.
     */
    @Override
    public void influence(final Field field) {
        for (FieldObject mire : field.objects(Mire.class)) {
            CellPosition pos = field.freeCellAround(
                    ((Mire) mire).pos(),
                    Mire.class
            );

            if (pos != null) {
                Mire newMire = new Mire(field);
                newMire.setPos(pos);
                field.addObject(pos, newMire);
                _mires.add(newMire);
            }
        }
        ((StupidRobot) field.object(StupidRobot.class)).checkIfRobotIsInMire();
    }

    /**
     * Cleans result of influence on the field and on the field objects.
     *
     * @param field The game field.
     */
    @Override
    public void cleanInfluence(final Field field) {
        _mires.forEach(field::removeObject);
        ((StupidRobot) field.object(StupidRobot.class)).checkIfRobotIsInMire();
    }
}
