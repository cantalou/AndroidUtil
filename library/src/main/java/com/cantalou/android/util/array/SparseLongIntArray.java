package com.cantalou.android.util.array;

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * SparseLongArrays map long to int. Unlike a normal array of longs, there can
 * be gaps in the indices. It is intended to be more efficient than using a
 * HashMap to map Longs to Integers.
 */
public class SparseLongIntArray implements Cloneable {

    private long[] mKeys;
    private int[] mValues;
    private int mSize;

    /**
     * Creates a new SparseLongArray containing no mappings.
     */
    public SparseLongIntArray() {
        this(10);
    }

    /**
     * Creates a new SparseLongArray containing no mappings that will not
     * require any additional memory allocation to store the specified number of
     * mappings.
     */
    public SparseLongIntArray(int initialCapacity) {
        initialCapacity = ArrayUtils.idealIntArraySize(initialCapacity);

        mKeys = new long[initialCapacity];
        mValues = new int[initialCapacity];
        mSize = 0;
    }

    @Override
    public SparseLongIntArray clone() {
        SparseLongIntArray clone = null;
        try {
            clone = (SparseLongIntArray) super.clone();
            clone.mKeys = mKeys.clone();
            clone.mValues = mValues.clone();
        } catch (CloneNotSupportedException cnse) {
        /* ignore */
        }
        return clone;
    }

    /**
     * Gets the int mapped from the specified key, or <code>0</code> if no such
     * mapping has been made.
     */
    public int get(long key) {
        return get(key, 0);
    }

    /**
     * Gets the int mapped from the specified key, or the specified value if no
     * such mapping has been made.
     */
    public int get(long key, int valueIfKeyNotFound) {
        int i = binarySearch(mKeys, 0, mSize, key);

        if (i < 0) {
            return valueIfKeyNotFound;
        } else {
            return mValues[i];
        }
    }

    /**
     * Removes the mapping from the specified key, if there was any.
     */
    public void delete(long key) {
        int i = binarySearch(mKeys, 0, mSize, key);

        if (i >= 0) {
            removeAt(i);
        }
    }

    /**
     * Removes the mapping at the given index.
     */
    public void removeAt(int index) {
        System.arraycopy(mKeys, index + 1, mKeys, index, mSize - (index + 1));
        System.arraycopy(mValues, index + 1, mValues, index, mSize - (index + 1));
        mSize--;
    }

    /**
     * Adds a mapping from the specified key to the specified value, replacing
     * the previous mapping from the specified key if there was one.
     */
    public int put(long key, int value) {
        int i = binarySearch(mKeys, 0, mSize, key);

        if (i >= 0) {
            int old = mValues[i];
            mValues[i] = value;
            return old;
        } else {
            i = ~i;

            if (mSize >= mKeys.length) {
                int n = ArrayUtils.idealIntArraySize(mSize + 1);

                long[] nkeys = new long[n];
                int[] nvalues = new int[n];

                // Log.e("SparseLongArray", "grow " + mKeys.length + " to " +
                // n);
                System.arraycopy(mKeys, 0, nkeys, 0, mKeys.length);
                System.arraycopy(mValues, 0, nvalues, 0, mValues.length);

                mKeys = nkeys;
                mValues = nvalues;
            }

            if (mSize - i != 0) {
                // Log.e("SparseLongArray", "move " + (mSize - i));
                System.arraycopy(mKeys, i, mKeys, i + 1, mSize - i);
                System.arraycopy(mValues, i, mValues, i + 1, mSize - i);
            }

            mKeys[i] = key;
            mValues[i] = value;
            mSize++;
            return -1;
        }
    }

    /**
     * Returns the number of key-value mappings that this SparseLongArray
     * currently stores.
     */
    public int size() {
        return mSize;
    }

    /**
     * Given an index in the range <code>0...size()-1</code>, returns the key
     * from the <code>index</code>th key-value mapping that this SparseLongArray
     * stores.
     */
    public long keyAt(int index) {
        return mKeys[index];
    }

    /**
     * Given an index in the range <code>0...size()-1</code>, returns the value
     * from the <code>index</code>th key-value mapping that this SparseLongArray
     * stores.
     */
    public int valueAt(int index) {
        return mValues[index];
    }

    /**
     * Returns the index for which {@link #keyAt} would return the specified
     * key, or a negative number if the specified key is not mapped.
     */
    public int indexOfKey(long key) {
        return binarySearch(mKeys, 0, mSize, key);
    }

    /**
     * Returns an index for which {@link #valueAt} would return the specified
     * key, or a negative number if no keys map to the specified value. Beware
     * that this is a linear search, unlike lookups by key, and that multiple
     * keys can map to the same value and this will find only one of them.
     */
    public int indexOfValue(int value) {
        for (int i = 0; i < mSize; i++)
            if (mValues[i] == value)
                return i;

        return -1;
    }

    /**
     * Removes all key-value mappings from this SparseLongArray.
     */
    public void clear() {
        mSize = 0;
    }

    /**
     * Puts a key/value pair into the array, optimizing for the case where the
     * key is greater than all existing keys in the array.
     */
    public void append(long key, int value) {
        if (mSize != 0 && key <= mKeys[mSize - 1]) {
            put(key, value);
            return;
        }

        int pos = mSize;
        if (pos >= mKeys.length) {
            int n = ArrayUtils.idealIntArraySize(pos + 1);

            long[] nkeys = new long[n];
            int[] nvalues = new int[n];

            // Log.e("SparseLongArray", "grow " + mKeys.length + " to " + n);
            System.arraycopy(mKeys, 0, nkeys, 0, mKeys.length);
            System.arraycopy(mValues, 0, nvalues, 0, mValues.length);

            mKeys = nkeys;
            mValues = nvalues;
        }

        mKeys[pos] = key;
        mValues[pos] = value;
        mSize = pos + 1;
    }

    private static int binarySearch(long[] a, int start, int len, long key) {
        int high = start + len, low = start - 1, guess;

        while (high - low > 1) {
            guess = (high + low) / 2;

            if (a[guess] < key)
                low = guess;
            else
                high = guess;
        }

        if (high == start + len)
            return ~(start + len);
        else if (a[high] == key)
            return high;
        else
            return ~high;
    }
}
