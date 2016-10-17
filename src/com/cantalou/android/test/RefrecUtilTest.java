package com.cantalou.android.test;

import android.test.AndroidTestCase;

import com.cantalou.android.util.ReflectUtil;

/**
 * RefrecUtil 测试类
 *
 * @author cantalou
 * @date 2015年12月5日 下午7:00:30
 */
public class RefrecUtilTest extends AndroidTestCase {

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

    public void testSet() {

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

    public void testGet() {

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

    /**
     * Test method for {@link
     * com.cantalou.android.util.ReflectUtil#invoke(java.lang.Object,
     * java.lang.String, java.lang.Class<?>[])}.
     */
    public void testInvokeObjectStringClassOfQArray() {

        Outer out = new Outer();
        assertEquals("outerMethod", ReflectUtil.invoke(out, "outerMethod"));
        assertEquals("outerMethodString1", ReflectUtil.invoke(out, "outerMethod", new Class[]{String.class}, "1"));
        assertEquals("staticOuterMethod", ReflectUtil.invoke(out, "staticOuterMethod"));
        assertEquals("staticOuterMethod1", ReflectUtil.invoke(out, "staticOuterMethod", new Class[]{String.class}, "1"));
        assertEquals("innerMethod", ReflectUtil.invoke(out, "getStaticInnerMethod.innerMethod"));
        assertEquals("staticInnerMethod", ReflectUtil.invoke(out, "getStaticInnerMethod.staticInnerMethod"));

    }

    /**
     * Test method for {@link
     * com.cantalou.android.util.ReflectUtil#invoke(java.lang.Object,
     * java.lang.String, java.lang.Class<?>[], java.lang.Object[])}.
     */
    public void testInvokeObjectStringClassOfQArrayObjectArray() {
    }
}
