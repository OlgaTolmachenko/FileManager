package com.mobidev.testfilemanager;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

/**
 * Created by olga on 28.02.17.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder> {

    private FilesModel filesModel;
    private List<File> files;

    public FilesAdapter(List<File> files, FilesModel filesModel) {
        this.files = files;
        this.filesModel = filesModel;
    }

    @Override
    public FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        return new FilesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FilesViewHolder holder, final int position) {
        if (files.get(position).isDirectory()) {
            holder.fileLogo.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_folder_blue_24dp));
        } else {
            holder.fileLogo.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_subject_blue_24dp));
        }
        holder.fileName.setText(files.get(position).getName());

        File selectedFile = files.get(position);
        holder.setSelectedFile(selectedFile);
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (files != null) {
            size = files.size();
        }
        return size;
    }

    public class FilesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView fileLogo;
        public TextView fileName;
        private File selectedFile;

        public FilesViewHolder(View itemView) {
            super(itemView);
            fileLogo = (ImageView) itemView.findViewById(R.id.fileLogo);
            fileName = (TextView) itemView.findViewById(R.id.fileName);
            itemView.setOnClickListener(this);
        }

        public File getSelectedFile() {
            return selectedFile;
        }

        public void setSelectedFile(File selectedFile) {
            this.selectedFile = selectedFile;
        }

        @Override
        public void onClick(View view) {
            if (selectedFile.isDirectory()) {
                filesModel.setCurrentDir(selectedFile);
                files = filesModel.getAllFiles(filesModel.getCurrentDir());
                notifyDataSetChanged();
            }
            sendFileSelectedEvent();
        }

        private void sendFileSelectedEvent() {
            MessageEvent fileSelectedEvent = new MessageEvent();
            fileSelectedEvent.setSelectedFile(selectedFile);
            EventBus.getDefault().post(fileSelectedEvent);
        }
    }
}
