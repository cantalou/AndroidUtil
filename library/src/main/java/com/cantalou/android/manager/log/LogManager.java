package com.cantalou.android.manager.log;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateUtils;

import com.cantalou.android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 日志记录服务<br>
 * 1.将日志内容写入文件<br>
 * 2.记录线程Crash日志
 *
 * @author cantalou
 * @date 2016年6月12日 上午11:23:22
 */
public final class LogManager {

    /**
     * 日志保存路径
     */
    private BufferedWriter logFile;

    /**
     * 生成日志文件日期
     */
    private long logFileDate;

    /**
     * 日志列表
     */
    private BlockingQueue<String> pendingLog = new LinkedBlockingQueue<String>();

    /**
     * 写日志线程
     */
    private Thread writeLogThread;

    private boolean init = false;

    private final int pid = android.os.Process.myPid();

    private static class InstanceHolder {
        static final LogManager INSTANCE = new LogManager();
    }

    private LogManager() {
    }

    public static LogManager getInstance() {
        return InstanceHolder.INSTANCE;
    }


    class CrashHandler implements Thread.UncaughtExceptionHandler {

        private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Log.e(ex);
            if (uncaughtExceptionHandler != null) {
                uncaughtExceptionHandler.uncaughtException(thread, ex);
            }
        }

        public void replace() {
            if (Thread.getDefaultUncaughtExceptionHandler() != this) {
                uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(this);
            }
        }
    }

    /**
     * 初始化日志文件目录, 启动写文件线程
     *
     * @param context
     */
    public void init(Context context) {

        new CrashHandler().replace();

        final File logDir = Environment.getExternalStoragePublicDirectory(context.getPackageName());

        writeLogThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String log = pendingLog.take();
                        updateLogFile(logDir);
                        logFile.write(log);
                        logFile.newLine();
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }, "Write Log Thread");
        writeLogThread.setDaemon(true);
        writeLogThread.start();
    }

    private void updateLogFile(File logDir) {
        try {
            long now = System.currentTimeMillis();
            if (now - logFileDate > DateUtils.DAY_IN_MILLIS) {
                if (logFileDate > 0) {
                    logFileDate += DateUtils.DAY_IN_MILLIS;
                } else {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    logFileDate = cal.getTimeInMillis();
                }
                if (logFile != null) {
                    logFile.flush();
                    logFile.close();
                }

                logDir.mkdirs();
                String logFileName = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(logFileDate);
                logFile = new BufferedWriter(new FileWriter(new File(logDir, logFileName), true));
            }
        } catch (Exception e) {
            Log.w(e);
        }
    }

    /**
     * 将文本日志写入文件
     *
     * @author cantalou
     * @date 2016年6月12日 下午5:48:28
     */
    public void writeLog(String log) {

        if (!init) {
            return;
        }

        try {
            pendingLog.put(new SimpleDateFormat("MM-dd HH:mm:ss:SSS ").format(new Date()) + pid + " " + log);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
