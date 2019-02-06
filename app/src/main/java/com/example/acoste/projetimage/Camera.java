package com.example.acoste.projetimage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Camera extends AppCompatActivity {



    private Button button_gallery = null;
    private Button button_effects = null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        button_effects = (Button) findViewById(R.id.effects_camera);
        button_effects.setOnClickListener(listener_effects);

        button_gallery = (Button) findViewById(R.id.galery_camera);
        button_gallery.setOnClickListener(listener_gallery);
    }

    private View.OnClickListener listener_effects = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(Camera.this, EffectsActivity.class);

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
}
