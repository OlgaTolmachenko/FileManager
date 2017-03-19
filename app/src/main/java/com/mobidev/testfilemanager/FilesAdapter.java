package com.mobidev.testfilemanager;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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

import java.io.File;
import java.util.List;

/**
 * Created by olga on 28.02.17.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder> {

    private Activity context;
    private FilesModel filesModel;
    private List<File> files;

    public FilesAdapter(Activity context, List<File> files) {
        this.context = context;
        filesModel = new FilesModel();
//        this.files = filesModel.getAllFiles(filesModel.getCurrentDir());
        this.files = files;
    }

    @Override
    public FilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        return new FilesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FilesViewHolder holder, final int position) {
        //TODO почему не разными типами и холдерами?
        if (files.get(position).isDirectory()) {
            holder.fileLogo.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_folder_blue_24dp));
        } else {
            holder.fileLogo.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_subject_blue_24dp));
        }
        holder.fileName.setText(files.get(position).getName());

        File selectedFile = files.get(position);
        holder.setSelectedFile(selectedFile);
    }

    private void openFile(Uri fileUri) {

        String mimeType = filesModel.getMimeType(fileUri);
        if (TextUtils.isEmpty(mimeType)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, mimeType);
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "The System understands this file type," +
                                "but no applications are installed to handle it.",
                        Toast.LENGTH_LONG).show();


//                Intent i = new Intent(Intent.ACTION_VIEW);

//                try {
//                    if (mimeType != null) {
//
//                        i.setDataAndType(fileUri, mimeType);
//
//                    } else {
//                        i.setType("*/*");
//                    }
//                    context.startActivity(i);
//                } catch (ActivityNotFoundException e) {
//                    Toast.makeText(context, "The System understands this file type," +
//                                    "but no applications are installed to handle it.",
//                            Toast.LENGTH_LONG).show();
//                }


            }
        }
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
                files = filesModel.getAllFiles(selectedFile);
                notifyDataSetChanged();
                sendFileSelectedEvent();
            } else {
                Uri selectedFileUri = Uri.fromFile(selectedFile);
                openFile(selectedFileUri);
            }
        }

        private void sendFileSelectedEvent() {
            FileSelectedEvent fileSelectedEvent = new FileSelectedEvent();
            fileSelectedEvent.setSelectedFile(selectedFile);
            EventBus.getDefault().post(fileSelectedEvent);
        }
    }
}
