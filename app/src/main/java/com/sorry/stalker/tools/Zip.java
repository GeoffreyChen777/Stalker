package com.sorry.stalker.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by sorry on 2016/6/1.
 */
public class Zip{
    public Zip(){

    }

    public static void upZipFile(File zipFile, String folderPath) throws ZipException,IOException {
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
        // 创建目标目录
            desDir.mkdirs();
        }


        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            InputStream is = zf.getInputStream(entry);
            String str = folderPath + File.separator + entry.getName();
        // 转换编码，避免中文时乱码
            str = new String(str.getBytes("8859_1"), "GB2312");
            File desFile = new File(str);
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
        // 创建目标文件的父目录
                    fileParentDir.mkdirs();
                }
        // 创建目标文件
                desFile.createNewFile();
            }
            OutputStream os = new FileOutputStream(desFile);
            byte[] buffer = new byte[1024];
            int realLength;
            while ((realLength = is.read(buffer)) > 0) {
                os.write(buffer, 0, realLength);
                os.flush();
            }
            is.close();
            os.close();
        }
        zf.close();
    }
}
