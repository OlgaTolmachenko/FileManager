package com.mobidev.testfilemanager;

import java.io.File;

/**
 * Created by Olga Tolmachenko on 19.03.17.
 */

public class FileSelectedEvent {
    private File selectedFile;

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }
}
