package com.mobidev.testfilemanager;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by olga on 28.02.17.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder> {

    private Activity context;

    public FilesAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        return new FilesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FilesViewHolder holder, final int position) {
        if (FilesModel.getInstance().getFilesToShow().get(position).isDirectory()) {
            holder.fileLogo.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_folder_blue_24dp));
        } else {
            holder.fileLogo.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.ic_subject_blue_24dp));
        }
        holder.fileName.setText(FilesModel.getInstance().getFilesToShow().get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File selectedFile = FilesModel.getInstance().getFilesToShow().get(holder.getAdapterPosition());

                if (selectedFile.isDirectory()) {
                    context.setTitle(selectedFile.getName());
                    FilesModel.getInstance().setPreviousDir(FilesModel.getInstance().getCurrentDir());
                    FilesModel.getInstance().setCurrentDir(selectedFile);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("com.mobidev.testfilemanager.PREVDIRNAME").putExtra(Constants.PREV_NAME, FilesModel.getInstance().getPreviousDirName()));
                    FilesModel.getInstance().getFilesToShow().clear();
                    FilesModel.getInstance().setFilesToShow(FilesModel.getInstance().getAllFilesInCurrDir(selectedFile));
                    notifyDataSetChanged();
                } else {
                    Uri clickedFile = Uri.fromFile(FilesModel.getInstance().getFilesToShow().get(holder.getAdapterPosition()));
                    openFile(clickedFile);
                }
            }
        });
    }

    private void openFile(Uri fileUri) {

        String mimeType = FilesModel.getInstance().getMimeType(fileUri);

        if (mimeType != null) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(fileUri, mimeType);
                context.startActivity(i);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "The System understands this file type," +
                                       "but no applications are installed to handle it.",
                               Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "System doesn't know how to handle that file type!",
                           Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (FilesModel.getInstance().getFilesToShow() != null) {
            size = FilesModel.getInstance().getFilesToShow().size();
        }
        return size;
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
