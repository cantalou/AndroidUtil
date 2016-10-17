package com.cantalou.android.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author cantalou
 * @date 2016年2月29日 上午10:55:48
 */
public class FileUtil {

    /**
     * 从assets目录复制文件到指定路径
     *
     * @param context        context
     * @param srcFileName    复制的文件名
     * @param targetFilePath 目标文件名
     * @return 复制是否成功
     */
    public static boolean copyAssetsFile(Context context, String srcFileName, String targetFilePath) {
        AssetManager asm = context.getAssets();
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        try {
            createDir(targetFilePath);
            File targetFile = new File(targetFilePath);
            if (targetFile.exists()) {
                targetFile.delete();
            }

            bis = new BufferedInputStream(asm.open(srcFileName));
            fos = new FileOutputStream(targetFile);
            byte[] buffer = new byte[8 * 1024];
            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
            }
            return true;
        } catch (Exception e) {
            Log.e(e);
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (Exception e2) {
            }

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e2) {
            }
        }
        return false;
    }

    /**
     * 创建文件夹
     *
     * @param dir
     */
    public static void createDir(String dir) {
        createDir(new File(dir));
    }

    /**
     * 创建文件夹
     *
     * @param f
     */
    public static void createDir(File f) {
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * 复制流内容
     *
     * @param in  输入流
     * @param out 输出流
     */
    public static void copyContent(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8096];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        out.flush();
    }

    /**
     * 复制流内容
     *
     * @param in      输入流
     * @param outFile 输出文件
     */
    public static void copyContent(InputStream in, File outFile) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outFile);
            copyContent(in, fos);
        } finally {
            close(fos);
        }
    }

    /**
     * 复制内容
     *
     * @param inFile  输入文件
     * @param outFile 输出文件
     */
    public static void copyContent(File inFile, File outFile) throws IOException {
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(inFile);
            fos = new FileOutputStream(outFile);
            copyContent(fis, fos);
        } finally {
            close(fis, fos);
        }
    }

    /**
     * 关闭资源
     *
     * @param ios
     */
    public static void close(Closeable... ios) {
        for (Closeable io : ios) {
            if (io == null) {
                continue;
            }
            try {
                io.close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

}
