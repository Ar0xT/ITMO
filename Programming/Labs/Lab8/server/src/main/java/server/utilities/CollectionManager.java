package server.utilities;

import common.models.MusicBand;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class is used for managing a Hashtable.
 */
public class CollectionManager {
    private final ConcurrentHashMap<String, MusicBand> collection;
    private final Date initializationDate;

    /**
     * Constructs a collection manager that initializes the private fields.
     */
    public CollectionManager() {
        this.collection = new ConcurrentHashMap<>();
        this.initializationDate = new Date();
    }

    /**
     * This method is used for putting a key-value pair to the Hashtable.
     * @param key  they key of the Hashtable.
     * @param band  the value of the Hashtable as {@link MusicBand}.
     */
    public void add(String key, MusicBand band) {
        this.collection.put(key, band);
    }

    /**
     * This method is used to retrieve a music band that matches the key of the collection.
     * @param key  the key to retrieve a specific music band.
     * @return  the music band that is found, returns null if not found.
     */
    public MusicBand get(String key) {
        return this.collection.get(key);
    }

    /**
     * This method is used to remove a certain music band from the collection.
     * @param key  the key that is tied to the music band to be removed.
     */
    public void remove(String key) {
        this.collection.remove(key);
    }

    /**
     * This method is used to empty the collection.
     */
    public synchronized void clear() {
        this.collection.clear();
    }

    /**
     * This method is used to get all the keys of the collection.
     * @return  the keys as a {@link Set<String>}.
     */
    public Set<String> getKeys() {
        return this.collection.keySet();
    }


    /**
     * This method is used to get all the music bands from the collection.
     * @return  all music bands as {@link Collection<MusicBand>}.
     */
    public Collection<MusicBand> getAll() {
        return this.collection.values();
    }


    /**
     * This method is used to get the size of the collection.
     * @return  the size of the collection as int.
     */
    public int size() {
        return this.collection.size();
    }

    /**
     * This method is used to get the date the collection was created.
     * @return  the date of creation.
     */
    public Date getInitializationDate() {
        return this.initializationDate;
    }

    /**
     * This method is used to get the type of the collection.
     * @return  the type of the collection.
     */
    public String getCollectionType() {
        return this.collection.getClass().getSimpleName();
    }

    /**
     * This method is used to get the collection itself.
     * @return  the collection.
     */
    public synchronized Hashtable<String, MusicBand> getCollection() {
        return new Hashtable<>(this.collection);
    }

    public synchronized void replaceAll(Map<String, MusicBand> replacement) {
        this.collection.clear();
        this.collection.putAll(replacement);
    }

    public List<Map.Entry<String, MusicBand>> snapshotEntries() {
        return new ArrayList<>(this.collection.entrySet());
    }

    public int removeKeys(Collection<String> keys) {
        int removed = 0;
        for (String key : keys) {
            if (this.collection.remove(key) != null) {
                removed++;
            }
        }
        return removed;
    }

    public List<Map.Entry<String, MusicBand>> getEntriesSortedByBandName() {
        return this.collection.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().getName(), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /**
     * This method is used to check if this collection contains a specific key.
     * @param key  they key to check.
     * @return  true if it exists, otherwise false.
     */
    public boolean containsKey(String key) {
        return this.collection.containsKey(key);
    }

    public String findKeyByBandId(long id) {
        for (Map.Entry<String, MusicBand> entry : this.collection.entrySet()) {
            if (entry.getValue().getId() == id) {
                return entry.getKey();
            }
        }
        return null;
    }

    public int removeIf(Predicate<Map.Entry<String, MusicBand>> predicate) {
        List<String> keysToRemove = this.collection.entrySet().stream()
                .filter(predicate)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        keysToRemove.forEach(this.collection::remove);
        return keysToRemove.size();
    }

    public int removeBandsGreaterThan(MusicBand referenceBand) {
        return removeIf(entry -> entry.getValue().compareTo(referenceBand) > 0);
    }

    public int removeBandsLowerThan(MusicBand referenceBand) {
        return removeIf(entry -> entry.getValue().compareTo(referenceBand) < 0);
    }

    public int removeKeysGreaterThan(String key) {
        return removeIf(entry -> entry.getKey().compareTo(key) > 0);
    }

    public boolean replaceBandById(long id, MusicBand replacement) {
        for (Map.Entry<String, MusicBand> entry : this.collection.entrySet()) {
            if (entry.getValue().getId() == id) {
                MusicBand existing = entry.getValue();
                replacement.setId(existing.getId());
                replacement.setCreationDate(existing.getCreationDate());
                replacement.setOwnerLogin(existing.getOwnerLogin());
                this.collection.put(entry.getKey(), replacement);
                return true;
            }
        }
        return false;
    }


    /**
     * Returns all bands with the given number of participants.
     *
     * @param count  the number of participants to filter by.
     * @return The list of music band objects matching the parameter count.
     */
    public List<MusicBand> getBandsByParticipants(long count) {
        return this.collection.values().stream()
                .filter(band -> band.getNumberOfParticipants() == count)
                .collect(Collectors.toList());
    }

    /**
     * Return all non-null singles count in descending order.
     * @return  The list of Integers in descending order.
     */

    public List<Integer> getAllSinglesCountDescending() {
        return this.collection.values().stream()
                .map(MusicBand::getSinglesCount)
                .filter(Objects::nonNull)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

    }

    /**
     * Groups bands by name and returns how many bands had the same name.
     *
     * @return Map<String, Long> where String is the name of the band and Long is the count.
     */
    public Map<String, Long> countByName() {
        return this.collection.values().stream()
                .collect(Collectors.groupingBy(
                        MusicBand::getName,
                        Collectors.counting()
                ));

    }

    /**
     * Finds a band by its id.
     * @param id The id to search for.
     * @return the music band that was found, null if not found.
     */
    public MusicBand getBandById(long id) {
        return this.collection.values().stream()
                .filter(band -> band.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * A custom implementation of the toString() method.
     * @return the data of the collection as String.
     */
    @Override
    public String toString() {
        if (this.collection.isEmpty()) {
            return "The collection is empty.";
        }
        return getEntriesSortedByBandName().stream()
                .map(entry -> "Key: " + entry.getKey() + "\n" + entry.getValue() + "\n" + "=".repeat(100))
                .collect(Collectors.joining("\n"));
    }


}
