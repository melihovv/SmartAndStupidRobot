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

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The <code>Field</code> class defines game field. It stores all object that
 * can be placed on it.
 */
public class Field {

    // Field objects.
    private HashMap<Class, List<FieldObject>> _objs;
    // Field dimension.
    private Dimension _dim;

    /**
     * Construct new <code>Dimension</code>.
     *
     * @param dimension
     * @throws IllegalArgumentException If <code>dimension</code> isn't
     *                                  positive.
     */
    public Field(Dimension dimension) throws IllegalArgumentException {
        setSize(dimension);
        _objs = new HashMap<>();
    }

    /**
     * Set field size.
     *
     * @param dimension
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
     * Add object to field.
     *
     * @param pos
     * @param obj
     * @return True if object was added, otherwise false.
     */
    public <Position> boolean addObject(Position pos,
                                        FieldObject<Position> obj) {
        Class objClass = obj.getClass();

        if (obj.setPos(pos)) {
            if (_objs.containsKey(objClass)) {
                _objs.get(objClass).add(obj);
            } else {
                List<FieldObject> objList = new ArrayList<>();
                objList.add(obj);
                _objs.put(objClass, objList);
            }
            return true;
        }

        return false;
    }

    /**
     * Remove object from field.
     *
     * @param obj
     * @return True if such object was on field and it was deleted, otherwise
     * false.
     */
    public <Position> boolean removeObject(FieldObject<Position> obj) {
        boolean success = false;
        Class objClass = obj.getClass();

        if (_objs.containsKey(objClass)) {
            success = _objs.get(objClass).remove(obj);

            if (success) {
                obj.setPos(null);
            }
        }

        return success;
    }

    /**
     * Get all field objects.
     *
     * @return
     */
    public List<FieldObject> objects() {
        List<FieldObject> objsList = new ArrayList<>();

        for (Map.Entry<Class, List<FieldObject>> entry : _objs.entrySet()) {
            objsList.addAll(entry.getValue());
        }

        return objsList;
    }

    /**
     * Get all <code>objType</code> field objects.
     *
     * @param objType
     * @return
     */
    public List<FieldObject> objects(Class objType) {
        List<FieldObject> objList = new ArrayList<>();

        if (_objs.containsKey(objType)) {
            objList.addAll(_objs.get(objType));
        }

        return objList;
    }

    /**
     * Get object with position <code>pos</code>.
     *
     * @param pos
     * @return List of objects which are on position <code>pos</code>.
     */
    public <Position> List<FieldObject> objects(Position pos) {
        List<FieldObject> objsList = new ArrayList<>();

        for (Map.Entry<Class, List<FieldObject>> entry : _objs.entrySet()) {
            objsList.addAll(entry.getValue().stream().filter(
                    obj -> obj.pos().equals(pos)).collect(Collectors.toList()));
        }

        return objsList;
    }

    public <Position> List<FieldObject> objects(Class objType, Position pos) {
        List<FieldObject> objList = new ArrayList<>();

        if (_objs.containsKey(objType)) {
            objList.addAll(_objs.get(objType).stream().filter(
                    obj -> obj.pos().equals(pos)).collect(Collectors.toList()));
        }

        return objList;
    }

    /**
     * Check if position <code>pos</code> is free.
     *
     * @param pos
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
     * Clear game field.
     */
    public void clear() {
        _objs.clear();
    }

    /**
     * Returns true if <code>pos</code> belongs to field, otherwise — false.
     * @param pos
     * @return
     */
    public boolean contains(Point pos) {
        return pos.getX() >= 1 && pos.getX() <= _dim.getWidth() &&
                pos.getY() >= 1 && pos.getY() <= _dim.getHeight();
    }
}
