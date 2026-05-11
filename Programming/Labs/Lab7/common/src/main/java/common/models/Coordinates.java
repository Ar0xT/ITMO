package common.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class represents a 2-dimensional coordinate.
 */
public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;

    private double x;
    private Integer y;

    /**
     * Creates a new Coordinates with the parameters x and y.
     * @param x  the X coordinate must be greater than -431.
     * @param y the Y coordinate cannot be null.
     */
    public Coordinates(double x, Integer y) {
        setX(x);
        setY(y);
    }


    /**
     * Returns the X coordinate.
     *
     * @return the X coordinate as a double.
     */
    public double getX() {
        return this.x;
    }

    /**
     * Sets the X coordinate.
     *
     * @param x the new X coordinate (must be greater than -431).
     */
    public void setX(double x) {
        if (x <= -431) {
            throw new IllegalArgumentException("X must be greater than -431");
        }
        this.x = x;
    }

    /**
     * Returns the Y coordinate.
     *
     * @return the Y coordinate as an Integer.
     */
    public Integer getY() {
        return y;
    }

    /**
     * Sets the Y coordinate.
     *
     * @param y the new Y coordinate (cannot be null).
     */
    public void setY(Integer y) {
        if (y == null) throw new IllegalArgumentException("Y cannot be null");
        this.y = y;
    }

    /**
     * A custom implementation of the toString() method.
     *
     * @return a string in the format X = {x}, Y = {y}.
     */
    @Override
    public String toString() {
        return "X = " + x +", Y = " + y;
    }

    /**
     * A custom implementation of the hashCode() method.
     *
     * @return the hashcode of the coordinates as an int.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    /**
     * A custom implementation of the equals() method.
     * @param o   the reference object with which to compare.
     * @return whether the two objects are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Coordinates coordinates) {
            return coordinates.getX() == this.x &&
                    Objects.equals(coordinates.getY(), this.y);
        }
        return false;
    }
}

