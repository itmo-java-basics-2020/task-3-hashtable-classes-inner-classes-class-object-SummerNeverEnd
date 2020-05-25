package ru.itmo.java;

public class HashTable {

    private static final int DEFAULT_CAPACITY = 1000;
    private static final double DEFAULT_LOAD_FACTOR = 0.5;
    private static final double MULTIPLIER_FOR_HASH_FUNCTION = 0.618;
    private static final int STEP_OF_LINEAR_EXPLORATION = 7;
    private static final Entry DUMMY_ENTRY = new Entry(null, null, true);

    private Entry[] hashTable;
    private int capacity;
    private int size;
    private final double loadFactor;
    private int threshold;


    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    public HashTable(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(double loadFactor) {
        this(DEFAULT_CAPACITY, loadFactor);
    }

    public HashTable(int initialCapacity, double loadFactor) {
        this.capacity = initialCapacity;
        this.loadFactor = Math.max(Math.min(loadFactor, 1), 0);

        size = 0;
        hashTable = new Entry[initialCapacity];

        this.threshold = (int) (this.loadFactor * initialCapacity);
    }

    private int hashCode(Object key) {

        return (int) (capacity * (MULTIPLIER_FOR_HASH_FUNCTION * Math.abs(key.hashCode()) % 1));
    }

    private int findIndexForKey(Object key) {
        int hashIndex = hashCode(key);
        int curIndex = hashIndex;
        int i = 0;
        while (hashTable[curIndex] != null
                && (!(key.equals(hashTable[curIndex].getKey())) || hashTable[curIndex].isDeleted)) {
            i++;
            curIndex = (hashIndex + i * STEP_OF_LINEAR_EXPLORATION) % capacity;
            if(curIndex == hashIndex){
                return -1;
            }
        }
        if (hashTable[curIndex] == null) {
            return -1;
        }

        return curIndex;
    }

    public Object put(Object key, Object value) {
        Entry temp = new Entry(key, value);

        int hashIndex = findIndexForKey(key);

        Object previousValue;

        if (hashIndex != -1) {
            previousValue = hashTable[hashIndex].getValue();
            hashTable[hashIndex] = temp;

            return previousValue;
        }

        int index = hashCode(key);
        int curIndex = index;
        int i = 0;

        while (!(hashTable[curIndex] == null || hashTable[curIndex].isDeleted)) {
            i++;
            curIndex = (index + i * STEP_OF_LINEAR_EXPLORATION) % capacity;
        }

        hashTable[curIndex] = temp;
        size++;
    
        if (size >= threshold) {
            resize();
        }

        return null;
    }

    public Object get(Object key) {

        int hashIndex = findIndexForKey(key);

        if (hashIndex == -1) {
            return null;
        }
        return hashTable[hashIndex].getValue();
    }

    public Object remove(Object key) {

        int index = findIndexForKey(key);
        if (index == -1) {
            return null;
        }

        Object removedValue = hashTable[index].getValue();
        hashTable[index] = DUMMY_ENTRY;
        size--;

        return removedValue;
    }

    public int size() {
        return size;
    }

    private void resize() {

        Entry[] oldArray = hashTable;

        capacity *= 2;
        hashTable = new Entry[capacity];
        threshold = (int) (loadFactor * capacity);
        size = 0;

        for (Entry e : oldArray) {
            if (!(e == null || e.isDeleted)) {
                put(e.getKey(), e.getValue());
            }
        }
    }

    private static class Entry {
        private Object key;
        private Object value;
        boolean isDeleted;

        Entry(Object key, Object value) {
            this(key, value, false);
        }

        Entry(Object key, Object value, boolean deleted) {
            this.key = key;
            this.value = value;
            this.isDeleted = deleted;
        }

        private Object getValue() {
            return value;
        }

        private Object getKey() {
            return key;
        }
    }

}