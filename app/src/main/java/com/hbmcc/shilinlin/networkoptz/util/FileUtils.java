package com.hbmcc.shilinlin.networkoptz.util;

import android.os.Environment;

import com.hbmcc.shilinlin.networkoptz.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by linlinshi on 2017/12/20.
 */

public class FileUtils {
    String appName = App.getContext().getApplicationInfo().loadLabel(App.getContext()
            .getPackageManager
            ()).toString();
    private static String SDPath;
    private static String appPath;

    //构造函数，得到SD卡的目录，这行函数得到的目录名其实是叫"/SDCARD"
    public FileUtils() {
        SDPath = Environment.getExternalStorageDirectory() + "/";
        appPath = Environment.getExternalStorageDirectory() + "/" + appName + "/";
    }

    public static String getSDPATH() {
        return SDPath;
    }

    public static String getAppPath() {
        return appPath;
    }

    //在SD卡上创建文件
    public static File createSDFile(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        return file;
    }

    //在SD卡上创建单级目录
    public static File createSDDir(String dirName) {
        File dir = new File(dirName);
        dir.mkdir();
        return dir;
    }

    //在SD卡上创建多级目录
    public static File createSDDirs(String dirsName) {
        File dirs = new File(dirsName);
        dirs.mkdirs();
        return dirs;
    }

    //判断SD卡上的文件或者文件夹是否存在
    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }


    //将一个InputStream里面的数据写入到SD卡中
    //将input写到path这个目录中的fileName文件上
    private File write2SDFromInput(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            createSDDir(path);
            file = createSDFile(path + fileName);
            //FileInputStream是读取数据，FileOutputStream是写入数据，写入到file这个文件上去
            output = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            while ((input.read(buffer)) != -1) {
                output.write(buffer);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static int initialStorage() {
        if (!FileUtils.isFileExist(FileUtils.getAppPath() )) {
            FileUtils.createSDDirs(FileUtils.getAppPath() );
        }

        if (!FileUtils.isFileExist(FileUtils.getAppPath() + "Database/")) {
            FileUtils.createSDDirs(FileUtils.getAppPath() + "Database/");
        }
        return 0;
    }

    public static String getLteInputFile(){
        return FileUtils.getAppPath() + "4G工参(模板).csv";
    }
}
