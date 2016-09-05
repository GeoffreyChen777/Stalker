package com.sorry.stalker.tools;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author michaelw
 * @email michael.wang1028@gmail.com
 * @date 2012-09-03
 */
public class ConverEncoding {

    static String CODE = "UTF-8";
    static String FILE_SUFFIX = ".srt";
    //文件扩展名
    //static String FILE_SUFFIX = ".css";
    //static String FILE_SUFFIX = ".js";
    //static String FILE_SUFFIX = ".htm";
    static String srcDir = "C:\\WorkTools\\weenCompany_ChineseEnglish_JT_V5.3.0_UTF8";//文件所在目录

    /*public static void main(String[] args) throws Exception {
        List<String> files = new ArrayList<String>();
        fetchFileList(srcDir, files, FILE_SUFFIX);
        String filecode = "";
        for (String fileName : files) {
            filecode = codeString(fileName);
            if (!filecode.equals(CODE)) {
                convert(fileName, filecode, fileName, CODE);
            }
        }
    }*/

    public static void convert(String oldFile, String oldCharset,
                               String newFlie, String newCharset) {
        BufferedReader bin;
        FileOutputStream fos;
        StringBuffer content = new StringBuffer();
        if(oldFile.contains("encoded")){
            return;
        }
        try {
            System.out.println("the old file is :"+oldFile);
            System.out.println("The oldCharset is : "+oldCharset);
            bin = new BufferedReader(new InputStreamReader(new FileInputStream(
                    oldFile), oldCharset));
            String line = null;
            while ((line = bin.readLine()) != null) {
                //System.out.println("content:" + content);
                content.append(line);
                content.append(System.getProperty("line.separator"));
            }
            bin.close();
            File dir = new File(newFlie.substring(0, newFlie.lastIndexOf("/")));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            fos = new FileOutputStream(newFlie);
            Writer out = new OutputStreamWriter(fos, newCharset);
            out.write(content.toString());
            out.close();
            fos.close();
            System.out.println("convert done!!!!!!");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fetchFileList(String strPath, List<String> filelist,
                                     final String regex) {
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        Pattern p = Pattern.compile(regex);
        if (files == null)
            return;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                fetchFileList(files[i].getAbsolutePath(), filelist, regex);
            } else {
                String strFileName = files[i].getAbsolutePath().toLowerCase();
                Matcher m = p.matcher(strFileName);
                if (m.find()) {
                    filelist.add(strFileName);
                }
            }
        }
    }

    /**
     * 判断文件的编码格式
     *
     * @param fileName
     *            :file
     * @return 文件编码格式
     * @throws Exception
     */
    public static String codeString(String fileName) throws Exception {
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(
                fileName));
        int p = (bin.read() << 8) + bin.read();
        String code = null;

        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }

        return code;
    }
}