package com.example.acoste.projetimage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class EffectsActivity extends AppCompatActivity {

    private Button button_camera = null;
    private Button button_gallery = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects);

        button_gallery = (Button) findViewById(R.id.gallery_effects);
        button_gallery.setOnClickListener(listener_gallery);

        button_camera = (Button) findViewById(R.id.camera_effects);
        button_camera.setOnClickListener(listener_camera);
    }

    private View.OnClickListener listener_gallery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(EffectsActivity.this, Gallery.class);

            startActivity(intent);
        }
    };

    private View.OnClickListener listener_camera = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(EffectsActivity.this, Camera.class);

            startActivity(intent);
        }
    };
}
