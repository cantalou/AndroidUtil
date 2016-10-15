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

    }
}
