package com.mobidev.testfilemanager;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Created by olga on 28.02.17.
 */

public class FilesModel {
    private File currentDir;
    private File previousDir;
    //TODO: ЗАЧЕМ??????
    private Stack<File> filesHistory;
    private static FilesModel instance;
    //TODO: ЗАЧЕМ??????
    private List<File> filesToShow;

    private FilesModel() {
        setupCurrentDir();
        setupHistory();
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

    private void setupHistory() {
        filesHistory = new Stack<>();
    }

    public File getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(File currentDir) {
        this.currentDir = currentDir;
    }

    public File getPreviousDir() {
        return filesHistory.pop();
    }

    public String getPreviousDirName() {
        String name = "";
        if (hasPreviousDir()) {
            name = filesHistory.peek().getName();
        }
        return name;
    }

    public void setPreviousDir(File previousDir) {
        this.previousDir = previousDir;
        filesHistory.push(previousDir);
    }

    public boolean hasPreviousDir() {
        return !filesHistory.isEmpty();
    }

    public List<File> getAllFilesInCurrDir(File file) {

        File[] allFiles = file.listFiles();
        //TODO: Тут нужно было написать свой компаратор тогда достаточно былобы одного прохода по массиву https://coderanch.com/t/378718/java/sort-array-files-directories
        List<File> dirs = new ArrayList<>();
        List<File> files = new ArrayList<>();

        for (File currentFile : allFiles) {
            if (currentFile.isDirectory()) {
                dirs.add(currentFile);
            } else {
                files.add(currentFile);
            }
        }

        Collections.sort(dirs);
        Collections.sort(files);

        dirs.addAll(files);

        return dirs;
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
