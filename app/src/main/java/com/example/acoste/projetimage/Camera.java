package com.example.acoste.projetimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera extends AppCompatActivity {



    private Button button_gallery = null;
    private Button button_effects = null;
    private Button button_menu = null ;
    private Button button_camera = null ;
    private ImageView imageView = null;
    String mCurrentPhotoPath = null;

    static final int REQUEST_IMAGE_CAPTURE = 1;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        button_effects = (Button) findViewById(R.id.effects_camera);
        button_effects.setOnClickListener(listener_effects);

        button_gallery = (Button) findViewById(R.id.galery_camera);
        button_gallery.setOnClickListener(listener_gallery);

        button_menu = (Button) findViewById(R.id.menu_camera);
        button_menu.setOnClickListener(listener_menu);

        button_camera = (Button) findViewById(R.id.camera);
        button_camera.setOnClickListener(listener_camera);

        imageView = findViewById(R.id.imageView);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }


    }



    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

/*
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.provider",
                        photoFile);

                if(photoURI != null){
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
                else
                    System.out.println("PhotoURI null\n");

            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    */

    private View.OnClickListener listener_effects = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Camera.this, EffectsActivity.class);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ((BitmapDrawable)imageView.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra("image", byteArray);

            startActivity(intent);
        }
    };

    private View.OnClickListener listener_gallery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Camera.this, Gallery.class);

            startActivity(intent);
        }
    };

    private View.OnClickListener listener_menu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Camera.this, Main.class);

            startActivity(intent);
        }
    };

    private View.OnClickListener listener_camera = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                dispatchTakePictureIntent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
