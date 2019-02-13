package com.example.acoste.projetimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class EffectsActivity extends AppCompatActivity {

    private Button button_camera = null;
    private Button button_gallery = null;
    private Button button_menu = null ;

    /** 1 - TEMPORARY IMPLEMENTATION - 1 **/
    private Button button_test = null;
    private ImageView img;
    private Bitmap bmp;
    /** 1 - TEMPORARY IMPLEMENTATION - 1 **/




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects);

        button_gallery = (Button) findViewById(R.id.gallery_effects);
        button_gallery.setOnClickListener(listener_gallery);

        button_camera = (Button) findViewById(R.id.camera_effects);
        button_camera.setOnClickListener(listener_camera);

        button_menu = (Button) findViewById(R.id.menu_effects_activty);
        button_menu.setOnClickListener(listener_menu);

        /**  1 - TEMPORARY IMPLEMENTATION - 1 **/
        button_test = (Button) findViewById(R.id.test);
        button_test.setOnClickListener(listener_test);
        img = (ImageView) findViewById(R.id.img_to_modify);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test0);
        img.setImageBitmap(bmp);
        /**  1 - TEMPORARY IMPLEMENTATION - 1 **/
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

    private View.OnClickListener listener_menu = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(EffectsActivity.this, Main.class);

            startActivity(intent);
        }
    };

    /**  1 - TEMPORARY IMPLEMENTATION - 1 **/
    private View.OnClickListener listener_test = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Simple simple = new Simple(bmp);
            bmp = simple.keepColor(bmp, 50);
            img.setImageBitmap(bmp);
        }
    };
    /** 1 - TEMPORARY IMPLEMENTATION - 1 **/

}
