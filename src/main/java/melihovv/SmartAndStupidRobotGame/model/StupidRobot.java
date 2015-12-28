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
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Logger;

/**
 * The <code>StupidRobot</code> defines stupid robot on the field.
 */
public class StupidRobot extends FieldObject<CellPosition> {

    private final ArrayList<StupidRobotActionListener> _listenerList;
    private final StupidRobotActionEvent _event;
    private int _stepsToSkip = 0;
    static final Logger log = Logger.getLogger(StupidRobot.class.getName());

    public StupidRobot(Field field) {
        super(field);
        _listenerList = new ArrayList<>();
        _event = new StupidRobotActionEvent(this);
    }

    public void makeMove() {
        if (_stepsToSkip != 0) {
            --_stepsToSkip;
            return;
        }

        CellPosition smRobPos = ((SmartRobot) _field.objects(SmartRobot.class)
                .get(0)).pos();

        // Smart robot is in the same cell.
        if (_pos.equals(smRobPos)) {
            fireSmartRobotIsCatched();
            return;
        }

        // Check if smart robot is near.
        boolean isSmartRobotNear =
                _pos.next(Direction.north()).equals(smRobPos) ||
                        _pos.next(Direction.south()).equals(smRobPos) ||
                        _pos.next(Direction.east()).equals(smRobPos) ||
                        _pos.next(Direction.east()).next(Direction.north())
                                .equals(smRobPos) ||
                        _pos.next(Direction.east()).next(Direction.south())
                                .equals(smRobPos) ||
                        _pos.next(Direction.west()).equals(smRobPos) ||
                        _pos.next(Direction.west()).next(Direction.north())
                                .equals(smRobPos) ||
                        _pos.next(Direction.west()).next(Direction.south())
                                .equals(smRobPos);
        log.info("Is smart robot near? " + isSmartRobotNear);

        if (isSmartRobotNear) {

            // Robots are in the same column.
            if (smRobPos.pos().getX() == _pos.pos().getX()) {
                List<FieldObject> nearWall = null;
                List<FieldObject> nearWall2 = null;

                // Smart robot is on the top.
                if (smRobPos.pos().getY() < _pos.pos().getY()) {
                    nearWall = _field.objects(
                            Wall.class,
                            new MiddlePosition(Direction.north(), _pos)
                    );
                    nearWall2 = _field.objects(
                            Wall.class,
                            new MiddlePosition(
                                    Direction.south(),
                                    _pos.next(Direction.north())
                            )
                    );
                } else if (smRobPos.pos().getY() > _pos.pos().getY()) {
                    nearWall = _field.objects(
                            Wall.class,
                            new MiddlePosition(Direction.south(), _pos)
                    );
                    nearWall2 = _field.objects(
                            Wall.class,
                            new MiddlePosition(
                                    Direction.north(),
                                    _pos.next(Direction.south())
                            )
                    );
                }

                if (nearWall.isEmpty() &&
                        nearWall2.isEmpty()) {

                    setPos(smRobPos);
                    fireSmartRobotIsCatched();
                    return;
                }
            } else if (smRobPos.pos().getY() == _pos.pos().getY()) {
                // Robots are in the same row.
                List<FieldObject> nearWall = null;
                List<FieldObject> nearWall2 = null;

                // Smart robot is on the left.
                if (smRobPos.pos().getX() < _pos.pos().getX()) {
                    nearWall = _field.objects(
                            Wall.class,
                            new MiddlePosition(Direction.west(), _pos)
                    );
                    nearWall2 = _field.objects(
                            Wall.class,
                            new MiddlePosition(
                                    Direction.east(),
                                    _pos.next(Direction.west())
                            )
                    );
                } else if (smRobPos.pos().getX() > _pos.pos().getX()) {
                    nearWall = _field.objects(
                            Wall.class,
                            new MiddlePosition(Direction.east(), _pos)
                    );
                    nearWall2 = _field.objects(
                            Wall.class,
                            new MiddlePosition(
                                    Direction.west(),
                                    _pos.next(Direction.east())
                            )
                    );
                }

                if (nearWall.isEmpty() &&
                        nearWall2.isEmpty()) {

                    setPos(smRobPos);
                    fireSmartRobotIsCatched();
                    return;
                }
            } else {
                // The smart robot is diagonally across from the stupid one.

                // TODO remove duplicated code.
                // Smart robot is on the left.
                if (smRobPos.pos().getX() < _pos.pos().getX()) {
                } else {
                    // Smart robot is on the right.
                }
            }
        } else {
            Direction dir = null;

            // Robots are in the same column.
            if (smRobPos.pos().getX() == _pos.pos().getX()) {
                // Smart robot is on the top.
                if (smRobPos.pos().getY() < _pos.pos().getY()) {
                    dir = Direction.north();
                } else {
                    dir = Direction.south();
                }
            } else {
                // Come closer horizontally.
                // Smart robot is on the right.
                if (smRobPos.pos().getX() > _pos.pos().getX()) {
                    dir = Direction.east();
                } else {
                    dir = Direction.west();
                }
            }

            if (dir != null) {
                if (isMovePossible(dir)) {
                    if (setPos(_pos.next(dir))) {
                        for (FieldObject mire : _field.objects(Mire.class)) {
                            if (_pos.equals(mire.pos())) {
                                log.info("Stupid robot in mire, skip 3 steps");
                                _stepsToSkip = 3;
                                break;
                            }
                        }

                        fireRobotAction();
                    }
                }
            }
        }
    }

    private boolean isMovePossible(Direction dir) {
        List<FieldObject> objs = _field.objects(Wall.class,
                new MiddlePosition(dir.opposite(), _pos.next(dir)));
        List<FieldObject> objs2 = _field.objects(Wall.class,
                new MiddlePosition(dir, _pos));
        if (!objs.isEmpty() || !objs2.isEmpty()) {
            return false;
        }

        Point nextPos = _pos.next(dir).pos();
        boolean isPosValid = _field.contains(nextPos);
        return isPosValid;
    }

    @Override
    public boolean setPos(CellPosition pos) {
        if (pos != null) {
            _pos = pos;
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Events.
    ////////////////////////////////////////////////////////////////////////////

    /**
     * The <code>StupidRobotActionEvent</code> defines event of stupid robot.
     */
    public class StupidRobotActionEvent extends EventObject {
        public StupidRobotActionEvent(Object source) {
            super(source);
        }
    }

    public interface StupidRobotActionListener extends EventListener {
        void stupidRobotMadeMove(StupidRobotActionEvent e);

        void smartRobotIsCatched(StupidRobotActionEvent e);
    }

    public void addListener(StupidRobotActionListener l) {
        _listenerList.add(l);
    }

    public void removeListener(StupidRobotActionListener l) {
        _listenerList.remove(l);
    }

    public void clearListeners() {
        _listenerList.clear();
    }

    protected void fireRobotAction() {
        for (Object listener : _listenerList) {
            ((StupidRobotActionListener) listener).stupidRobotMadeMove(_event);
        }
    }

    protected void fireSmartRobotIsCatched() {
        for (Object listener : _listenerList) {
            ((StupidRobotActionListener) listener).smartRobotIsCatched(_event);
        }
    }
}
