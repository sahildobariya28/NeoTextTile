package com.example.texttile.presentation.screen.features.storage_feature.storage_file;

import static com.example.texttile.presentation.ui.util.DialogUtil.emptyRecordDialog;

import android.app.Activity;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import javax.annotation.Nonnull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.texttile.R;
import com.example.texttile.databinding.StorageImageItemBinding;
import com.example.texttile.data.model.JalaWithFileModel;
import com.example.texttile.presentation.ui.lib.snackUtil.CustomSnackUtil;
import com.example.texttile.presentation.ui.lib.snackUtil.DownloadSnackUtil;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class StorageImageAdapter extends RecyclerView.Adapter<StorageImageAdapter.ViewHolder> {

    ArrayList<JalaWithFileModel> jalaWithFileModels = new ArrayList<>();
    Activity activity;

    public StorageImageAdapter(Activity activity, ArrayList<JalaWithFileModel> jalaWithFileModels) {
        this.activity = activity;
        this.jalaWithFileModels = jalaWithFileModels;
    }

    @Nonnull
    @Override
    public ViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        StorageImageItemBinding binding = StorageImageItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@Nonnull ViewHolder holder, int position) {

        Animation animation = AnimationUtils.loadAnimation(activity, R.anim.scale);
        holder.binding.layoutMain.startAnimation(animation);

        if (jalaWithFileModels.get(position).getFile_uri() != null) {
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(jalaWithFileModels.get(position).getFile_uri());
            holder.binding.textFileName.setText(photoRef.getName());
        }


        Glide.with(activity)
                .load(jalaWithFileModels.get(position).getFile_uri())
                .placeholder(R.drawable.file_icon)
                .into(holder.binding.imgDesign);

        holder.binding.btnDownload.setOnClickListener(view -> {
            downloadFile(jalaWithFileModels.get(position).getFile_uri());

        });

    }

    private void downloadFile(String string_uri) {


        if (string_uri != null && !string_uri.isEmpty()) {
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(string_uri);

            DownloadSnackUtil downloadSnackUtil = new DownloadSnackUtil();

            downloadSnackUtil.showSnack(activity, "Downloading");


            //  /storage/emulated/0/Download/Nature.ep
            final File rootPath = new File(Environment.getExternalStorageDirectory(), "Download");

            if (!rootPath.exists()) {
                rootPath.mkdirs();
            }


            final File localFile = new File(rootPath, "Nature.ep");

            photoRef.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        downloadSnackUtil.dismissSnack();
                        new CustomSnackUtil().showSnack(activity, "Download successful", R.drawable.img_true);

                    })
                    .addOnFailureListener(exception -> {
                        new CustomSnackUtil().showSnack(activity, "Download Failed Please Try Again!", R.drawable.error_msg_icon);
                    })
                    .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@Nonnull FileDownloadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            downloadSnackUtil.updateProgress(progress);
                        }
                    });
        } else {
            emptyRecordDialog(activity);
        }
    }

    @Override
    public int getItemCount() {
        return jalaWithFileModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        StorageImageItemBinding binding;

        public ViewHolder(@Nonnull StorageImageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
