package com.example.acoste.projetimage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Gallery extends AppCompatActivity{

    private Button button_camera = null;
    private Button button_effects = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        button_effects = (Button) findViewById(R.id.effects_gallery);
        button_effects.setOnClickListener(listener_effects);

        button_camera = (Button) findViewById(R.id.camera_gallery);
        button_camera.setOnClickListener(listener_camera);
    }

    private View.OnClickListener listener_effects = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Gallery.this, EffectsActivity.class);

            startActivity(intent);
        }
    };

    private View.OnClickListener listener_camera = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Gallery.this, Camera.class);

            startActivity(intent);
        }
    };
}
