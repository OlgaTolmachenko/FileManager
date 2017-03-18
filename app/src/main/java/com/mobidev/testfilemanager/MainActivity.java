package com.mobidev.testfilemanager;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FilesAdapter filesAdapter;
    private TextView prevDirName;

    private BroadcastReceiver broadcastReceiver;
    private FilesModel filesModel;

    private List<File> files;

    private RecyclerView filesRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filesModel = new FilesModel();

        checkAndRequestPermissions();
        files = filesModel.getAllFiles(filesModel.getCurrentDir());

        filesAdapter = new FilesAdapter(this, files, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        filesRecycler = (RecyclerView) findViewById(R.id.fileRecycler);
        filesRecycler.setLayoutManager(new LinearLayoutManager(this));
        filesRecycler.setAdapter(filesAdapter);
        RecyclerView.ItemDecoration filesDivider = new Divider(ContextCompat.getDrawable(this, R.drawable.list_divider));
        filesRecycler.addItemDecoration(filesDivider);

        prevDirName = (TextView) findViewById(R.id.prevDirName);
        LinearLayout backLayout = (LinearLayout) findViewById(R.id.backLayout);
        backLayout.setOnClickListener(this);



//        filesAdapter.notifyDataSetChanged();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.INTENT_ACTION)) {
                    String name = intent.getStringExtra(Constants.PREV_NAME) + "/";
                    setupPrevText(name);
                }
            }
        };
    }

    private void checkAndRequestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestDocumentsPermissions();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(
                            broadcastReceiver,
                            new IntentFilter(Constants.INTENT_ACTION));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        if (!filesModel.hasPreviousDir() || filesModel.getPreviousDirName().equals("0")) {
            super.onBackPressed();
        }
        setupBackwardsNavigation();
    }

    private void setupBackwardsNavigation() {
        //TODO fix перепрыгивание через одну папку при шаге назад
        files = filesModel.getAllFiles(filesModel.getCurrentDir().getParentFile());

        filesAdapter = new FilesAdapter(this, files, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        filesRecycler.setAdapter(filesAdapter);
//        filesAdapter.notifyDataSetChanged();
        setUpTitle();
        setupPrevText(filesModel.getPreviousDirName() + "/");
    }

    private void setupPrevText(String name) {
        //TODO почему логика определения корня не спрятанна в FilesModel?
        if (filesModel.getPreviousDirName().equals("0")) {
            prevDirName.setText("root/");
        } else {
            prevDirName.setText(name);
        }
    }

    private void setUpTitle() {
        if (filesModel.hasPreviousDir()) {
            setTitle(filesModel.getCurrentDir().getName());
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

                    files = filesModel.getAllFiles(filesModel.getCurrentDir());
                    filesAdapter.notifyDataSetChanged();
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
}
