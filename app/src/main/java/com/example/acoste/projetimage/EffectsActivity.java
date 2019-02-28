package com.example.acoste.projetimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;


public class EffectsActivity extends AppCompatActivity{

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

    /*public View.OnClickListener setInitialImgListener = new View.OnClickListener() {
        public void onClick(View v) { Effects.setInitialImg(Effects.initialImg);}
    };*/

    public View.OnClickListener toGreyListener = new View.OnClickListener() {
        public void onClick(View v) { Simple.grey(bmp);}
    };

    /*public View.OnClickListener toGreyRSListener = new View.OnClickListener() {
        public void onClick(View v) { Simple.toGreyRS(Effects.currentImg, );}
    };*/

    public View.OnClickListener keepColorListener = new View.OnClickListener() {
        public void onClick(View v) { Simple.keepColor(bmp, 1);}
    };

    /*public View.OnClickListener keepColorRSListener = new View.OnClickListener() {
        public void onClick(View v) { Simple.keepColorRS(Effects.currentImg, );}
    };*/

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects);


        button_gallery = (Button) findViewById(R.id.gallery_effects);
        button_gallery.setOnClickListener(listener_gallery);

        button_camera = (Button) findViewById(R.id.camera_effects);
        button_camera.setOnClickListener(listener_camera);

        button_menu = (Button) findViewById(R.id.menu_effects_activty);
        button_menu.setOnClickListener(listener_menu);

        Button button0 = (Button) findViewById(R.id.button0);
        button0.setOnClickListener(toGreyListener);

        /*Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(toGreyRSListener);*/

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(keepColorListener);

        /*Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(keepColorRSListener);

        Button button15 = (Button) findViewById(R.id.button15);
        button15.setOnClickListener(setInitialImgListener);*/



        ImageView mIcon = (ImageView) findViewById(R.id.img_to_modify);
        RoundedBitmapDrawable mDrawable;
        /**  1 - TEMPORARY IMPLEMENTATION - 1 **/
        Uri photoUri = null;
        if(getIntent() != null)
            photoUri = Uri.parse(getIntent().getStringExtra("imageUri"));

        if(photoUri != null) {
            try {
                bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(bmp != null)
             mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bmp);
        else{
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test0);
            mDrawable = RoundedBitmapDrawableFactory.create(getResources(), bmp);
        }
        /**  1 - TEMPORARY IMPLEMENTATION - 1 **/
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
