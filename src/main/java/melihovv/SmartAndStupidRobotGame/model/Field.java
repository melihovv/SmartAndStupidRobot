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

import melihovv.SmartAndStupidRobotGame.model.navigation.MiddlePosition;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The <code>Field</code> class defines game field. It stores all object that
 * can be placed on it.
 */
public class Field {

    // Field objects.
    private final HashMap<Class, List<FieldObject>> _objs;
    // Field dimension.
    private Dimension _dim;
    // Logger.
    private static final Logger log = Logger.getLogger(Field.class.getName());

    /**
     * Constructs new <code>Dimension</code> with dimension
     * <code>dimension</code>.
     *
     * @param dimension The dimension of the field.
     * @throws IllegalArgumentException If <code>dimension</code> isn't
     *                                  positive.
     */
    public Field(Dimension dimension) throws IllegalArgumentException {
        setSize(dimension);
        _objs = new HashMap<>();
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
     * @throws IllegalArgumentException If <code>dimension</code> isn't
     *                                  positive.
     */
    public void setSize(Dimension dimension) throws IllegalArgumentException {
        if (dimension.getWidth() <= 0 || dimension.getHeight() <= 0) {
            throw new IllegalArgumentException(
                    "Field size must be positive");
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
    public <Position> boolean addObject(Position pos,
                                        FieldObject<Position> obj) {
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
    public <Position> boolean removeObject(FieldObject<Position> obj) {
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
    public List<FieldObject> objects(Class objType) {
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
    public <Position> List<FieldObject> objects(Position pos) {
        final List<FieldObject> objsList = new ArrayList<>();

        for (Map.Entry<Class, List<FieldObject>> entry : _objs.entrySet()) {
            objsList.addAll(entry.getValue().stream().filter(
                    obj -> obj.pos().equals(pos)).collect(Collectors.toList()));
        }

        return objsList;
    }

    /**
     * Returns objects with type <code>objType</code> and position <code>pos</code>.
     *
     * @param objType Type of objects.
     * @param pos     Position on which objects will be returned.
     * @return List of objects of type <code>objType</code> which are on
     * position <code>pos</code>.
     */
    public <Position> List<FieldObject> objects(Class objType, Position pos) {
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
    public List<FieldObject> objects(Class objType,
                                     MiddlePosition pos) {

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
     * Checks if position <code>pos</code> is free.
     *
     * @param pos Position to check.
     * @return True if position is free, otherwise — false.
     */
    public <Position> boolean isPosFree(Position pos) {
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
    public boolean contains(Point pos) {
        return pos.getX() >= 1 && pos.getX() <= _dim.getWidth() &&
                pos.getY() >= 1 && pos.getY() <= _dim.getHeight();
    }
}
