package com.mobidev.testfilemanager;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Created by olga on 28.02.17.
 */

public class FilesScreenModel {
    private File currentDir;
    private File previousDir;
    private Stack<File> filesHistory;

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
        return filesHistory.pop();
    }

    public void setPreviousDir(File previousDir) {
        this.previousDir = previousDir;
        filesHistory.add(previousDir);
    }

    public boolean hasPreviousDir() {
        return !filesHistory.isEmpty();
    }

    public List<File> getAllFilesInCurrDir(File file) {
        File allFiles[] = file.listFiles();

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
}
