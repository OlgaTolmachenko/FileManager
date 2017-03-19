package com.mobidev.testfilemanager;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView prevDirNameField;
    private FilesModel filesModel;
    private RecyclerView filesRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    public void onBackPressed() {
        if (!filesModel.hasPreviousDir()) {
            super.onBackPressed();
        }
        setupBackwardsNavigation();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backLayout:
                setupBackwardsNavigation();
                break;
        }
    }

    @Subscribe
    public void onEvent(MessageEvent fileSelectedEvent) {
        String name = fileSelectedEvent.getSelectedFile().getName();
        setUpTitle(name);
        setupPrevText();
    }

    private void setupBackwardsNavigation() {
        //TODO понять, почему после первого же бэка директория сбрасывается на корневую
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

    private void setUpAdapter() {
        List<File> files = getFilesInCurrDir();
        filesRecycler.setAdapter(new FilesAdapter(this, files, filesModel));
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
