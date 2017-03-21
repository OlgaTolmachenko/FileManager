package com.mobidev.testfilemanager;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.mimeType;
import static android.R.attr.switchMinWidth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView prevDirNameField;
    private FilesModel filesModel;
    private RecyclerView filesRecycler;
    private boolean isPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPaused = false;

        prevDirNameField = (TextView) findViewById(R.id.prevDirName);

        filesModel = new FilesModel();

        checkAndRequestPermissions();

        filesRecycler = (RecyclerView) findViewById(R.id.fileRecycler);
        setupLayoutManager();
        setupDecoration();
        setUpAdapter();

        LinearLayout backLayout = (LinearLayout) findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    public void onBackPressed() {
        if (!filesModel.hasPreviousDir() || isPaused) {
            super.onBackPressed();
        }
        setupBackwardsNavigation();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLayout:
                if (filesModel.hasPreviousDir()) {
                    setupBackwardsNavigation();
                }
                break;
        }
    }

    @Subscribe
    public void onEvent(MessageEvent fileSelectedEvent) {
        if (fileSelectedEvent.getSelectedFile().isDirectory()) {
            String name = fileSelectedEvent.getSelectedFile().getName();
            setUpTitle(name);
            setupPrevText();
        } else {
            Uri selectedFileUri;
            if(Build.VERSION.SDK_INT == 24){
                selectedFileUri = FileProvider.getUriForFile(
                        MainActivity.this,
                        "com.mobidev.testfilemanager",
                        fileSelectedEvent.getSelectedFile());
            } else{
                selectedFileUri = Uri.fromFile(fileSelectedEvent.getSelectedFile());
            }
            openFile(selectedFileUri);
        }
    }

    private void setupBackwardsNavigation() {
        setUpTitle(filesModel.getCurrDirName());
        setupPrevText();

        if (filesModel.hasPreviousDir()) {
            filesModel.setCurrentDir(filesModel.getPreviousDir());
        }

        setUpAdapter();
    }

    private void setupPrevText() {
        prevDirNameField.setText(filesModel.getPreviousDirName());
    }

    private void setUpTitle(String selectedDir) {
        if (filesModel.hasPreviousDir()) {
            setTitle(selectedDir);
        } else {
            setTitle(getPackageManager().getApplicationLabel(this.getApplicationInfo()));
        }
    }

    private void openFile(Uri fileUri) {
        String mimeType = filesModel.getMimeType(fileUri);

        Intent openFileIntent = new Intent(Intent.ACTION_VIEW);

        switch (mimeType) {
            case ".mp3":
                openFileIntent.setDataAndType(fileUri, "audio/mp3");
                break;
            default:
                openFileIntent.setDataAndType(fileUri, mimeType);
        }

        openFileIntent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION);

//        if (TextUtils.isEmpty(mimeType)) {
//            openFileIntent.setType("*/*");
//        } else {
//            openFileIntent.setDataAndType(fileUri, mimeType);
//        }

        try {
            startActivity(openFileIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_app_match, Toast.LENGTH_LONG).show();
        }
    }

    private void setUpAdapter() {
        List<File> files = getFilesInCurrDir();
        filesRecycler.setAdapter(new FilesAdapter(files, filesModel));
    }

    private List<File> getFilesInCurrDir() {
        return filesModel.getAllFiles(filesModel.getCurrentDir());
    }

    private void setupLayoutManager() {
        filesRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupDecoration() {
        RecyclerView.ItemDecoration filesDivider = new Divider(ContextCompat.getDrawable(this, R.drawable.list_divider));
        filesRecycler.addItemDecoration(filesDivider);
    }

    private void checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestDocumentsPermissions();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void requestDocumentsPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS}, Constants.REQUEST_READ_STORAGE_PERMISSION);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQUEST_READ_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && permissions[0].equals(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setUpAdapter();
                }
            }
        }
    }
}
