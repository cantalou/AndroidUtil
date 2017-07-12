package com.cantalou.android.util;

import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

/**
 * @author cantalou
 * @date 2016年6月12日 下午1:41:13
 */
@RunWith(AndroidJUnit4.class)
public class LogTest {

    public void testThrowable() {
        Log.d("This is a throwable:{}", new NullPointerException());
    }

    public void testArray() {

    }
}
