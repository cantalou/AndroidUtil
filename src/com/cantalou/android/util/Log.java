package com.cantalou.android.util;

import java.lang.reflect.Array;

/**
 * 日志记录工具类<br>
 * 1.使用"{}"作为字符串拼接占位符
 *
 * @author cantalou
 * @date 2015年12月1日 上午11:15:22
 */
public class Log {

    public static boolean logEnable = true;

    public static int defaultLevel = 0;

    public static final int VERBOSE = 2;

    public static final int DEBUG = 3;

    public static final int INFO = 4;

    public static final int WARN = 5;

    public static final int ERROR = 6;

    public static String LOG_TAG_FLAG = "";

    /**
     * 输出警告信息,支持"{}"占位符
     *
     * @param t 错误
     */
    public static void w(Throwable t) {
        if (logEnable) {
            log(WARN, t, "");
        }
    }

    /**
     * 输出警告信息,支持"{}"占位符
     *
     * @param msg 日志内容
     */
    public static void w(String msg, Object... formatArgs) {
        if (logEnable) {
            log(WARN, null, msg, formatArgs);
        }
    }

    /**
     * 输出警告信息,支持"{}"占位符
     *
     * @param t   错误
     * @param msg 日志内容
     */
    public static void w(Throwable t, String msg, Object... formatArgs) {
        if (logEnable) {
            log(WARN, t, msg, formatArgs);
        }
    }

    /**
     * 输出错误信息,支持"{}"占位符
     *
     * @param t   错误
     * @param msg 日志内容
     */
    public static void e(Throwable t, String msg, Object... formatArgs) {
        if (logEnable) {
            log(WARN, t, msg, formatArgs);
        }
    }

    /**
     * 输出错误信息,支持"{}"占位符
     *
     * @param t 错误
     */
    public static void e(Throwable t) {
        if (logEnable) {
            log(WARN, t, "");
        }
    }

    /**
     * 输出调试信息,支持"{}"占位符
     *
     * @param msg 日志内容
     */
    public static void i(String msg, Object... formatArgs) {
        if (logEnable) {
            log(INFO, null, msg, formatArgs);
        }
    }

    /**
     * 输出调试信息,支持"{}"占位符
     *
     * @param msg 日志内容
     */
    public static void d(String msg, Object... formatArgs) {
        if (logEnable) {
            log(DEBUG, null, msg, formatArgs);
        }
    }

    /**
     * 输出调试信息,支持"{}"占位符
     *
     * @param msg 日志内容
     */
    public static void v(String msg, Object... formatArgs) {
        if (logEnable) {
            log(VERBOSE, null, msg, formatArgs);
        }
    }

    /**
     * 输出警告信息,支持"{}"占位符
     *
     * @param msg 日志内容
     */
    private static void log(int level, Throwable t, String msg, Object... formatArgs) {

        if (defaultLevel > level) {
            return;
        }

        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        String className = ste.getClassName();
        int dotIndex = className.lastIndexOf('.');
        String simpleClassName = dotIndex != -1 ? className.substring(dotIndex + 1) : className;
        String callerInfo = simpleClassName + "." + ste.getMethodName() + LOG_TAG_FLAG;

        if (formatArgs != null && formatArgs.length > 0) {
            msg = replace(msg, formatArgs);
        }

        final int logLimit = 4000;
        String[] msgs;
        if (msg.length() > logLimit) {
            int len = msg.length() / logLimit + 1;
            msgs = new String[len];
            for (int i = 0; i < len; i++) {
                int end = logLimit * (i + 1);
                msgs[i] = msg.substring(i * logLimit, end < msg.length() ? end : msg.length());
            }
        } else {
            msgs = new String[]{msg};
        }

        for (String toPrint : msgs) {
            switch (level) {
                case VERBOSE: {
                    android.util.Log.v(callerInfo, toPrint, t);
                    break;
                }
                case DEBUG: {
                    android.util.Log.d(callerInfo, toPrint, t);
                    break;
                }
                case INFO: {
                    android.util.Log.i(callerInfo, toPrint, t);
                    break;
                }
                case WARN: {
                    android.util.Log.w(callerInfo, toPrint, t);
                    break;
                }
                case ERROR: {
                    android.util.Log.e(callerInfo, toPrint, t);
                    break;
                }
            }
        }

    }

    /**
     * 使用Object数组参数替换字符串msg中的"{}"占位符
     *
     * @return 占位符替换后的字符串
     */
    public static String replace(String msg, Object... args) {

        if (StringUtils.isBlank(msg)) {
            return msg;
        }

        int index = msg.indexOf("{}");
        if (index == -1) {
            return msg;
        }

        StringBuilder sb = new StringBuilder(msg);

        int i = 0;
        while (index != -1 && i < args.length) {
            String value = toString(args[i++]);
            sb.replace(index, index + 2, value);
            index = sb.indexOf("{}", index + value.length());
        }
        return sb.toString();
    }

    /**
     * 将参数obj转化成字符串
     *
     * @param obj
     * @return 对象字符串
     * @author cantalou
     * @date 2015年12月1日 下午1:47:36
     */
    private static String toString(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj.getClass().isArray()) {

            int len = Array.getLength(obj);
            if (len == 0) {
                return "[]";
            }
            StringBuilder sb = new StringBuilder(len * 7);
            sb.append('[');
            sb.append(Array.get(obj, 0));
            for (int i = 1; i < len; i++) {
                sb.append(", ");
                sb.append(Array.get(obj, i));
            }
            sb.append(']');
            return sb.toString();
        } else if (obj instanceof Throwable) {
            return android.util.Log.getStackTraceString((Throwable) obj);
        } else {
            return obj.toString();
        }
    }

}
