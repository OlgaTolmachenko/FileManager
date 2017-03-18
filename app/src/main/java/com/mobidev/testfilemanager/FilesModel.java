package com.mobidev.testfilemanager;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import static com.mobidev.testfilemanager.Constants.TAG;

/**
 * Created by olga on 28.02.17.
 */

public class FilesModel {
    private File currentDir;

    public FilesModel() {
        setupCurrentDir();
    }


    private void setupCurrentDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            currentDir = Environment.getExternalStorageDirectory();
        } else {
            Log.d(TAG, "setupCurrentDir: storage is READONLY");
        }
    }

    public File getCurrentDir() {
        return currentDir;
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

    public List<File> getAllFiles(final File file) {

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

        for(File currFile : allFiles) {
            Log.d(Constants.TAG, "getAllFiles: " + currFile.getName());
        }
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
}
