package com.example.acoste.projetimage;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
//import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Main extends AppCompatActivity {

    private ImageView img_tuto = null;
    private ImageView img_gallery = null;
    private ImageView img_camera = null;

    private ImageView base_img = null;
    private Bitmap bitmap_base_img = null;
    private Bitmap bitmap_tuto = null;
    private Bitmap bitmap_gallery = null;
    private Bitmap bitmap_camera = null;

    private String test_main = "Menu 1234";
    private String code_string = "code_string_main";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.main_layout);

        /***************************************
         Initialisation des boutons de navigation
         ***************************************/
        img_tuto =  findViewById(R.id.tuto_main);
        img_tuto.setOnClickListener(listener_tuto);
        bitmap_tuto = BitmapFactory.decodeResource(getResources(), R.drawable.tuto_icon);
        img_tuto.setImageBitmap(bitmap_tuto);

        img_camera =  findViewById(R.id.camera_main);
        img_camera.setOnClickListener(listener_camera);
        bitmap_camera = BitmapFactory.decodeResource(getResources(), R.drawable.apps_camera_icon);
        img_camera.setImageBitmap(bitmap_camera);

        img_gallery = findViewById(R.id.gallery_main);
        img_gallery.setOnClickListener(listener_gallery);
        bitmap_gallery = BitmapFactory.decodeResource(getResources(), R.drawable.gallery_icon);
        img_gallery.setImageBitmap(bitmap_gallery);

        base_img = findViewById(R.id.base_img);
        bitmap_base_img = BitmapFactory.decodeResource(getResources(), R.drawable.test5);
        base_img.setImageBitmap(bitmap_base_img);
    }

    /***************************************
     Listener des boutons de navigation
     ***************************************/
    private View.OnClickListener listener_tuto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Main.this, Tutorial.class);
            intent.putExtra(code_string, test_main);
            startActivity(intent);
        }
    };

    private View.OnClickListener listener_gallery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Main.this, Gallery.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener listener_camera = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Main.this, Camera.class);
            startActivity(intent);
        }
    };
}
