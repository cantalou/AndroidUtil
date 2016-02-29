package com.cantalou.android.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * 
 *
 * 
 * @author cantalou
 * @date 2016年2月29日 上午10:55:48
 */
public class FileUtil {

    /**
     * 从assets目录复制文件到指定路径
     *
     * @param context
     *            context
     * @param srcFileName
     *            复制的文件名
     * @param targetDir
     *            目标目录
     * @param targetFileName
     *            目标文件名
     * 
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
}
