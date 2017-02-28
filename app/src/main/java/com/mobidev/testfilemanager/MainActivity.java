package com.mobidev.testfilemanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
//        implements LoaderManager.LoaderCallbacks<List<File>>
{

//    private AsyncTaskLoader<List<File>> fileLoader;
    private List<File> files;
    private FilesAdapter filesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestDocumentsPermissions();
        }

        files = FilesModel.getInstance().getAllFilesInCurrDir(FilesModel.getInstance().getCurrentDir());

        filesAdapter = new FilesAdapter(files, this);

        RecyclerView filesRecycler = (RecyclerView) findViewById(R.id.fileRecycler);
        filesRecycler.setLayoutManager(new LinearLayoutManager(this));
        filesRecycler.setAdapter(filesAdapter);
        RecyclerView.ItemDecoration filesDivider = new Divider(getDrawable(R.drawable.list_divider));
        filesRecycler.addItemDecoration(filesDivider);

//        getLoaderManager().initLoader(0, null, this);
//        fileLoader.forceLoad();
    }

    @Override
    public void onBackPressed() {
        if (FilesModel.getInstance().hasPreviousDir()) {
            files.clear();
            FilesModel.getInstance().setCurrentDir(FilesModel.getInstance().getPreviousDir());
            files = FilesModel.getInstance().getAllFilesInCurrDir(FilesModel.getInstance().getCurrentDir());
            filesAdapter.notifyDataSetChanged();
        }
//        super.onBackPressed();
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

                    files = FilesModel.getInstance().getAllFilesInCurrDir(FilesModel.getInstance().getCurrentDir());
                }
            }
        }
    }

    private void requestDocumentsPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_DOCUMENTS}, Constants.REQUEST_READ_STORAGE_PERMISSION);
    }
}
