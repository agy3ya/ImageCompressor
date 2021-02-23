package com.agy3ya.imagecompressor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SEND";
    private AppCompatButton chooseButton;
    private AppCompatButton compressButton;
    private AppCompatButton shareButton;
    private ImageView originalImage;
    private ImageView compressedImage;
    private TextView originalTextView;
    private TextView compressedTextView;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;
    private Bitmap bitmap;
    private Bitmap imageBitmap;
    private Context context;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        originalImage = findViewById(R.id.originalImage);
        chooseButton = findViewById(R.id.chooseButton);
        compressedImage = findViewById(R.id.compressedImage);
        compressButton = findViewById(R.id.compressButton);
        shareButton = findViewById(R.id.shareButton);
        originalTextView =findViewById(R.id.originalTextView);
        compressedTextView=findViewById(R.id.compressedTextView);
        compressButton.setEnabled(false);
        shareButton.setEnabled(false);
        setupClickListener();

    }

    private void setupClickListener() {

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Pick a Image "), PICK_IMAGE);
            }
        });
        compressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageBitmap = Compressor.reduceBitmapSize(bitmap, 307200);
                    compressedImage.setImageBitmap(imageBitmap);
                    String resolution = calculateResolution(imageBitmap);
                    compressedTextView.setText(resolution);
                    shareButton.setEnabled(true);
                }
            });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Uri myuri = bitmapToUri(imageBitmap);
               Intent intent = new Intent(Intent.ACTION_SEND);
               intent.putExtra(Intent.EXTRA_STREAM,myuri);
               intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
               intent.setType("image/*");
               startActivity(intent);
            }
        });

     }

    private Uri bitmapToUri(Bitmap bitmap) {
        File imagesFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),BuildConfig.APPLICATION_ID + ".provider",file);

        } catch (IOException e) {
            Log.d(TAG, "execpetion while sharing the file :" + e.getMessage());
        }
        return uri;
    }

    @Override
        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
                imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    originalImage.setImageBitmap(bitmap);
                    compressButton.setEnabled(true);
                    String resolution = calculateResolution(bitmap);
                    originalTextView.setText(resolution);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        private String calculateResolution(Bitmap bitmap){
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            return String.valueOf(width) + "x" + String.valueOf(height);
        }



    }
