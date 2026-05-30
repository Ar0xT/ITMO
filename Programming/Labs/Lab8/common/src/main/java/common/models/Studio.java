package common.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class represents a studio of a music band.
 */
public class Studio implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    /**
     * Creates a new Studio with the given name.
     * @param name the Studio's name.
     */
    public Studio(String name) {
        this.name = name;
    }

    /**
     * This method is used for retrieving the name of the studio.
     * @return name of the studio.
     */
    public String getName() {
        return this.name;
    }

    /**
     * This method is used for setting a new name for the studio.
     * @param name the new name for the studio.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * A custom implementation of the toString() method.
     *
     * @return value of the studio's name.
     */
    @Override
    public String toString() {
        return "Name: " + this.name;
    }

    /**
     * A custom implementation of the hashCode() method.
     *
     * @return hash code of the studio's name.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * A custom implementation of the equals() method.
     * @param o   the reference object with which to compare.
     * @return whether the two objects were equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Studio studio = (Studio) o;
        return Objects.equals(this.name, studio.getName());
    }
}

