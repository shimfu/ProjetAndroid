package com.example.acoste.projetimage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class EffectsActivity extends AppCompatActivity{

    private ImageView img_gallery = null;
    private ImageView img_rotate = null;
    private ImageView img_camera = null;
    private ImageView img_menu = null;

    private Bitmap bitmap_gallery = null;
    private Bitmap bitmap_rotate = null;
    private Bitmap bitmap_camera = null;
    private Bitmap bitmap_menu = null;

    private Effects effectButton = null;
    private Effects effectImage = null;

    private ImageView img;
    private Bitmap bmpInit = null;
    private Bitmap bmpResize = null;
    private Bitmap bmp;
    private int[] save;

    private ImageView img_greyRS;
    private ImageView img_keepColorRS;
    private ImageView img_bilateralRS;
    private ImageView img_randomHueRS;
    private ImageView img_blurRS;
    private ImageView img_colorPartitionRS;
    private ImageView img_drawOutlineRS;
    private ImageView img_histEqRS;
    private ImageView img_linearContrastRS;
    private ImageView img_medianFilterRS;
    private ImageView img_minFilterRS;
    private ImageView img_sobelGradientRS;

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

        img_rotate = findViewById(R.id.rotate);
        img_rotate.setOnClickListener(listener_rotate);
        bitmap_rotate = BitmapFactory.decodeResource(getResources(), R.drawable.fleche);
        img_rotate.setImageBitmap(bitmap_rotate);

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

        //img.setImageBitmap(bmpInit);


        effectImage = new Effects(bmpInit);



        bmpResize = Bitmap.createScaledBitmap(bmpInit, 100, 100, false);
        img.setImageBitmap(effectImage.getCurrentImg());

        effectButton = new Effects(bmpResize);


        //img.setImageBitmap(bmpInit);

        img.setOnClickListener(listener_zoom);


        /***************************************
         Initialisation des boutons des différents effets
         ***************************************/
        img_greyRS =  findViewById(R.id.img_greyRS);
        img_greyRS.setOnClickListener(listener_greyRS);
        effectButton.toGreyRS(getApplicationContext());
        img_greyRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        img_keepColorRS =  findViewById(R.id.img_keepColorRS);
        img_keepColorRS.setOnClickListener(listener_keepColorRS);
        effectButton.keepColorRS(40, getApplicationContext());
        img_keepColorRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        img_bilateralRS =  findViewById(R.id.img_bilateralRS);
        img_bilateralRS.setOnClickListener(listener_bilateralRS);
        effectButton.bilateral_filter_RS(getApplicationContext(), 1);
        img_bilateralRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        img_randomHueRS =  findViewById(R.id.img_randomHueRS);
        img_randomHueRS.setOnClickListener(listener_randomHueRS);
        effectButton.randomHueRS(getApplicationContext());
        img_randomHueRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        img_blurRS =  findViewById(R.id.img_blurRS);
        img_blurRS.setOnClickListener(listener_blur_moy_RS);
        effectButton.blur_moy_RS(getApplicationContext(), 3);
        img_blurRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        img_colorPartitionRS =  findViewById(R.id.img_colorPartitionRS);
        img_colorPartitionRS.setOnClickListener(listener_colorPartition_RS);
        effectButton.colorPartition_RS(getApplicationContext(), 9 , 3 ,0);
        img_colorPartitionRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        img_drawOutlineRS =  findViewById(R.id.img_drawOutlineRS);
        img_drawOutlineRS.setOnClickListener(listener_drawOutline_RS);
        effectButton.drawOutline_RS(getApplicationContext(), 0.3f);
        img_drawOutlineRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        img_histEqRS =  findViewById(R.id.img_histEqRS);
        img_histEqRS.setOnClickListener(listener_equalizationContrastRS);
        effectButton.equalization_contrast_RS(getApplicationContext());
        img_histEqRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        img_linearContrastRS =  findViewById(R.id.img_linearContrastRS);
        img_linearContrastRS.setOnClickListener(listener_linearContrastRS);
        effectButton.linearContrastRS(getApplicationContext());
        img_linearContrastRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        img_medianFilterRS =  findViewById(R.id.img_medianFilterRS);
        img_medianFilterRS.setOnClickListener(listener_medianfilter_RS);
        effectButton.medianfilter_RS(getApplicationContext(),6);
        img_medianFilterRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        img_minFilterRS =  findViewById(R.id.img_minFilterRS);
        img_minFilterRS.setOnClickListener(listener_minfilter_RS);
        effectButton.minfilter_RS(getApplicationContext(), 2);
        img_minFilterRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        img_sobelGradientRS =  findViewById(R.id.img_sobelGradientRS);
        img_sobelGradientRS.setOnClickListener(listener_sobelGradient_RS);
        effectButton.sobelGradient_RS(getApplicationContext());
        img_sobelGradientRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();


        Button button_reset = findViewById(R.id.reset);
        button_reset.setOnClickListener(listener_reset);

        Button button_save = findViewById(R.id.save);
        button_save.setOnClickListener(listener_save);


    }


    private View.OnClickListener listener_reset = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effectImage.reset();
            img.setImageBitmap(effectImage.getInitialImg());
        }
    };

    private View.OnClickListener listener_save = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startSave(bmp);
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

    private View.OnClickListener listener_rotate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            img.setRotation(img.getRotation()+90);
            img_greyRS.setRotation(img_greyRS.getRotation()+90);;
            img_keepColorRS.setRotation(img_keepColorRS.getRotation()+90);;
            img_bilateralRS.setRotation(img_bilateralRS.getRotation()+90);;
            img_randomHueRS.setRotation(img_randomHueRS.getRotation()+90);;
            img_blurRS.setRotation(img_blurRS.getRotation()+90);;
            img_colorPartitionRS.setRotation(img_colorPartitionRS.getRotation()+90);;
            img_drawOutlineRS.setRotation(img_drawOutlineRS.getRotation()+90);;
            img_histEqRS.setRotation(img_histEqRS.getRotation()+90);;
            img_linearContrastRS.setRotation(img_linearContrastRS.getRotation()+90);;
            img_medianFilterRS.setRotation(img_medianFilterRS.getRotation()+90);;
            img_minFilterRS.setRotation(img_minFilterRS.getRotation()+90);;
            img_sobelGradientRS.setRotation(img_sobelGradientRS.getRotation()+90);;
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

            switch_effects("toGreyRS", 0, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_keepColorRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("keepColorRs", 40, 0, 0, 0);
        }
    };

    /*private View.OnClickListener listener_comboEffects = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect.comboEffects(getApplicationContext(), 5);
            img.setImageBitmap(effect.getCurrentImg());
        }
    };*/

    private View.OnClickListener listener_drawOutline_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("drawOutline_RS", 0, 0, 0, 0.3f);
        }
    };

    private View.OnClickListener listener_colorPartition_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("colorPartition_RS", 9, 3, 0, 0);

        }
    };

    private View.OnClickListener listener_bilateralRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("bilateral_filter_RS", 1, 0, 0, 0);

        }
    };
    private View.OnClickListener listener_sobelGradient_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("sobelGradient_RS", 0, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_medianfilter_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("medianfilter_RS", 5, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_minfilter_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("minfilter_RS", 1, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_randomHueRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("randomHueRS", 0, 0, 0, 0);

        }
    };


    private View.OnClickListener listener_linearContrastRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("linearContrastRS", 0, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_equalizationContrastRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("equalization_contrast_RS", 0, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_blur_moy_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("blur_moy_RS", 3, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_blur_gaussian5x5_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("blur_gaussian5x5_RS", 0, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_sobel_horizontal_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("sobel_horizontal_RS", 0, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_sobel_vertical_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("sobel_vertical_RS", 0, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_laplacian_mask_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch_effects("laplacian_mask_RS", 0, 0, 0, 0);

        }
    };

    public void startSave (Bitmap bitmap) {

        int check = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (check != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024 );
        }
        FileOutputStream fileOutputStream = null;
        File file = getDisc();
        Log.i("Path : ", "Storage = " + file.toString());
        if(!file.exists() && !file.mkdirs()){
            Toast.makeText(this, "Can't create directory to save image", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "Img"+date+".jpg";
        String file_name = file.getAbsolutePath()+"/"+name;
        File new_file = new File (file_name);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024 );
        try {
            fileOutputStream = new FileOutputStream(new_file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            Toast.makeText(this, "Save image succes", Toast.LENGTH_SHORT).show();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshGallery(new_file);
    }
    public void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }
    private File getDisc() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(file, "Photo Fun");
    }

    public void switch_effects(String name_effect, int param_1, int param_2, int param_3, float param_f){
        switch(name_effect){
            case "toGreyRS" :
                effectImage.toGreyRS(getApplicationContext());
                break;
            case "keepColorRS" :
                effectImage.keepColorRS(param_1, getApplicationContext());
                break;
            case "randomHueRS" :
                effectImage.randomHueRS(getApplicationContext());
                break;
            case "linearContrastRS" :
                effectImage.linearContrastRS(getApplicationContext());
                break;
            case "equalization_contrast_RS":
                effectImage.equalization_contrast_RS(getApplicationContext());
                break;
            case "blur_moy_RS" :
                effectImage.blur_moy_RS(getApplicationContext(), param_1);
                break;
            case "blur_gaussian5x5_RS" :
                effectImage.blur_gaussian5x5_RS(getApplicationContext());
                break;
            case "sobel_horizontal_RS" :
                effectImage.sobel_horizontal_RS(getApplicationContext());
                break;
            case "sobel_vertical_RS" :
                effectImage.sobel_vertical_RS(getApplicationContext());
                break;
            case "laplacian_mask_RS" :
                effectImage.laplacian_mask_RS(getApplicationContext());
                break;
            case "bilateral_filter_RS" :
                effectImage.bilateral_filter_RS(getApplicationContext(), param_1);
                break;
            case "drawOutline_RS" :
                effectImage.drawOutline_RS(getApplicationContext(), param_f);
                break;
            case "sobelGradient_RS" :
                effectImage.sobelGradient_RS(getApplicationContext());
                break;
            case "medianfilter_RS" :
                effectImage.medianfilter_RS(getApplicationContext(), param_1);
                break;
            case "minfilter_RS" :
                effectImage.minfilter_RS(getApplicationContext(), param_1);
                break;
            case "colorPartition_RS" :
                effectImage.colorPartition_RS(getApplicationContext(), param_1, param_2, param_3);
                break;


        }
        img.setImageBitmap(effectImage.getCurrentImg());
        update_preview();


    }

    public void update_preview(){

        //effectButton.set_Init(Bitmap.createScaledBitmap(effectImage.getCurrentImg(), 100, 100, true));
        Effects  effectButton = new Effects(effectImage.getCurrentImg());

        effectButton.toGreyRS(getApplicationContext());
        img_greyRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        effectButton.keepColorRS(40, getApplicationContext());
        img_keepColorRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();


        effectButton.bilateral_filter_RS(getApplicationContext(), 1);
        img_bilateralRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        effectButton.randomHueRS(getApplicationContext());
        img_randomHueRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        effectButton.blur_moy_RS(getApplicationContext(), 3);
        img_blurRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();


        effectButton.colorPartition_RS(getApplicationContext(), 9 , 3 ,0);
        img_colorPartitionRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();


        effectButton.drawOutline_RS(getApplicationContext(), 0.3f);
        img_drawOutlineRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();


        effectButton.equalization_contrast_RS(getApplicationContext());
        img_histEqRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        effectButton.linearContrastRS(getApplicationContext());
        img_linearContrastRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();


        effectButton.medianfilter_RS(getApplicationContext(),6);
        img_medianFilterRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();


        effectButton.minfilter_RS(getApplicationContext(), 2);
        img_minFilterRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();


        effectButton.sobelGradient_RS(getApplicationContext());
        img_sobelGradientRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();
    }




}