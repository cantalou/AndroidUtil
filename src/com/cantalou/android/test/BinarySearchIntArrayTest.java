package com.cantalou.android.test;

import com.cantalou.android.util.array.BinarySearchIntArray;

import android.test.AndroidTestCase;

/**
 * 
 *
 * 
 * @author cantalou
 * @date 2016年2月29日 上午10:54:50
 */
public class BinarySearchIntArrayTest extends AndroidTestCase {

    public void testPutContains() {
	BinarySearchIntArray array = new BinarySearchIntArray();
	array.put(1);
	array.put(2);
	array.put(3);
	array.put(4);
	array.put(1);
	assertEquals(4, array.size());
	assertTrue(array.contains(1));
	assertTrue(array.contains(4));
	assertTrue(!array.contains(5));
	assertTrue(!array.contains(-1));
    }
}
