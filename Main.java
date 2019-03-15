package com.example.acoste.projetimage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

//import android.support.v8.renderscript.RenderScript;

public class Main extends AppCompatActivity {

    private Button button_tuto = null;
    private Button button_gallery = null;
    private Button button_camera = null;

    private String test_main = "Menu 1234";
    private String code_string = "code_string_main";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);




        button_tuto = findViewById(R.id.tuto_main);
        button_tuto.setOnClickListener(listener_tuto);

        button_camera = findViewById(R.id.camera_main);
        button_camera.setOnClickListener(listener_camera);

        button_gallery = findViewById(R.id.gallery_main);
        button_gallery.setOnClickListener(listener_gallery);


    }

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
