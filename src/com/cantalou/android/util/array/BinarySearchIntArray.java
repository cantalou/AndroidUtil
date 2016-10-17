package com.cantalou.android.util.array;

/**
 * BinarySearchIntArrays map integers .
 *
 * @author cantalou
 * @date 2016年2月29日 上午10:55:31
 */
public class BinarySearchIntArray implements Cloneable {

    private int[] mKeys;
    private int mSize;

    /**
     * Creates a new BinarySearchIntArray containing no keys.
     */
    public BinarySearchIntArray() {
        this(10);
    }

    /**
     * Creates a new BinarySearchIntArray containing no keys that will not
     * require any additional memory allocation to store the specified number of
     * keys.
     */
    public BinarySearchIntArray(int initialCapacity) {
        initialCapacity = idealIntArraySize(initialCapacity);
        mKeys = new int[initialCapacity];
        mSize = 0;
    }

    @Override
    public BinarySearchIntArray clone() {
        BinarySearchIntArray clone = null;
        try {
            clone = (BinarySearchIntArray) super.clone();
            clone.mKeys = mKeys.clone();
        } catch (CloneNotSupportedException cnse) {
        /* ignore */
        }
        return clone;
    }

    /**
     * Check array contains the specified key, or <code>false</code> if no such
     * key has been made.
     */
    public boolean contains(int key) {
        return binarySearch(mKeys, 0, mSize, key) > -1;
    }

    /**
     * Removes the specified key, if there was any.
     */
    public void delete(int key) {
        int i = binarySearch(mKeys, 0, mSize, key);

        if (i >= 0) {
            removeAt(i);
        }
    }

    /**
     * Removes the key at the given index.
     */
    public void removeAt(int index) {
        System.arraycopy(mKeys, index + 1, mKeys, index, mSize - (index + 1));
        mSize--;
    }

    /**
     * Adds a specified key , replacing the previous specified key if there was
     * one.
     */
    public void put(int key) {
        int i = binarySearch(mKeys, 0, mSize, key);
        if (i < 0) {
            i = ~i;
            if (mSize >= mKeys.length) {
                int n = idealIntArraySize(mSize + 1);
                int[] nkeys = new int[n];
                System.arraycopy(mKeys, 0, nkeys, 0, mKeys.length);
                mKeys = nkeys;
            }
            if (mSize - i != 0) {
                System.arraycopy(mKeys, i, mKeys, i + 1, mSize - i);
            }
            mKeys[i] = key;
            mSize++;
        }
    }

    /**
     * Returns the number of keythat this BinarySearchIntArray currently stores.
     */
    public int size() {
        return mSize;
    }

    /**
     * Returns the index for which {@link #keyAt} would return the specified
     * key, or a negative number if the specified key is not mapped.
     */
    public int indexOfKey(int key) {
        return binarySearch(mKeys, 0, mSize, key);
    }

    /**
     * Removes all key-value mappings from this BinarySearchIntArray.
     */
    public void clear() {
        mSize = 0;
    }

    private static int binarySearch(int[] a, int start, int len, int key) {
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

    public static int idealByteArraySize(int need) {
        for (int i = 4; i < 32; i++)
            if (need <= (1 << i) - 12)
                return (1 << i) - 12;

        return need;
    }

    public static int idealIntArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }
}
