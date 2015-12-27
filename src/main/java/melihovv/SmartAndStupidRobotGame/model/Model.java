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
import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;
import melihovv.SmartAndStupidRobotGame.model.navigation.MiddlePosition;

import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * The <code>Model</code> class defines game model.
 */
public class Model {

    private final Field _field;
    private final Target _target;
    static final Logger log = Logger.getLogger(Model.class.getName());

    public Model() {
        _field = new Field(new Dimension(10, 10));
        _target = new Target(_field);
    }

    public void start() {
        generateField();
        identifyGameOver();
        smartRobot().addListener(new SmartRobotObserver());
    }

    private void identifyGameOver() {
        if (smartRobot().pos().equals(_target.pos())) {
            log.info("Smart robot has reached target position");
        }
    }

    private void generateField() {
        _field.addObject(
                new CellPosition(new Point(9, 6)),
                _target
        );
        _field.addObject(
                new CellPosition(new Point(3, 3)),
                new SmartRobot(_field)
        );
        _field.addObject(
                new CellPosition(new Point(4, 3)),
                new Mire(_field)
        );
        _field.addObject(
                new CellPosition(new Point(5, 3)),
                new Mire(_field)
        );
        _field.addObject(
                new CellPosition(new Point(5, 4)),
                new Mire(_field)
        );
        _field.addObject(
                new MiddlePosition(
                        Direction.north(),
                        new CellPosition(new Point(6, 2))
                ),
                new Wall(_field)
        );
        _field.addObject(
                new MiddlePosition(
                        Direction.south(),
                        new CellPosition(new Point(7, 2))
                ),
                new Wall(_field)
        );
        _field.addObject(
                new MiddlePosition(
                        Direction.south(),
                        new CellPosition(new Point(6, 2))
                ),
                new Wall(_field)
        );
        _field.addObject(
                new MiddlePosition(
                        Direction.west(),
                        new CellPosition(new Point(6, 1))
                ),
                new Wall(_field)
        );
        _field.addObject(
                new MiddlePosition(
                        Direction.west(),
                        new CellPosition(new Point(6, 2))
                ),
                new Wall(_field)
        );
        _field.addObject(
                new MiddlePosition(
                        Direction.east(),
                        new CellPosition(new Point(6, 1))
                ),
                new Wall(_field)
        );
        _field.addObject(
                new MiddlePosition(
                        Direction.east(),
                        new CellPosition(new Point(6, 2))
                ),
                new Wall(_field)
        );
    }

    public Field field() {
        return _field;
    }

    public SmartRobot smartRobot() {
        List<FieldObject> objects = _field.objects(SmartRobot.class);
        return objects.isEmpty() ? null : (SmartRobot) objects.get(0);
    }

    public Target target() {
        return _target;
    }

    private class SmartRobotObserver
            implements SmartRobot.SmartRobotActionListener {

        @Override
        public void smartRobotMadeMove(SmartRobot.SmartRobotActionEvent e) {
            log.info("Smart robot made move");
            identifyGameOver();
        }
    }

    public class Target extends FieldObject<CellPosition> {
        public Target(Field field) {
            super(field);
        }
    }
}
