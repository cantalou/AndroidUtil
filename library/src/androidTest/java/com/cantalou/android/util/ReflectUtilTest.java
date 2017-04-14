package com.cantalou.android.util;

import org.junit.Test;

import static org.junit.Assert.*;

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
 * Copyright (c) 2017年, 4399 Network CO.ltd. All Rights Reserved.
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

        static String staticOuterMethod() {
            return "staticOuterMethod";
        }

        static String staticOuterMethod(String s) {
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

        assertEquals("str", ReflectUtil.get(out, "str"));
        assertEquals("staticStr", ReflectUtil.get(out, "staticStr"));
        assertEquals("innerStr", ReflectUtil.get(out, "inner.str"));
        assertEquals("innerStaticStr", ReflectUtil.get(out, "inner.staticStr"));
        assertEquals("staticInnerStr", ReflectUtil.get(out, "staticInner.str"));
    }

    @Test
    public void invoke() throws Exception {

        Outer out = new Outer();
        assertEquals("outerMethod", ReflectUtil.invoke(out, "outerMethod"));
        assertEquals("outerMethodString1", ReflectUtil.invoke(out, "outerMethod", new Class[]{String.class}, "1"));
        assertEquals("staticOuterMethod", ReflectUtil.invoke(out, "staticOuterMethod"));
        assertEquals("staticOuterMethod1", ReflectUtil.invoke(out, "staticOuterMethod", new Class[]{String.class}, "1"));
        assertEquals("innerMethod", ReflectUtil.invoke(out, "getStaticInnerMethod.innerMethod"));
        assertEquals("staticInnerMethod", ReflectUtil.invoke(out, "getStaticInnerMethod.staticInnerMethod"));
    }

    @Test
    public void invoke1() throws Exception {

    }

    @Test
    public void invoke2() throws Exception {

    }

    @Test
    public void findMethod() throws Exception {

    }

    @Test
    public void findField() throws Exception {

    }

    @Test
    public void forName() throws Exception {

    }

    @Test
    public void newInstance() throws Exception {

    }

    @Test
    public void newInstance1() throws Exception {

    }

    @Test
    public void newInstance2() throws Exception {

    }

    @Test
    public void findAllFieldValue() throws Exception {

    }

}