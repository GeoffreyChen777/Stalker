package com.sorry.stalker.tools;

import java.io.File;
import java.io.FileOutputStream;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

public class Rar {

    public static void unrar(File sourceRar, File destDir) throws Exception {
        Archive archive = null;
        FileOutputStream fos = null;
        System.out.println("Starting...");
        try {
            archive = new Archive(sourceRar);
            FileHeader fh = archive.nextFileHeader();
            int count = 0;
            File destFileName = null;
            while (fh != null) {
                System.out.println((++count) + ") " + fh.getFileNameString());
                String compressFileName = fh.getFileNameString().trim();
                StringBuffer result = new StringBuffer();
                for(int i = 0;i<compressFileName.length();i++){
                    char a = compressFileName.charAt(i);
                    if((((int)a)>=65&&((int)a)<=90)||(((int)a)>=97&&((int)a)<=122)||(((int)a)>=48&&((int)a)<=57)||a==' '||a=='.'||a=='/'){
                        result.append(a);
                    }
                }
                compressFileName = result.toString();

                if(compressFileName.endsWith(".")){
                    compressFileName = compressFileName.substring(compressFileName.length()-1, compressFileName.length());
                }
                destFileName = new File(destDir.getAbsolutePath() + "/" + compressFileName);
                if (fh.isDirectory()) {
                    if (!destFileName.exists()) {
                        destFileName.mkdirs();
                    }
                    fh = archive.nextFileHeader();
                    continue;
                }
                if (!destFileName.getParentFile().exists()) {
                    destFileName.getParentFile().mkdirs();
                }
                fos = new FileOutputStream(destFileName);
                archive.extractFile(fh, fos);
                fos.close();
                fos = null;
                fh = archive.nextFileHeader();
            }

            archive.close();
            archive = null;
            System.out.println("Finished !");
        } catch (Exception e) {
            throw e;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (Exception e) {
                    //ignore
                }
            }
            if (archive != null) {
                try {
                    archive.close();
                    archive = null;
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }
    public static void unRarFile(String srcRarPath, String dstDirectoryPath) {
        if (!srcRarPath.toLowerCase().endsWith(".rar")) {
            System.out.println("非rar文件！");
            return;
        }
        File dstDiretory = new File(dstDirectoryPath);
        if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
            dstDiretory.mkdirs();
        }
        Archive a = null;
        try {
            a = new Archive(new File(srcRarPath));
            if (a != null) {
                a.getMainHeader().print(); // 打印文件信息.
                FileHeader fh = a.nextFileHeader();
                while (fh != null) {
                    if (fh.isDirectory()) { // 文件夹
                        File fol = new File(dstDirectoryPath + File.separator
                                + fh.getFileNameString());
                        fol.mkdirs();
                    } else { // 文件
                        File out = new File(dstDirectoryPath + File.separator
                                + fh.getFileNameString().trim());
                        //System.out.println(out.getAbsolutePath());
                        try {// 之所以这么写try，是因为万一这里面有了异常，不影响继续解压.
                            if (!out.exists()) {
                                if (!out.getParentFile().exists()) {// 相对路径可能多级，可能需要创建父目录.
                                    out.getParentFile().mkdirs();
                                }
                                out.createNewFile();
                            }
                            FileOutputStream os = new FileOutputStream(out);
                            a.extractFile(fh, os);
                            os.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    fh = a.nextFileHeader();
                }
                a.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}