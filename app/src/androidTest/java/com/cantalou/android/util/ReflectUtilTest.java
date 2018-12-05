package com.cantalou.android.util;

import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Project Name: AndroidUtil<p>
 * File Name:    ReflectUtilTest.java<p>
 * ClassName:    ReflectUtilTest<p>
 * <p>
 * TODO.
 *
 * @author LinZhiWei
 * @date 2017年04月14日 13:47
 * <p>
 * Copyright (c) 2017年,  Network CO.ltd. All Rights Reserved.
 */
public class ReflectUtilTest {

    static class Inner {
        String str;
        static String staticStr;

        String innerMethod() {
            return "innerMethod";
        }

        static String staticInnerMethod() {
            return "staticInnerMethod";
        }
    }

    static class Outer {
        String str;
        static String staticStr;

        Inner inner = new Inner();
        static Inner staticInner = new Inner();

        String outerMethod() {
            return "outerMethod";
        }

        String outerMethod(String s) {
            return "outerMethodString" + s;
        }

        String outerMethod(CharSequence s) {
            return "outerMethodString" + s;
        }

        static String staticOuterMethod() {
            return "staticOuterMethod" + 0;
        }

        static String staticOuterMethod(String s) {
            return "staticOuterMethod" + s;
        }

        static String staticOuterMethod(CharSequence s) {
            return "staticOuterMethod" + s;
        }

        static String staticOuterMethod(int s) {
            return "staticOuterMethod" + s;
        }

        static String staticOuterMethod(Integer s) {
            return "staticOuterMethod" + s;
        }

        Inner getStaticInnerMethod() {
            return staticInner;
        }
    }


    @Test
    public void set() throws Exception {

        Outer out = new Outer();

        ReflectUtil.set(out, "str", "str");
        ReflectUtil.set(out, "staticStr", "staticStr");
        ReflectUtil.set(out, "inner.str", "innerStr");
        ReflectUtil.set(out, "inner.staticStr", "innerStaticStr");
        ReflectUtil.set(out, "staticInner.str", "staticInnerStr");

        assertEquals("str", out.str);
        assertEquals("staticStr", Outer.staticStr);
        assertEquals("innerStr", out.inner.str);
        assertEquals("innerStaticStr", Inner.staticStr);
        assertEquals("staticInnerStr", Outer.staticInner.str);
    }

    @Test
    public void get() throws Exception {
        Outer out = new Outer();
        out.str = "str";
        Outer.staticStr = "staticStr";
        out.inner.str = "innerStr";
        Inner.staticStr = "innerStaticStr";
        Outer.staticInner.str = "staticInnerStr";

        Assert.assertEquals("str", ReflectUtil.get(out, "str"));
        Assert.assertEquals("staticStr", ReflectUtil.get(out, "staticStr"));
        Assert.assertEquals("innerStr", ReflectUtil.get(out, "inner.str"));
        Assert.assertEquals("innerStaticStr", ReflectUtil.get(out, "inner.staticStr"));
        Assert.assertEquals("staticInnerStr", ReflectUtil.get(out, "staticInner.str"));
    }

    /**
     * invoke(Object target, String methodName, Class<?>[] paramsTypes, Object... args)
     *
     * @throws Exception
     */
    @Test
    public void invoke() throws Exception {

        Outer out = new Outer();
        Assert.assertEquals("outerMethodString1", ReflectUtil.invoke(out, "outerMethod", new Class[]{String.class}, "1"));
        Assert.assertEquals("staticOuterMethod0", ReflectUtil.invoke(out, "staticOuterMethod"));
        Assert.assertEquals("staticOuterMethod1", ReflectUtil.invoke(out, "staticOuterMethod", new Class[]{CharSequence.class}, "1"));
    }

    /**
     * invoke(Object target, String methodName, Object... params)
     *
     * @throws Exception
     */
    @Test
    public void invoke1() throws Exception {
        Outer out = new Outer();
        Assert.assertEquals("outerMethod", ReflectUtil.invoke(out, "outerMethod"));
        Assert.assertEquals("staticOuterMethod0", ReflectUtil.invoke(out, "staticOuterMethod"));
        Assert.assertEquals("innerMethod", ReflectUtil.invoke(out, "getStaticInnerMethod.innerMethod"));
        Assert.assertEquals("staticInnerMethod", ReflectUtil.invoke(out, "getStaticInnerMethod.staticInnerMethod"));

        Assert.assertEquals("outerMethodString3", ReflectUtil.invoke(out, "outerMethod", "3"));
        Assert.assertEquals("staticOuterMethod4", ReflectUtil.invoke(out, "staticOuterMethod", "4"));
        Assert.assertEquals("staticOuterMethod5", ReflectUtil.invoke(out, "staticOuterMethod", 5));
        Assert.assertEquals("staticOuterMethod6", ReflectUtil.invoke(out, "staticOuterMethod", Integer.valueOf(6)));

        long start = 0;

        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            Assert.assertEquals("staticOuterMethod4", ReflectUtil.invoke(out, "staticOuterMethod", new Class[]{CharSequence.class}, "4"));
        }
        Log.d("staticOuterMethod explicit CharSequence type time {}", System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            Assert.assertEquals("staticOuterMethod4", ReflectUtil.invoke(out, "staticOuterMethod", new Class[]{String.class}, "4"));
        }
        Log.d("staticOuterMethod explicit String type time {}", System.currentTimeMillis() - start);

        start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            Assert.assertEquals("staticOuterMethod4", ReflectUtil.invoke(out, "staticOuterMethod", "4"));
        }
        Log.d("staticOuterMethod type time {}", System.currentTimeMillis() - start);

    }

}
