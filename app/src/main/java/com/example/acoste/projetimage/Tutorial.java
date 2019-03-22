package com.example.acoste.projetimage;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Tutorial extends AppCompatActivity {

    private ImageView img_gallery = null;
    private ImageView img_camera = null;
    //private ImageView img_effects = null;
    private ImageView img_menu = null;

    //private Bitmap bitmap_tuto = null;
    private Bitmap bitmap_gallery = null;
    private Bitmap bitmap_camera = null;
    private Bitmap bitmap_base_img = null;
    private Bitmap bitmap_menu = null;

    private String test_tuto_string = null;
    private ImageView imageView = null;
    private TextView test_tuto_textview = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial);

        img_gallery = findViewById(R.id.gallery_tuto);
        img_gallery.setOnClickListener(listener_gallery);
        bitmap_gallery = BitmapFactory.decodeResource(getResources(), R.drawable.gallery_icon);
        img_gallery.setImageBitmap(bitmap_gallery);

        img_camera = findViewById(R.id.camera_tuto);
        img_camera.setOnClickListener(listener_camera);
        bitmap_camera = BitmapFactory.decodeResource(getResources(), R.drawable.apps_camera_icon);
        img_camera.setImageBitmap(bitmap_camera);

        img_menu = findViewById(R.id.menu_tuto);
        img_menu.setOnClickListener(listener_menu);
        bitmap_menu = BitmapFactory.decodeResource(getResources(), R.drawable.menu_logo);
        img_menu.setImageBitmap(bitmap_menu);

        imageView = findViewById(R.id.img_tuto);
        bitmap_base_img = BitmapFactory.decodeResource(getResources(), R.drawable.test6);
        imageView.setImageBitmap(bitmap_base_img);
    }


    private View.OnClickListener listener_gallery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Tutorial.this, Gallery.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener listener_menu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Tutorial.this, Main.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener listener_camera = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Tutorial.this, Camera.class);
            startActivity(intent);
        }
    };
}
