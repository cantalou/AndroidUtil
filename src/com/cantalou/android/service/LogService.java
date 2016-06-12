package com.cantalou.android.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.cantalou.android.util.Log;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateUtils;

/**
 * 日志记录服务<br>
 * 1.将日志内容写入文件<br>
 * 2.记录线程Crash日志
 * 
 * @author cantalou
 * @date 2016年6月12日 上午11:23:22
 */
public final class LogService {

    /**
     * 日志保存路径
     */
    private BufferedWriter logFile;

    /**
     * 生成日志文件日期
     */
    private long logFileDate;

    /**
     * 日志目录
     */
    private String logDir;

    /**
     * 日志列表
     */
    private BlockingQueue<String> pendingLog = new LinkedBlockingQueue<String>();

    /**
     * 写日志线程
     */
    private Thread writeLogThread;

    private LogService() {
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

    public void init(Context context) {

        new CrashHandler().replace();
        updateLogFile(context);

        writeLogThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String log = pendingLog.take();
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

    private void updateLogFile(Context context) {
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
                File logDir = Environment.getExternalStoragePublicDirectory(context.getPackageName());
                String logFileName = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(logFileDate);
                logDir.mkdirs();
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
        try {
            pendingLog.put(log);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
