package com.example.acoste.projetimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Gallery extends AppCompatActivity{

    private ImageView img_gallery = null;
    private ImageView img_camera = null;
    private ImageView img_rotate = null;
    private ImageView img_effects = null;
    private ImageView img_menu = null;

    private Bitmap bitmap_gallery = null;
    private Bitmap bitmap_rotate = null;
    private Bitmap bitmap_camera = null;
    private Bitmap bitmap_effects = null;
    private Bitmap bitmap_menu = null;

    //Constant used in the onActivityResult function
    static final int RESULT_LOAD_IMG = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.gallery);

        //Initializing button to launch camera activity
        img_camera =  findViewById(R.id.camera_gallery);
        img_camera.setOnClickListener(listener_camera);
        bitmap_camera = BitmapFactory.decodeResource(getResources(), R.drawable.apps_camera_icon);
        img_camera.setImageBitmap(bitmap_camera);

        //Initializing button to launch gallery
        img_gallery = findViewById(R.id.gallery);
        img_gallery.setOnClickListener(listener_gallery);

        //Initializing button to rotate image
        img_rotate = findViewById(R.id.rotate);
        img_rotate.setOnClickListener(listener_rotate);
        bitmap_rotate = BitmapFactory.decodeResource(getResources(), R.drawable.fleche);
        img_rotate.setImageBitmap(bitmap_rotate);

        //Initializing button to launch effect activity
        img_effects = findViewById(R.id.effects_gallery);
        img_effects.setOnClickListener(listener_effects);
        bitmap_effects = BitmapFactory.decodeResource(getResources(), R.drawable.effect_logo);
        img_effects.setImageBitmap(bitmap_effects);

        //Initializing button to launch menu activity
        img_menu = findViewById(R.id.menu_gallery);
        img_menu.setOnClickListener(listener_menu);
        bitmap_menu = BitmapFactory.decodeResource(getResources(), R.drawable.menu_logo);
        img_menu.setImageBitmap(bitmap_menu);

        //Launches the gallery as soon as the user arrives on the gallery activity
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);

    }

    /***
     * Resize the bitmap passed as a parameter
     * @param image Bitmap that needs to be resize
     * @param maxSize Max size assigned to the bitmap
     * @return Bitmap
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /***
     * Retrieve the selected image from the gallery and convert it to a bitmap
     * @param reqCode Request requested by the code (==1 to recover a photo)
     * @param resultCode Result returned by code
     * @param data Intent launched by the function call
     */
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        if (reqCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                bitmap_gallery = BitmapFactory.decodeStream(imageStream);
                bitmap_gallery = getResizedBitmap(bitmap_gallery, 500);
                img_gallery.setImageBitmap(bitmap_gallery);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Une erreur s'est produite",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Vous n'avez pas choisi d'image", Toast.LENGTH_LONG).show();
        }
    }

    /***
     * Send to the effects activity
     */
    private View.OnClickListener listener_effects = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Gallery.this, EffectsActivity.class);
            //Converts the bitmap of the image into a byte array and sends it to the intent to find it in EffectsActivity
            if(bitmap_gallery != null){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap_gallery.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("imageGallery", byteArray);
            }
            startActivity(intent);
        }
    };

    /***
     * Rotates the image 90 degrees
     */
    private View.OnClickListener listener_rotate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            img_gallery.setRotation(img_gallery.getRotation()+90);
        }
    };

    /***
     * Send to the camera activity
     */
    private View.OnClickListener listener_camera = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Gallery.this, Camera.class);
            startActivity(intent);
        }
    };

    /***
     * Send to the menu activity
     */
    private View.OnClickListener listener_menu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Gallery.this, Main.class);
            startActivity(intent);
        }
    };

    /***
     * Launch to the gallery
     */
    private View.OnClickListener listener_gallery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        }

    };
}
