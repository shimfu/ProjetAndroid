package com.example.acoste.projetimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera extends AppCompatActivity {

    private ImageView img_gallery = null;
    private ImageView img_rotate = null;
    private ImageView img_camera = null;
    private ImageView img_effects = null;
    private ImageView img_menu = null;

    private Bitmap bitmap_gallery = null;
    private Bitmap bitmap_rotate = null;
    private Bitmap bitmap_camera = null;
    private Bitmap bitmap_effects = null;
    private Bitmap bitmap_menu = null;

    private Uri photoUri;

    //Constant used in the onActivityResult function
    static final int REQUEST_TAKE_PHOTO = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.camera);

        //Initializing button to lauch camera
        img_camera = findViewById(R.id.camera);
        img_camera.setOnClickListener(listener_camera);

        //Initializing button to rotate image
        img_rotate = findViewById(R.id.rotate);
        img_rotate.setOnClickListener(listener_rotate);
        bitmap_rotate = BitmapFactory.decodeResource(getResources(), R.drawable.fleche);
        img_rotate.setImageBitmap(bitmap_rotate);

        //Initializing button to lauch gallery activity
        img_gallery = findViewById(R.id.gallery_camera);
        img_gallery.setOnClickListener(listener_gallery);
        bitmap_gallery = BitmapFactory.decodeResource(getResources(), R.drawable.gallery_icon);
        img_gallery.setImageBitmap(bitmap_gallery);

        //Initializing button to lauch effect activity
        img_effects = findViewById(R.id.effects_camera);
        img_effects.setOnClickListener(listener_effects);
        bitmap_effects = BitmapFactory.decodeResource(getResources(), R.drawable.effect_logo);
        img_effects.setImageBitmap(bitmap_effects);

        //Initializing button to lauch menu activity
        img_menu = findViewById(R.id.menu_camera);
        img_menu.setOnClickListener(listener_menu);
        bitmap_menu = BitmapFactory.decodeResource(getResources(), R.drawable.menu_logo);
        img_menu.setImageBitmap(bitmap_menu);

        //Launches the camera as soon as the user arrives on the camera activity
        dispatchTakePictureIntent();
    }

    /***
     *Gives the path where the image will be stored at photoFile and launches the camera
     */
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
                photoUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileproviderProjetAndroid",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    
    /***
     *
     * @param requestCode Request requested by the code (==1 to take a photo)
     * @param resultCode Result returned by code
     * @param data Intent launched by the function call (not used)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to and Make sure the request was successful
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                bitmap_camera = MediaStore.Images.Media.getBitmap(this.getContentResolver(),photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            img_camera.setImageBitmap(bitmap_camera);
        }
    }

    /***
     *
     * @return File path where the image will be stored
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    /***
     * Send to the effects activity
     */
    private View.OnClickListener listener_effects = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Camera.this, EffectsActivity.class);
            //Envoie la photoUri à l'intent pour retrouver l'image stockée dans EffectsActivity
            if(photoUri != null)
                intent.putExtra("imageUri", photoUri.toString());
            startActivity(intent);
        }
    };

    /***
     * Rotates the image 90 degrees
     */
    private View.OnClickListener listener_rotate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            img_camera.setRotation(img_camera.getRotation()+90);
        }
    };

    /***
     * Send to the gallery activity
     */
    private View.OnClickListener listener_gallery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Camera.this, Gallery.class);
            startActivity(intent);
        }
    };

    /***
     * Send to the menu activity
     */
    private View.OnClickListener listener_menu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Camera.this, Main.class);
            startActivity(intent);
        }
    };

    /***
     * Launch the camera
     */
    private View.OnClickListener listener_camera = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dispatchTakePictureIntent();
        }
    };

}
