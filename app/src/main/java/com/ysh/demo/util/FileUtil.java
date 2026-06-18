package com.ysh.demo.util;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtil {
    public static String getFileName(Activity activity, Uri uri) {
        Cursor cursor = activity.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        if (columnIndex == -1) {
            return null;
        }
        String string = cursor.getString(columnIndex);
        cursor.close();
        return string;
    }

    public static String getAndInserterString(String filePath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            String tag1 = "<coordinates>";
            String tag2 = "</coordinates>";
            boolean isCoordinates = false;
            while ((line = bufferedReader.readLine()) != null) {
                if (isCoordinates) {
                    if (line.contains(tag2)) {
                        isCoordinates = false;
                        stringBuilder.append(line).append("\n");
                    } else {
                        int index = line.lastIndexOf(" ");
                        String substring = line.substring(0, index);
                        stringBuilder.append(substring).append("<string>").append(line.trim()).append("</string>").append("\n");
                    }
                } else {
                    stringBuilder.append(line).append("\n");
                }
                if (line.contains(tag1)) {
                    isCoordinates = true;
                }
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //输入流读取一行字符串
    public int readLine(InputStream inputStream, byte[] b, int off, int len) throws IOException {
        //如果内容长度小于等于0，直接返回0
        if (len <= 0) {
            return 0;
        }
        //count统计当前读到的位置
        // c读取Byte值
        int count = 0, c;
        //逐字节读取内容
        while ((c = inputStream.read()) != -1) {
            //将读到的字节存储起来
            b[off++] = (byte) c;
            //读到的位置自增1
            count++;
            //如果遇到'\n'换行符或者读取完毕返回当前读到的位置
            if (c == '\n' || count == len) {
                break;
            }
        }
        //如果当前位置不在内容长度里，返回-1表示读取一行数据失败
        return count > 0 ? count : -1;
    }

    public static String generateMD5(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInteger = new BigInteger(1, md5sum);
            StringBuilder hash = new StringBuilder(bigInteger.toString(16));
            while (hash.length() < 32) {
                hash.insert(0, "0");
            }
            return hash.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileContent(String filePath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileSuffix(@NotNull File file) {
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public static String getFileSuffix(@NotNull String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    public static boolean isImage(@NotNull String fileName) {
        String lowerCase = fileName.toLowerCase();
        return lowerCase.endsWith("jpg") ||
                lowerCase.endsWith("png") || lowerCase.endsWith("jpeg") ||
                lowerCase.endsWith("bmp") || lowerCase.endsWith("gif") ||
                lowerCase.endsWith("dng") || lowerCase.endsWith("webp")
                ;
    }
}
