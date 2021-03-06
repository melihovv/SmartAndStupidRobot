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

package melihovv.SmartAndStupidRobotGame.model.navigation;

/**
 * The <code>Direction</code> class defines the direction in a coordinate system
 * north-east-south-west.
 * It allows to compare directions and to create a new direction relative to the
 * current.
 */
public class Direction {

    // The angle corresponding to the direction.
    private int _angle = 0;

    /**
     * Constructs the new <code>Direction</code>.
     *
     * @param angle The angle corresponding to the direction (0 degree - is the
     *              north direction).
     */
    private Direction(final int angle) {
        int copy = angle % 360;
        if (copy < 0) {
            copy += 360;
        }

        _angle = copy;
    }

    /**
     * Returns north direction.
     *
     * @return The north <code>Direction</code>.
     */
    public static Direction north() {
        return new Direction(0);
    }

    /**
     * Returns south direction.
     *
     * @return The south <code>Direction</code>.
     */
    public static Direction south() {
        return new Direction(180);
    }

    /**
     * Returns east direction.
     *
     * @return The east <code>Direction</code>.
     */
    public static Direction east() {
        return new Direction(270);
    }

    /**
     * Returns west direction.
     *
     * @return The west <code>Direction</code>.
     */
    public static Direction west() {
        return new Direction(90);
    }

    /**
     * Returns the new <code>Direction</code> rotated 45 degrees clockwise.
     *
     * @return The new <code>Direction</code> rotated 45 degrees clockwise.
     */
    public Direction clockwise() {
        return new Direction(this._angle - 90);
    }

    /**
     * Returns the new <code>Direction</code> rotated 45 degrees anticlockwise.
     *
     * @return The new <code>Direction</code> rotated 45 degrees anticlockwise.
     */
    public Direction anticlockwise() {
        return new Direction(this._angle + 90);
    }

    /**
     * Returns the new <code>Direction</code> rotated 180 degrees.
     *
     * @return The new <code>Direction</code> rotated 180 degrees.
     */
    public Direction opposite() {
        return new Direction(this._angle + 180);
    }

    /**
     * Checks if <code>other</code> direction is opposite of current.
     *
     * @param other Direction to check.
     * @return Result of checking.
     */
    public boolean isOpposite(final Direction other) {
        return this.opposite().equals(other);
    }

    /**
     * Checks if <code>other</code> equals current direction. They are equal if
     * the <code>other</code> object is instance of <code>Direction</code> and
     * both have the same direction.
     *
     * @param other Any object.
     * @return Result of checking.
     */
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Direction) {
            Direction otherDirect = (Direction) other;
            return _angle == otherDirect._angle;
        }

        return false;
    }

    /**
     * Returns hash code of the <code>Direction</code> instance.
     *
     * @return Hash code of the <code>Direction</code> instance.
     */
    @Override
    public int hashCode() {
        return _angle;
    }
}
