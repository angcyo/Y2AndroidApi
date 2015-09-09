package com.angcyo.y2androidapi.util;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by angcyo on 15-07-26-026.
 */
public class FileUtil {
    /**
     * Save file from stream.
     *
     * @param fullName 文件的全路径
     * @param read     需要保存的数据流
     */
    public static void saveFileFromStream(String fullName, InputStream read) {
        File newFile = new File(fullName);
        FileWriter fw = null;
        BufferedReader reader = null;
        try {
            fw = new FileWriter(newFile);
            reader = new BufferedReader(new InputStreamReader(read, "utf-8"));
            String data;
            while ((data = reader.readLine()) != null) {
                fw.write(data);
            }
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save file from string.
     *
     * @param fullName the full name
     * @param content  the content
     */
    public static void saveFileFromString(String fullName, String content) {
        File newFile = new File(fullName);
        FileWriter fw = null;
        try {
            fw = new FileWriter(newFile);
            fw.write(content.toString());
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 判断文件是否存在
     *
     * @param fullName 文件全路径
     * @return the boolean
     */
    public static boolean isFileExist(String fullName) {
        File newFile = new File(fullName);
        return newFile.exists() ? true : false;
    }

    public static String getAppCachePath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    public static void cleanAppChache(@NonNull String cachePath){
        try {
            File cacheFile = new File(cachePath);
            for (File file :
                    cacheFile.listFiles() ) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
