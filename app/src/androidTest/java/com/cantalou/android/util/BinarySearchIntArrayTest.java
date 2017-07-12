package com.cantalou.android.util;

import android.support.test.runner.AndroidJUnit4;

import com.cantalou.android.util.array.BinarySearchIntArray;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * @author cantalou
 * @date 2016年2月29日 上午10:54:50
 */
@RunWith(AndroidJUnit4.class)
public class BinarySearchIntArrayTest{

    @Test
    public void testPutContains() {
        BinarySearchIntArray array = new BinarySearchIntArray();
        array.put(1);
        array.put(2);
        array.put(3);
        array.put(4);
        array.put(1);
        Assert.assertEquals(4, array.size());
        assertTrue(array.contains(1));
        assertTrue(array.contains(4));
        assertTrue(!array.contains(5));
        assertTrue(!array.contains(-1));
    }
}
