package com.example.imagecompressor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

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
        originalTextView =findViewById(R.id.originalTextView);
        compressedTextView=findViewById(R.id.compressedTextView);
        compressButton.setEnabled(false);
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
                }
            });

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
