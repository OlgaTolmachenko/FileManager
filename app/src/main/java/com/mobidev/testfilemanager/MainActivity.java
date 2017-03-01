package com.mobidev.testfilemanager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
//        implements LoaderManager.LoaderCallbacks<List<File>>
{

//    private AsyncTaskLoader<List<File>> fileLoader;
    private FilesAdapter filesAdapter;
    private LinearLayout backLayout;
    private TextView prevDirName;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra("name") + "/";
            setupPrevText(name);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backLayout = (LinearLayout) findViewById(R.id.backLayout);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupBackwardsNavigation();
            }
        });

        prevDirName = (TextView) findViewById(R.id.prevDirName);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestDocumentsPermissions();
        }

        FilesModel.getInstance().setFilesToShow(
                FilesModel.getInstance().getAllFilesInCurrDir(FilesModel.getInstance().getCurrentDir()));

        filesAdapter = new FilesAdapter(this);

        RecyclerView filesRecycler = (RecyclerView) findViewById(R.id.fileRecycler);
        filesRecycler.setLayoutManager(new LinearLayoutManager(this));
        filesRecycler.setAdapter(filesAdapter);
        RecyclerView.ItemDecoration filesDivider = new Divider(getDrawable(R.drawable.list_divider));
        filesRecycler.addItemDecoration(filesDivider);

//        getLoaderManager().initLoader(0, null, this);
//        fileLoader.forceLoad();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(
                            broadcastReceiver,
                            new IntentFilter("LOL"));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onBackPressed() {

        if (!FilesModel.getInstance().hasPreviousDir()) {
            super.onBackPressed();
        }
        setupBackwardsNavigation();
    }

    private void setupBackwardsNavigation() {
        if (FilesModel.getInstance().hasPreviousDir()) {
            FilesModel.getInstance().getFilesToShow().clear();
            FilesModel.getInstance().setCurrentDir(FilesModel.getInstance().getPreviousDir());
            FilesModel.getInstance().setFilesToShow(
                    FilesModel.getInstance().getAllFilesInCurrDir(FilesModel.getInstance().getCurrentDir()));
            filesAdapter.notifyDataSetChanged();
        }
        setUpTitle();
        setupPrevText(FilesModel.getInstance().getPreviousDirName() + "/");
    }

    private void setupPrevText(String name) {
            if (Objects.equals(FilesModel.getInstance().getPreviousDirName(), "0")
                    || Objects.equals(FilesModel.getInstance().getPreviousDirName(), "0/")) {
                prevDirName.setText("root/");
            } else {
                prevDirName.setText(name);
            }
    }

    private void setUpTitle() {
        if (FilesModel.getInstance().hasPreviousDir()) {
            setTitle(FilesModel.getInstance().getCurrentDir().getName());
        } else {
            setTitle(getPackageManager().getApplicationLabel(this.getApplicationInfo()));
        }
    }

    //    @Override
//    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
//        fileLoader = new AsyncTaskLoader<List<File>>(this) {
//            @Override
//            public List<File> loadInBackground() {
//                return filesModel.getAllFilesInCurrDir(filesModel.getCurrentDir());
//            }
//        };
//        return fileLoader;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
//        files = data;
//        filesAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onLoaderReset(Loader<List<File>> loader) {
//
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQUEST_READ_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && Objects.equals(permissions[0], android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    FilesModel.getInstance().setFilesToShow(
                            FilesModel.getInstance().getAllFilesInCurrDir(FilesModel.getInstance().getCurrentDir()));
                }
            }
        }
    }

    private void requestDocumentsPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS}, Constants.REQUEST_READ_STORAGE_PERMISSION);
    }
}
