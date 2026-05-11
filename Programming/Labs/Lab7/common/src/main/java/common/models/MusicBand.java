package common.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

/**
 * This class represents the complete Music Band, it is the primary model class.
 */
public class MusicBand implements Comparable<MusicBand>, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private Coordinates coordinates;
    private java.util.Date creationDate;
    private Long numberOfParticipants;
    private Integer singlesCount;
    private LocalDate establishmentDate;
    private MusicGenre genre;
    private Studio studio;
    private String ownerLogin;

    private static long nextId = 1;

    /**
     * Constructs a Music Band object.
     *
     * @param name Is the name of the band.
     * @param coordinates Is the coordinates of the band.
     * @param numberOfParticipants Is the number of participants of the band.
     * @param singlesCount Is the count of single releases of the band.
     * @param establishmentDate Is the date when the band was established.
     * @param genre Is the Music Genre of the band.
     * @param studio Is the operating studio of the band.
     */
    public MusicBand(String name, Coordinates coordinates, Long numberOfParticipants,
                     Integer singlesCount, java.time.LocalDate establishmentDate,
                     MusicGenre genre, Studio studio) {
        this.id = generateId();
        setName(name);
        setCoordinates(coordinates);
        setCreationDate();
        setNumberOfParticipants(numberOfParticipants);
        setSinglesCount(singlesCount);
        setEstablishmentDate(establishmentDate);
        setMusicGenre(genre);
        setStudio(studio);
    }

    /**
     * Generates a unique id for the band, this method belongs to the class.
     *
     * @return the id as a long.
     */
    private static long generateId() {
        return nextId++;
    }

    public void setId(long id) {this.id = id;}

    /**
     * Sets the id for the next band after reading the XML file.
     * @param maxId the highest id of the loaded XML file.
     */
    public static void setNextId(long maxId) {
        nextId = maxId + 1;
    }

    /**
     * Setter method for the name of the music band.
     * @param name  the new name of the music and.
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("The name cannot be null or empty");
        }
        this.name = name;
    }


    /**
     * Setter method for the coordinates of the music band.
     * @param coordinates the new coordinates of the music band.
     */
    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        this.coordinates = coordinates;
    }

    /**
     * Setter method for the creation date of the music band. (auto generated)
     */
    private void setCreationDate() {
        this.creationDate = new Date();
    }

    public void setCreationDate(Date creationDate) {
        if (creationDate == null) {
            throw new IllegalArgumentException("Creation date cannot be null");
        }
        this.creationDate = creationDate;
    }

    /**
     * Setter method for the number of members of the music band.
     * @param numberOfParticipants the new number of participants of the music band.
     */
    public void setNumberOfParticipants(Long numberOfParticipants) {
        if (numberOfParticipants == null || numberOfParticipants <= 0) {
            throw new IllegalArgumentException("Number of participants cannot be null or less than 0");
        }
        this.numberOfParticipants = numberOfParticipants;
    }

    /**
     * Setter method for the number of singles of the music band.
     * @param singlesCount the new count for the singles of the music band.
     */
    public void setSinglesCount(Integer singlesCount) {
        if (singlesCount != null && singlesCount <= 0) {
            throw new IllegalArgumentException("Singles count must be greater than 0");
        }
        this.singlesCount = singlesCount;
    }

    /**
     * Setter method for the establishment date of the music band.
     * @param establishmentDate the new date of establishment.
     */
    public void setEstablishmentDate(java.time.LocalDate establishmentDate) {
        this.establishmentDate = establishmentDate;
    }

    /**
     * Setter method for the music genre of the band.
     * @param genre  the new genre of the music band.
     */
    public void setMusicGenre(MusicGenre genre) {
        this.genre = genre;
    }

    /**
     * Setter method for the studio of the music band.
     * @param studio  the new studio of the music band.
     */
    public void setStudio(Studio studio) {
        this.studio = studio;
    }

    /**
     * Setter method for the owner login of the music band.
     * @param ownerLogin  the new owner login of the music band.
     */
    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    /**
     * Getter method for the name of the music band.
     * @return the name of the music band as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter method for the id of the music band.
     * @return the id of the music band as a long.
     */
    public long getId() {
        return id;
    }

    /**
     * Getter method for the coordinates of the music band.
     * @return the coordinates of the music band.
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Getter method for the date when the music band was added to the collection.
     * @return the date as {@link Date}.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Getter method for the number of members of the music band.
     * @return the date as {@link Long}.
     */
    public Long getNumberOfParticipants() {
        return numberOfParticipants;
    }

    /**
     * Getter method for the count of single release of the music band.
     * @return the count of single releases as an {@link Integer}.
     */
    public Integer getSinglesCount() {
        return singlesCount;
    }


    /**
     * Getter method for the establishment date of the music band.
     * @return the date of establishment of the music band as {@link LocalDate}.
     */
    public LocalDate getEstablishmentDate() {
        return establishmentDate;
    }


    /**
     * Getter method for the music genre of the music band.
     * @return the music genre as {@link MusicGenre}.
     */
    public MusicGenre getGenre() {
        return genre;
    }


    /**
     * Getter method for the studio of the music band.
     * @return the studio of the music band as {@link Studio}.
     */
    public Studio getStudio() {
        return studio;
    }

    /**
     * Getter method for the owner login of the music band.
     * @return the owner login of the music band as a String.
     */
    public String getOwnerLogin() {
        return ownerLogin;
    }

    /**
     * Getter method for the next id of a music band to be created, this method belongs to the class.
     * @return the next id of a band as a long.
     */
    public static long getNextId() {
        return nextId;
    }

    /**
     * Creates a fresh server-managed instance to ensure auto-generated fields
     * (id and creationDate) are assigned on the server side.
     */
    public static MusicBand createServerManagedCopy(MusicBand source) {
        if (source == null) {
            throw new IllegalArgumentException("Source band cannot be null");
        }

        Studio studioCopy = source.getStudio() == null ? null : new Studio(source.getStudio().getName());
        Coordinates coordinatesCopy = new Coordinates(source.getCoordinates().getX(), source.getCoordinates().getY());

        return new MusicBand(
                source.getName(),
                coordinatesCopy,
                source.getNumberOfParticipants(),
                source.getSinglesCount(),
                source.getEstablishmentDate(),
                source.getGenre(),
                studioCopy
        );
    }

    /**
     * A custom implementation of the toString() method of a music band.
     * @return the string built with the help of the {@link StringBuilder} class.
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("\nMusic Band id: ").append(this.id)
        .append("\n" + "Name: ").append(this.name)
        .append("\n" + "Music Genre: ").append(this.genre)
        .append("\n" + "Studio: ").append(this.studio)
        .append("\n" + "Number of Participants: ").append(this.numberOfParticipants)
        .append("\n" + "Count of Singles: ").append(this.singlesCount)
        .append("\n" + "Coordinates: ").append(this.coordinates)
        .append("\n" + "Establishment Date: ").append(this.establishmentDate)
        .append("\n" + "Added to Collection on: ").append(this.creationDate);

        if (this.ownerLogin != null) {
            output.append("\n" + "Owner: ").append(this.ownerLogin);
        }

        return output.toString();
    }


    /**
     * A custom implementation of the equals method of a music band.
     * @param o   the reference object with which to compare.
     * @return whether the two objects were equal internally.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof MusicBand band) {
            return band.getId() == this.id;
        }
        return false;
    }

    /**
     * A custom implementation of the hashCode() method of a music band.
     * @return the hash code as an int.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * A custom implementation of the compareTo method of the {@link Comparable} interface.
     * @param o the object to be compared.
     * @return a positive number if "this" object comes before, a negative number if after, and 0 if equal.
     */
    @Override
    public int compareTo(MusicBand o) {
        return Long.compare(this.id, o.getId());
    }
}

