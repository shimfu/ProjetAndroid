package com.example.acoste.projetimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;


public class EffectsActivity extends AppCompatActivity {

    private Button button_camera = null;
    private Button button_gallery = null;
    private Button button_menu = null ;

    private int number_of_img = 15;

    private Button img_button[] = new Button[number_of_img];
    private int name_img_button[] = new int[number_of_img];

    /** 1 - TEMPORARY IMPLEMENTATION - 1 **/
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
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        if(byteArray != null)
            bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        img = (ImageView) findViewById(R.id.img_to_modify);
        if(bmp != null)
            img.setImageBitmap(bmp);
        else{
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test0);
            img.setImageBitmap(bmp);
        }
        /**  1 - TEMPORARY IMPLEMENTATION - 1 **/

        /**Test du zoom **/

        ImageView mIcon = (ImageView) findViewById(R.id.img_to_modify);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test0);
        RoundedBitmapDrawable mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        mDrawable.setCircular(true);
        mIcon.setImageDrawable(mDrawable);

        mIcon.setOnClickListener(listener_zoom);

    }

    private View.OnClickListener listener_zoom= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(EffectsActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
            PhotoView photoView = mView.findViewById(R.id.imageView);
            photoView.setImageResource(R.drawable.test0);
            mBuilder.setView(mView);
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }
    };

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
