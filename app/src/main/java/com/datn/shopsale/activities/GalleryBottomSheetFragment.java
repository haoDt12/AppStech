package com.datn.shopsale.activities;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.shopsale.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GalleryBottomSheetFragment extends BottomSheetDialogFragment {
    private RecyclerView recyImgGallary;
    private ImageView imgSend;
    private List<Bitmap> imagePaths;



    public GalleryBottomSheetFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_gallary_dialog,container,false);
        recyImgGallary = (RecyclerView) view.findViewById(R.id.recy_imgGallary);
        imgSend = (ImageView)  view.findViewById(R.id.img_send);
        getAllImagesAsBitmaps();

        return  view;
    }
    public  List<Bitmap> getAllImagesAsBitmaps() {
        List<Bitmap> imageBitmaps = new ArrayList<>();

        String[] projection = {MediaStore.Images.Media._ID};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder)) {

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndexId = cursor.getColumnIndex(MediaStore.Images.Media._ID);

                do {
                    long imageId = cursor.getLong(columnIndexId);
                    Uri contentUri = Uri.withAppendedPath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            Long.toString(imageId)
                    );

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentUri);
                        imageBitmaps.add(bitmap);
                        Toast.makeText(getActivity(), ""+bitmap.toString(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } while (cursor.moveToNext());
            }
        }

        return imageBitmaps;
    }
}
