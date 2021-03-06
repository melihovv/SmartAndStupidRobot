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

import melihovv.SmartAndStupidRobotGame.model.field.position.CellPosition;
import melihovv.SmartAndStupidRobotGame.model.field.position.MiddlePosition;
import melihovv.SmartAndStupidRobotGame.model.navigation.Direction;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The <code>Field</code> class defines game field. It stores all object that
 * can be placed on it.
 */
public class Field implements CanMoveFieldObject<CellPosition> {

    // Field objects.
    private final Map<Class, List<FieldObject>> _objs;
    // Field dimension.
    private Dimension _dim;
    // Logger.
    private static final Logger log = Logger.getLogger(Field.class.getName());
    // Offsets used in freeCellAround() method.
    private static int[][] _offsets = {
            {-1, -1},
            {0, -1},
            {1, -1},
            {1, 0},
            {1, 1},
            {0, 1},
            {-1, 1},
            {-1, 0},
    };

    /**
     * Constructs new <code>Dimension</code> with dimension
     * <code>dimension</code>.
     *
     * @param dimension The dimension of the field.
     * @throws IllegalArgumentException If <code>dimension</code> isn't
     *                                  positive.
     */
    public Field(final Dimension dimension) throws IllegalArgumentException {
        _objs = new LinkedHashMap<>();
        setSize(dimension);
    }

    /**
     * Returns the dimension of the field.
     *
     * @return The dimension of the field.
     */
    public Dimension size() {
        return _dim;
    }

    /**
     * Returns the width of the field.
     *
     * @return The width of the field.
     */
    public int width() {
        return (int) _dim.getWidth();
    }

    /**
     * Returns the height of the field.
     *
     * @return The height of the field.
     */
    public int height() {
        return (int) _dim.getHeight();
    }

    /**
     * Sets field size.
     *
     * @param dimension The dimension of the field that will be set.
     * @throws IllegalArgumentException If <code>dimension</code> less than 2 or
     *                                  any object became outside the field.
     */
    public void setSize(final Dimension dimension) throws IllegalArgumentException {
        if (dimension.getWidth() < 2 || dimension.getHeight() < 2) {
            throw new IllegalArgumentException(
                    "Field size must be greater or equal 2");
        }

        for (FieldObject obj : objects()) {
            if (obj.pos() instanceof CellPosition) {
                final Point p = ((CellPosition) obj.pos()).pos();
                if (p.x > dimension.getWidth() || p.y > dimension.getHeight()) {
                    throw new IllegalArgumentException(
                            "Invalid dimension");
                }
            } else if (obj.pos() instanceof MiddlePosition) {
                final Point p = ((MiddlePosition) obj.pos()).cellPos().pos();
                if (p.x > dimension.getWidth() || p.y > dimension.getHeight()) {
                    throw new IllegalArgumentException(
                            "Invalid dimension");
                }
            }
        }

        _dim = dimension;
    }

    /**
     * Adds an object to the field.
     *
     * @param pos Position to which an object will be placed.
     * @param obj An object to be added.
     * @return True if object was added, otherwise false.
     */
    public <Position> boolean addObject(final Position pos,
                                        final FieldObject<Position> obj) {
        final Class objClass = obj.getClass();

        if (obj.setPos(pos)) {
            if (_objs.containsKey(objClass)) {
                _objs.get(objClass).add(obj);
            } else {
                List<FieldObject> objList = new ArrayList<>();
                objList.add(obj);
                _objs.put(objClass, objList);
            }
            return true;
        } else {
            log.warning("Cannot set pos " + pos + " to object " + obj);
        }

        return false;
    }

    /**
     * Removes object from the field.
     *
     * @param obj An object to be removed.
     * @return True if such object was on the field and it was deleted,
     * otherwise false.
     */
    public <Position> boolean removeObject(final FieldObject<Position> obj) {
        boolean success = false;
        final Class objClass = obj.getClass();

        if (_objs.containsKey(objClass)) {
            success = _objs.get(objClass).remove(obj);

            if (success) {
                obj.setPos(null);
            }
        }

        return success;
    }

    /**
     * Returns all field objects.
     *
     * @return All objects on the field.
     */
    public List<FieldObject> objects() {
        final List<FieldObject> objsList = new ArrayList<>();

        for (Map.Entry<Class, List<FieldObject>> entry : _objs.entrySet()) {
            objsList.addAll(entry.getValue());
        }

        return objsList;
    }

    /**
     * Returns all <code>objType</code> field objects.
     *
     * @param objType Type of objects.
     * @return All objects of type <code>objType</code>.
     */
    public List<FieldObject> objects(final Class objType) {
        final List<FieldObject> objList = new ArrayList<>();

        if (_objs.containsKey(objType)) {
            objList.addAll(_objs.get(objType));
        }

        return objList;
    }

    /**
     * Returns objects with position <code>pos</code>.
     *
     * @param pos Position on which objects will be returned.
     * @return List of objects which are on position <code>pos</code>.
     */
    public <Position> List<FieldObject> objects(final Position pos) {
        final List<FieldObject> objsList = new ArrayList<>();

        for (Map.Entry<Class, List<FieldObject>> entry : _objs.entrySet()) {
            objsList.addAll(entry.getValue().stream().filter(
                    obj -> obj.pos().equals(pos)).collect(Collectors.toList()));
        }

        return objsList;
    }

    /**
     * Returns objects with type <code>objType</code> and position
     * <code>pos</code>.
     *
     * @param objType Type of objects.
     * @param pos     Position on which objects will be returned.
     * @return List of objects of type <code>objType</code> which are on
     * position <code>pos</code>.
     */
    public <Position> List<FieldObject> objects(final Class objType,
                                                final Position pos) {
        final List<FieldObject> objList = new ArrayList<>();

        if (_objs.containsKey(objType)) {
            objList.addAll(_objs.get(objType).stream().filter(
                    obj -> obj.pos().equals(pos)).collect(Collectors.toList()));
        }

        return objList;
    }

    /**
     * Returns all objects of type <code>objType</code> with MiddlePosition
     * <code>pos</code>.
     *
     * @param objType Type of objects.
     * @param pos     Position on which objects will be returned.
     * @return List of objects of type <code>objType</code> which are on
     * middle position <code>pos</code>.
     */
    public List<FieldObject> objects(final Class<Wall> objType,
                                     final MiddlePosition pos) {

        final List<FieldObject> objList = new ArrayList<>();
        final MiddlePosition oppositePos = new MiddlePosition(
                pos.direct().opposite(),
                pos.cellPos().next(pos.direct())
        );

        if (_objs.containsKey(objType)) {
            objList.addAll(_objs.get(objType).stream().filter(
                    obj -> obj.pos().equals(pos) ||
                            obj.pos().equals(oppositePos)
            ).collect(Collectors.toList()));
        }

        return objList;
    }

    /**
     * Returns first object of type <code>objType</code>.
     *
     * @param objType Type of object to return.
     * @return First object of type <code>objType</code>.
     */
    public FieldObject object(final Class objType) {
        final List<FieldObject> objects = objects(objType);
        return objects.size() != 0 ? objects.get(0) : null;
    }

    /**
     * Checks if position <code>pos</code> is free.
     *
     * @param pos Position to check.
     * @return True if position is free, otherwise — false.
     */
    public <Position> boolean isPosFree(final Position pos) {
        for (Map.Entry<Class, List<FieldObject>> entry : _objs.entrySet()) {
            for (FieldObject obj : entry.getValue()) {
                if (obj.pos().equals(pos)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Clears game field.
     */
    public void clear() {
        _objs.clear();
    }

    /**
     * Returns true if <code>pos</code> belongs to field, otherwise — false.
     *
     * @param pos Position which is checked.
     * @return True if <code>pos</code> belongs to field, otherwise — false.
     */
    public boolean contains(final Point pos) {
        return pos.getX() >= 1 && pos.getX() <= _dim.getWidth() &&
                pos.getY() >= 1 && pos.getY() <= _dim.getHeight();
    }

    /**
     * Move movable object <code>object</code> in the direction of
     * <code>dir</code>.
     *
     * @param object Object to move.
     * @param dir    Direction in which object is moved.
     * @return True if object was moved, otherwise - false.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean move(final MovableObject<CellPosition> object, final Direction dir) {
        List<FieldObject> objects = objects(object.pos().next(dir));

        if (objects.size() != 0 &&
                objects.get(0) instanceof CanMoveFieldObject) {

            return ((CanMoveFieldObject<CellPosition>) objects.get(0))
                    .move(object, dir);
        }

        return object.setPos(object.pos().next(dir));
    }

    /**
     * Returns first cell which is not occupied by object of type
     * <code>type</code> around cell with position <code>pos</code>.
     *
     * @param pos Position of cell.
     * @param type Object type.
     * @return First free cell around cell with position <code>pos</code>.
     */
    public CellPosition freeCellAround(final CellPosition pos, final Class type) {
        for (int[] offset : _offsets) {
            final CellPosition p = new CellPosition(
                    new Point(
                            pos.pos().x + offset[0],
                            pos.pos().y + offset[1]
                    )
            );
            final List<FieldObject> objects = objects(type, p);
            if (objects.size() == 0) {
                return p;
            }
        }
        return null;
    }
}
