package com.cantalou.android.test;

import com.cantalou.android.util.Log;

import android.test.AndroidTestCase;

/**
 * 
 *
 * 
 * @author cantalou
 * @date 2016年6月12日 下午1:41:13
 */
public class LogTest extends AndroidTestCase {

    public void testThrowable() {
        Log.d("This is a throwable:{}", new NullPointerException());
    }

    public void testArray() {
        assertEquals("This is a int array:[1, 2, 3]", Log.replace("This is a int array:{}", new int[] { 1, 2, 3 }));
        assertEquals("This is a double array:[1.1, 2.2, 33.3]", Log.replace("This is a double array:{}", new double[] { 1.1, 2.2, 33.3 }));
        Log.w("This is a string array:{}", new String[] { "String", "String", "String" });
    }
}
