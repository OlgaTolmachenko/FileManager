package com.mobidev.testfilemanager;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * Created by olga on 28.02.17.
 */

public class FilesModel {
    private File currentDir;
    //TODO: ЗАЧЕМ??????
    private static FilesModel instance;
    //TODO: ЗАЧЕМ??????
    private List<File> filesToShow;

    private FilesModel() {
        setupCurrentDir();
    }
    //TODO: ЗАЧЕМ??????
    public static FilesModel getInstance() {
        if (instance == null) {
            instance = new FilesModel();
        }
        return instance;
    }

    private void setupCurrentDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            currentDir = Environment.getExternalStorageDirectory();
        } else {
            Log.d(Constants.TAG, "setupCurrentDir: storage is READONLY");
        }
    }

    public File getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(File currentDir) {
        this.currentDir = currentDir;
    }

    public File getPreviousDir() {
        return currentDir.getParentFile();
    }

    public String getPreviousDirName() {
        String name = "";
        if (hasPreviousDir()) {
            name = currentDir.getParentFile().getName();
        }
        return name;
    }

    public boolean hasPreviousDir() {
        return currentDir.getParentFile() != null;
    }

    public List<File> getAllFilesInCurrDir(final File file) {

        final File[] allFiles = file.listFiles();
        Comparator comparator = new Comparator() {
            @Override
            public int compare(Object obj1, Object obj2) {
                File file1 = (File) obj1;
                File file2 = (File) obj2;

                if (file1.isDirectory() && !file2.isDirectory()) {
                    return -1;
                } else if (!file1.isDirectory() && file2.isDirectory()) {
                    return 1;
                } else {
                    return ((File) obj1).compareTo((File) obj2);
                }
            }
        };

        Arrays.sort(allFiles, comparator);
        return Arrays.asList(allFiles);
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.getPath());

        if (extension != null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return mimeType;
    }

    public List<File> getFilesToShow() {
        return filesToShow;
    }

    public void setFilesToShow(List<File> filesToShow) {
        this.filesToShow = filesToShow;
    }
}
