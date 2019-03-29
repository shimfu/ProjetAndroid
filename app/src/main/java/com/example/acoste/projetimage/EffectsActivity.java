package com.example.acoste.projetimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;


public class EffectsActivity extends AppCompatActivity{

    private ImageView img_gallery = null;
    private ImageView img_camera = null;
    private ImageView img_menu = null;

    private Bitmap bitmap_gallery = null;
    private Bitmap bitmap_camera = null;
    private Bitmap bitmap_menu = null;

    private Effects effect;

    private ImageView img;
    private Bitmap bmpInit;
    private Bitmap bmp;
    private int[] save;

    private ImageView img_greyRS;
    private ImageView img_keepColorRS;
    private ImageView img_comboEffects;
    private ImageView img_randomHueRS;
    private ImageView img_blur;
    private ImageView img_outline;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects);

        /***************************************
         Initialisation des boutons de navigation
         ***************************************/
        img_camera =  findViewById(R.id.camera);
        img_camera.setOnClickListener(listener_camera);
        bitmap_camera = BitmapFactory.decodeResource(getResources(), R.drawable.apps_camera_icon);
        img_camera.setImageBitmap(bitmap_camera);

        img_gallery = findViewById(R.id.gallery);
        img_gallery.setOnClickListener(listener_gallery);
        bitmap_gallery = BitmapFactory.decodeResource(getResources(), R.drawable.gallery_icon);
        img_gallery.setImageBitmap(bitmap_gallery);

        img_menu = findViewById(R.id.menu);
        img_menu.setOnClickListener(listener_menu);
        bitmap_menu = BitmapFactory.decodeResource(getResources(), R.drawable.menu_logo);
        img_menu.setImageBitmap(bitmap_menu);

        img = findViewById(R.id.img_to_modify);

        //Récupère l'Uri envoyé via Camera.activity et le convertit en bitmap
        Uri photoUri;
        if(getIntent().getStringExtra("imageUri") != null){
            photoUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            try {
                bmpInit = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //Récupère le tableau de byte envoyé via Gallery.activity et le convertit en bitmap
        else if(getIntent().getByteArrayExtra("imageGallery") != null){
            byte[] byteArray = getIntent().getByteArrayExtra("imageGallery");
            bmpInit = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        //Applique une photo par défaut à la bitmap si aucune n'a été envoyée depuis la caméra ou la gallerie
        else
            bmpInit = BitmapFactory.decodeResource(getResources(), R.drawable.test0);

        img.setImageBitmap(bmpInit);

        save = new int[bmpInit.getWidth() * bmpInit.getHeight()];
        bmpInit.getPixels(save, 0, bmpInit.getWidth(), 0, 0, bmpInit.getWidth(), bmpInit.getHeight());

        bmp = bmpInit.copy(bmpInit.getConfig(), true);

        effect = new Effects(bmp);

        img.setOnClickListener(listener_zoom);

        /***************************************
         Initialisation des boutons des différents effets
         ***************************************/
        img_greyRS =  findViewById(R.id.img_greyRs);
        img_greyRS.setOnClickListener(listener_greyRS);
        effect.toGreyRS(getApplicationContext());
        img_greyRS.setImageBitmap(effect.getCurrentImg());
        effect.reset();


        img_keepColorRS =  findViewById(R.id.img_keepColorRS);
        img_keepColorRS.setOnClickListener(listener_keepColorRS);
        effect.keepColorRS(50, getApplicationContext());
        img_keepColorRS.setImageBitmap(effect.getCurrentImg());
        effect.reset();

        img_comboEffects =  findViewById(R.id.img_comboEffects);
        img_comboEffects.setOnClickListener(listener_comboEffects);
        effect.comboEffects(getApplicationContext(), 5);
        img_comboEffects.setImageBitmap(effect.getCurrentImg());
        effect.reset();

        img_randomHueRS =  findViewById(R.id.img_randomHueRS);
        img_randomHueRS.setOnClickListener(listener_randomHueRS);
        effect.randomHueRS(getApplicationContext());
        img_randomHueRS.setImageBitmap(effect.getCurrentImg());
        effect.reset();

        img_blur =  findViewById(R.id.img_blur);
        img_blur.setOnClickListener(listener_blur);
        int mask[][] = Convolution.mask_moy(5);
        effect.blur(5, mask);
        img_blur.setImageBitmap(effect.getCurrentImg());
        effect.reset();

        img_outline =  findViewById(R.id.img_outline);
        img_outline.setOnClickListener(listener_outline);
        effect.outline();
        img_outline.setImageBitmap(effect.getCurrentImg());
        effect.reset();

        Button button_linear_contrast_ARGB = findViewById(R.id.button_linear_contrast_ARGB);
        button_linear_contrast_ARGB.setOnClickListener(listener_linearContrast_ARGB);

        Button button_linear_contrast_HSV = findViewById(R.id.button_linear_contrast_HSV);
        button_linear_contrast_HSV.setOnClickListener(listener_linearContrast_HSV);

        Button button_equalization_contrast_hsv = findViewById(R.id.button_equalization_contrast_hsv);
        button_equalization_contrast_hsv.setOnClickListener(listener_equalizationContrast_HSV);

        Button button_equalization_contrast_argb = findViewById(R.id.button_equalization_contrast_argb);
        button_equalization_contrast_argb.setOnClickListener(listener_equalizationContrast_ARGB);

        Button button_linearContrastRS = findViewById(R.id.button_linearContrastRS);
        button_linearContrastRS.setOnClickListener(listener_linearContrastRS);

        Button button_equalization_contrast_RS = findViewById(R.id.button_equalization_contrast_RS);
        button_equalization_contrast_RS.setOnClickListener(listener_equalizationContrastRS);

        Button button_blur_moy_RS = findViewById(R.id.button_blur_moy_RS);
        button_blur_moy_RS.setOnClickListener(listener_blur_moy_RS);

        Button button_blur_gaussian5x5_RS = findViewById(R.id.button_blur_gaussian5x5_RS);
        button_blur_gaussian5x5_RS.setOnClickListener(listener_blur_gaussian5x5_RS);

        Button button_sobel_horizontal_RS = findViewById(R.id.button_sobel_horizontal_RS);
        button_sobel_horizontal_RS.setOnClickListener(listener_sobel_horizontal_RS);

        Button button_sobel_vertical_RS = findViewById(R.id.button_sobel_vertical_RS);
        button_sobel_vertical_RS.setOnClickListener(listener_sobel_vertical_RS);

        Button button_laplacian_mask_RS = findViewById(R.id.button_laplacian_mask_RS);
        button_laplacian_mask_RS.setOnClickListener(listener_laplacian_mask_RS);

        Button button_reset = findViewById(R.id.button15);
        button_reset.setOnClickListener(listener_reset);


    }

    private View.OnClickListener listener_reset = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.reset();
            img.setImageBitmap(effect.getInitialImg());
        }
    };

    private View.OnClickListener listener_zoom= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(EffectsActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
            PhotoView photoView = mView.findViewById(R.id.imageView);
            photoView.setImageResource(R.drawable.test0);
            photoView.setImageBitmap(bmp);
            mBuilder.setView(mView);
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }
    };

    /***************************************
     Listener des boutons de navigation
     ***************************************/
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


    /***************************************
    Listener des boutons des différents effets
     ***************************************/
    private View.OnClickListener listener_greyRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.toGreyRS(getApplicationContext());
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_keepColorRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.keepColorRS(50, getApplicationContext());
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_comboEffects = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.comboEffects(getApplicationContext(), 5);
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_randomHueRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.randomHueRS(getApplicationContext());
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_outline = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.outline();
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_blur = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int mask[][] = Convolution.mask_moy(5);
            effect.blur(5, mask);
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_linearContrast_ARGB = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.linear_contrast_ARGB();
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_linearContrast_HSV = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.linear_contrast_HSV(50);
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_equalizationContrast_HSV = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.equalization_contrast_HSV(300);
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_equalizationContrast_ARGB = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.equalization_contrast_ARGB();
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_linearContrastRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.linearContrastRS(getApplicationContext());
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_equalizationContrastRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.equalization_contrast_RS(getApplicationContext());
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_blur_moy_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.blur_moy_RS(getApplicationContext(), 3); //to correct later
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_blur_gaussian5x5_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.blur_gaussian5x5_RS(getApplicationContext());
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_sobel_horizontal_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.sobel_horizontal_RS(getApplicationContext());
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_sobel_vertical_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.sobel_vertical_RS(getApplicationContext());
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

    private View.OnClickListener listener_laplacian_mask_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.laplacian_mask_RS(getApplicationContext());
            img.setImageBitmap(effect.getCurrentImg());
        }
    };

}