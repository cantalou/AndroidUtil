package com.cantalou.test.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;

import com.cantalou.util.BuildConfig;

public class Log {

    private static int defaultLevel = 0;

    public static final int VERBOSE = 2;

    public static final int DEBUG = 3;

    public static final int INFO = 4;

    public static final int WARN = 5;

    public static final int ERROR = 6;

    /**
     * 输出警告信息,支持"{}"占位符
     *
     * @param t
     *            错误
     * @param msg
     *            日志内容
     */
    public static void w(Throwable t) {
	if (BuildConfig.DEBUG) {
	    log(WARN, t, "");
	}
    }

    /**
     * 输出警告信息,支持"{}"占位符
     *
     * @param msg
     *            日志内容
     */
    public static void w(String msg, Object... formatArgs) {
	if (BuildConfig.DEBUG) {
	    log(WARN, null, msg, formatArgs);
	}
    }

    /**
     * 输出警告信息,支持"{}"占位符
     *
     * @param t
     *            错误
     * @param msg
     *            日志内容
     */
    public static void w(Throwable t, String msg, Object... formatArgs) {
	if (BuildConfig.DEBUG) {
	    log(WARN, t, msg, formatArgs);
	}
    }

    /**
     * 输出错误信息,支持"{}"占位符
     *
     * @param t
     *            错误
     * @param msg
     *            日志内容
     */
    public static void e(Throwable t, String msg, Object... formatArgs) {
	if (BuildConfig.DEBUG) {
	    log(WARN, t, msg, formatArgs);
	}
    }

    /**
     * 输出错误信息,支持"{}"占位符
     *
     * @param t
     *            错误
     */
    public static void e(Throwable t) {
	if (BuildConfig.DEBUG) {
	    log(WARN, t, "");
	}
    }

    /**
     * 输出调试信息,支持"{}"占位符
     *
     * @param msg
     *            日志内容
     */
    public static void d(String msg, Object... formatArgs) {
	if (BuildConfig.DEBUG) {
	    log(DEBUG, null, msg, formatArgs);
	}
    }

    /**
     * 输出警告信息,支持"{}"占位符
     *
     * @param msg
     *            日志内容
     */
    private static void log(int level, Throwable t, String msg, Object... formatArgs) {
	if (defaultLevel > level) {
	    return;
	}

	StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
	String className = ste.getClassName();
	int dotIndex = className.lastIndexOf('.');
	String simpleClassName = dotIndex != -1 ? className.substring(dotIndex + 1) : className;
	String callerInfo = simpleClassName + "." + ste.getMethodName();

	if (formatArgs != null && formatArgs.length > 0) {
	    msg = replace(msg, formatArgs);
	}

	switch (level) {
	case VERBOSE: {
	    android.util.Log.v(callerInfo, msg, t);
	    break;
	}
	case DEBUG: {
	    android.util.Log.d(callerInfo, msg, t);
	    break;
	}
	case INFO: {
	    android.util.Log.i(callerInfo, msg, t);
	    break;
	}
	case WARN: {
	    android.util.Log.w(callerInfo, msg, t);
	    break;
	}
	case ERROR: {
	    android.util.Log.e(callerInfo, msg, t);
	    break;
	}
	}
    }

    /**
     * 将Object数组转换成String数组
     *
     * @return String数组
     */
    private static String replace(String msg, Object[] args) {
	if (StringUtils.isBlank(msg) || msg.indexOf("{}") == -1) {
	    return msg;

	}
	StringBuilder sb = new StringBuilder(msg);
	int index = 0;
	for (Object arg : args) {
	    index = sb.indexOf("{}", index);
	    if (index == -1) {
		break;
	    }

	    String value = toString(arg);
	    sb.replace(index, index + 2, value);
	    index = index + value.length();
	}
	return sb.toString();
    }

    private static String toString(Object obj) {
	if (obj == null) {
	    return "null";
	} else if (obj.getClass().isArray()) {

	    int len = Array.getLength(obj);
	    if (len > 0) {
		StringBuilder sb = new StringBuilder("[");
		sb.append(toString(Array.get(obj, 0)));
		for (int i = 1; i < len; i++) {
		    sb.append(",").append(toString(Array.get(obj, i)));
		}
		sb.append("]");
		return sb.toString();
	    } else {
		return "[]";
	    }
	} else if (obj instanceof Throwable) {
	    StringWriter sw = new StringWriter(256);
	    ((Throwable) obj).printStackTrace(new PrintWriter(new StringWriter()));
	    return sw.toString();
	} else {
	    return obj.toString();
	}
    }
}
