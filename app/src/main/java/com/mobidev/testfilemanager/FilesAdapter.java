package com.mobidev.testfilemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by olga on 28.02.17.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder> {

    List<File> filesList;
    private Activity context;

    public FilesAdapter(List<File> filesList, Activity context) {
        this.filesList = filesList;
        this.context = context;
    }

    @Override
    public FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        return new FilesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FilesViewHolder holder, final int position) {
        if (filesList.get(position).isDirectory()) {
            holder.fileLogo.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_folder_blue_24dp));
        } else {
            holder.fileLogo.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_subject_blue_24dp));
        }
        holder.fileName.setText(filesList.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File selectedFile = filesList.get(position);

                if (selectedFile.isDirectory()) {
                    FilesModel.getInstance().setPreviousDir(FilesModel.getInstance().getCurrentDir());
                    FilesModel.getInstance().setCurrentDir(selectedFile);
                    context.setTitle(selectedFile.getName());
                    filesList.clear();
                    filesList = FilesModel.getInstance().getAllFilesInCurrDir(selectedFile);
                    notifyDataSetChanged();
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri clickedFile = Uri.fromFile(filesList.get(position));
                    intent.setDataAndType(clickedFile, FilesModel.getInstance().getMimeType(clickedFile));
                    if (holder.itemView.getContext().getContentResolver() != null) {
                        holder.itemView.getContext().startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    public static class FilesViewHolder extends RecyclerView.ViewHolder {
        public ImageView fileLogo;
        public TextView fileName;

        public FilesViewHolder(View itemView) {
            super(itemView);
            fileLogo = (ImageView) itemView.findViewById(R.id.fileLogo);
            fileName = (TextView) itemView.findViewById(R.id.fileName);
        }
    }
}
