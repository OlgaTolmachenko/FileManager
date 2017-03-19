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
import java.util.LinkedList;
import java.util.List;

import static com.mobidev.testfilemanager.FilesModel.getCurrentDir;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView prevDirNameField;

    private FilesModel filesModel;

    private List<File> files = new LinkedList<>();

    private RecyclerView filesRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filesModel = new FilesModel();

        checkAndRequestPermissions();
        files = filesModel.getAllFiles(getCurrentDir());


        filesRecycler = (RecyclerView) findViewById(R.id.fileRecycler);
        filesRecycler.setLayoutManager(new LinearLayoutManager(this));
        filesRecycler.setAdapter(new FilesAdapter(this, files));
        RecyclerView.ItemDecoration filesDivider = new Divider(ContextCompat.getDrawable(this, R.drawable.list_divider));
        filesRecycler.addItemDecoration(filesDivider);

        prevDirNameField = (TextView) findViewById(R.id.prevDirName);
        LinearLayout backLayout = (LinearLayout) findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);


        EventBus.getDefault().register(this);

//        filesAdapter.notifyDataSetChanged();
    }

    private void checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestDocumentsPermissions();
        }
    }

    @Override
    public void onBackPressed() {
        if (!filesModel.hasPreviousDir()) {
            super.onBackPressed();
        }
        setupBackwardsNavigation();
    }

    private void setupBackwardsNavigation() {
        //TODO понять, почему после первого же бэка директория сбрасывается на корневую
        files = filesModel.getAllFiles(filesModel.getPreviousDir());
        filesRecycler.setAdapter(new FilesAdapter(this, files));
        setUpTitle(FilesModel.getCurrentDir().getName());
        setupPrevText();
    }

    private void setupPrevText() {
//        //TODO почему логика определения корня не спрятанна в FilesModel?
//        if (filesModel.getPreviousDirName().equals("0/")) {
//            prevDirNameField.setText("root/");
//        } else {
//            prevDirNameField.setText(name);
//        }

        String prevDirName = FilesModel.getCurrentDir().getParentFile().getName();
        prevDirNameField.setText(prevDirName + "/");
    }

    private void setUpTitle(String selectedDir) {
        if (filesModel.hasPreviousDir()) {
            setTitle(selectedDir);
        } else {
            setTitle(getPackageManager().getApplicationLabel(this.getApplicationInfo()));
        }
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

                    files = filesModel.getAllFiles(getCurrentDir());
                    filesRecycler.setAdapter(new FilesAdapter(this, files));
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void requestDocumentsPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS}, Constants.REQUEST_READ_STORAGE_PERMISSION);
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
    public void onEvent(FileSelectedEvent fileSelectedEvent) {
        String name = fileSelectedEvent.getSelectedFile().getName();
        setUpTitle(name);
        setupPrevText();
    }
}
