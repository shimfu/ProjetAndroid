package com.example.acoste.projetimage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
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



public class EffectsActivity extends AppCompatActivity {

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
    private int progress1=0;

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
    private ImageView img_luminosityRS;
    private ImageView img_pixeliseRS;
    private ImageView img_negativeRS;
    private ImageView img_pencilRS;
    private ImageView img_cartoonRS;
    private ImageView img_sobelGradientColoredRS;
    private ImageView img_gaussian_blur;

    private TextView textView5, textView6;
    private SeekBar seekbar;

    String effect_name, name_parameter;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_effects);

        /***************************************
         Initialisation des boutons de navigation
         ***************************************/
        img_camera = findViewById(R.id.camera);
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
        if (getIntent().getStringExtra("imageUri") != null) {
            photoUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            try {
                bmpInit = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //Récupère le tableau de byte envoyé via Gallery.activity et le convertit en bitmap
        else if (getIntent().getByteArrayExtra("imageGallery") != null) {
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
        img_greyRS = findViewById(R.id.img_greyRS);
        img_greyRS.setOnClickListener(listener_greyRS);


        img_keepColorRS = findViewById(R.id.img_keepColorRS);
        img_keepColorRS.setOnClickListener(listener_keepColorRS);

        img_bilateralRS = findViewById(R.id.img_bilateralRS);
        img_bilateralRS.setOnClickListener(listener_bilateralRS);


        img_randomHueRS = findViewById(R.id.img_randomHueRS);
        img_randomHueRS.setOnClickListener(listener_randomHueRS);


        img_blurRS = findViewById(R.id.img_blurRS);
        img_blurRS.setOnClickListener(listener_blur_moy_RS);


        img_colorPartitionRS = findViewById(R.id.img_colorPartitionRS);
        img_colorPartitionRS.setOnClickListener(listener_colorPartition_RS);

        img_drawOutlineRS = findViewById(R.id.img_drawOutlineRS);
        img_drawOutlineRS.setOnClickListener(listener_drawOutline_RS);


        img_histEqRS = findViewById(R.id.img_histEqRS);
        img_histEqRS.setOnClickListener(listener_equalizationContrastRS);


        img_linearContrastRS = findViewById(R.id.img_linearContrastRS);
        img_linearContrastRS.setOnClickListener(listener_linearContrastRS);


        img_medianFilterRS = findViewById(R.id.img_medianFilterRS);
        img_medianFilterRS.setOnClickListener(listener_medianfilter_RS);


        img_minFilterRS = findViewById(R.id.img_minFilterRS);
        img_minFilterRS.setOnClickListener(listener_minfilter_RS);


        img_sobelGradientRS = findViewById(R.id.img_sobelGradientRS);
        img_sobelGradientRS.setOnClickListener(listener_sobelGradient_RS);

        img_luminosityRS = findViewById(R.id.img_luminosityRS);
        img_luminosityRS.setOnClickListener(listener_luminosityRS);

        img_pixeliseRS = findViewById(R.id.img_pixeliseRS);
        img_pixeliseRS.setOnClickListener(listener_pixeliseRS);

        img_negativeRS = findViewById(R.id.img_negativeRS);
        img_negativeRS.setOnClickListener(listener_negativeRS);

        img_pencilRS = findViewById(R.id.img_pencilRS);
        img_pencilRS.setOnClickListener(listener_pencilRS);

        img_cartoonRS = findViewById(R.id.img_cartoonRS);
        img_cartoonRS.setOnClickListener(listener_cartoonRS);

        img_sobelGradientColoredRS = findViewById(R.id.img_sobelGradientColoredRS);
        img_sobelGradientColoredRS.setOnClickListener(listener_sobelGradientColoredRS);

        img_gaussian_blur = findViewById(R.id.img_gaussian_blur);
        img_gaussian_blur.setOnClickListener(listener_blur_gaussian_RS);

        Button button_reset = findViewById(R.id.reset);
        button_reset.setOnClickListener(listener_reset);

        Button button_save = findViewById(R.id.save);
        button_save.setOnClickListener(listener_save);

        seekbar = findViewById(R.id.seekBar);
        textView5 = findViewById(R.id.textView5);


        hide_param(true);
        update_preview();

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress1 = progressValue;
                textView5.setText(name_parameter + " : " + progressValue + "/" + seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if ((effect_name.equals("drawOutline_RS") ) || (effect_name.equals("luminosity_RS") )){

                    float param_drawOutlineRS = (float) progress1;
                    param_drawOutlineRS = param_drawOutlineRS/100.0f;
                    Log.e("float", Float.toString(param_drawOutlineRS) );
                    switch_effects(effect_name, 0, 0, 0, param_drawOutlineRS);
                }
                else
                    switch_effects(effect_name,progress1,0,0,0);
            }
        });

    }


    private View.OnClickListener listener_reset = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effectImage.reset();
            img.setImageBitmap(effectImage.getInitialImg());
            update_preview();
            hide_param(true);
        }
    };

    private View.OnClickListener listener_save = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            startSave(effectImage.getCurrentImg());
        }
    };

    private View.OnClickListener listener_zoom = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(EffectsActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_custom_layout, null);
            PhotoView photoView = mView.findViewById(R.id.imageView);
            photoView.setImageResource(R.drawable.test0);
            photoView.setImageBitmap(effectImage.getCurrentImg());
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
            img.setRotation(img.getRotation() + 90);
            img_greyRS.setRotation(img_greyRS.getRotation() + 90);
            ;
            img_keepColorRS.setRotation(img_keepColorRS.getRotation() + 90);
            ;
            img_bilateralRS.setRotation(img_bilateralRS.getRotation() + 90);
            ;
            img_randomHueRS.setRotation(img_randomHueRS.getRotation() + 90);
            ;
            img_blurRS.setRotation(img_blurRS.getRotation() + 90);
            ;
            img_colorPartitionRS.setRotation(img_colorPartitionRS.getRotation() + 90);
            ;
            img_drawOutlineRS.setRotation(img_drawOutlineRS.getRotation() + 90);
            ;
            img_histEqRS.setRotation(img_histEqRS.getRotation() + 90);
            ;
            img_linearContrastRS.setRotation(img_linearContrastRS.getRotation() + 90);
            ;
            img_medianFilterRS.setRotation(img_medianFilterRS.getRotation() + 90);
            ;
            img_minFilterRS.setRotation(img_minFilterRS.getRotation() + 90);
            ;
            img_sobelGradientRS.setRotation(img_sobelGradientRS.getRotation() + 90);

            img_gaussian_blur.setRotation(img_gaussian_blur.getRotation() + 90);

            img_pixeliseRS.setRotation(img_pixeliseRS.getRotation() + 90);

            img_luminosityRS.setRotation(img_luminosityRS.getRotation() + 90);

            img_negativeRS.setRotation(img_negativeRS.getRotation() + 90);

            img_pencilRS.setRotation(img_pencilRS.getRotation() + 90);


            img_cartoonRS.setRotation(img_cartoonRS.getRotation() + 90);


            img_sobelGradientColoredRS.setRotation(img_sobelGradientColoredRS.getRotation() + 90);





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
            hide_param(true);
            effect_name = "toGreyRS";
            switch_effects(effect_name, 0, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_keepColorRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "keepColorRS";
            name_parameter = "Color";
            show_param(true,0 , 360);
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
            effect_name = "drawOutline_RS";
            name_parameter = "Intensity";
            show_param(true, 0, 100);
        }
    };

    private View.OnClickListener listener_colorPartition_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "colorPartition_RS";
            name_parameter = "Color";
            show_param(true, 0, 360 ) ;
        }
    };

    private View.OnClickListener listener_bilateralRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "bilateral_filter_RS";
            name_parameter = "Intensity";
            show_param(true, 1, 3 );
        }
    };

    private View.OnClickListener listener_sobelGradient_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hide_param(true);

            effect_name = "sobelGradient_RS";

            switch_effects("sobelGradient_RS", 0, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_medianfilter_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "medianfilter_RS";
            name_parameter = "Intensity";
            show_param(true, 0 , 30 ) ;
        }
    };

    private View.OnClickListener listener_minfilter_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "minfilter_RS";
            name_parameter = "Intensity";
            show_param(true, 0 , 30 );
        }
    };

    private View.OnClickListener listener_randomHueRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hide_param(true);

            effect_name = "randomHueRS";

            switch_effects("randomHueRS", 0, 0, 0, 0);

        }
    };


    private View.OnClickListener listener_linearContrastRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hide_param(true);

            effect_name = "linearContrastRS";
            name_parameter = "Brightness";
            show_param(true, 100, 1000);
        }
    };

    private View.OnClickListener listener_equalizationContrastRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hide_param(true);

            effect_name = "equalization_contrast_RS";

            switch_effects("equalization_contrast_RS", 0, 0, 0, 0);
        }
    };

    private View.OnClickListener listener_blur_moy_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "blur_moy_RS";
            name_parameter = "Intensity";
            show_param(true,0, 30);
        }
    };

    private View.OnClickListener listener_luminosityRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "luminosity_RS";
            name_parameter = "Intensity";
            show_param(true, 0, 100);
        }
    };

    private View.OnClickListener listener_pixeliseRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "pixelise_RS";
            name_parameter = "SizePixel";
            show_param(true, 1, 20);
        }
    };

    private View.OnClickListener listener_negativeRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hide_param(true);

            effect_name = "negative_RS";

            switch_effects("negative_RS", 0, 0, 0, 0);
        }
    };

    private View.OnClickListener listener_pencilRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "pencil_RS";
            name_parameter = "Intensity";
            show_param(true, 1, 30);
        }
    };

    private View.OnClickListener listener_cartoonRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "cartoon_RS";
            name_parameter = "filter size";
            show_param(true, 1, 30);
        }
    };

    private View.OnClickListener listener_sobelGradientColoredRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hide_param(true);

            effect_name = "sobelGradientColored_RS";

            switch_effects("sobelGradientColored_RS", 0, 0, 0, 0);
        }
    };

    private View.OnClickListener listener_blur_gaussian_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "blur_gaussian_RS";
            name_parameter = "Intensity";
            show_param(true, 1, 30);

        }
    };

    private View.OnClickListener listener_sobel_horizontal_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "sobel_horizontal_RS";

            switch_effects("sobel_horizontal_RS", 0, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_sobel_vertical_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "sobel_vertical_RS";

            switch_effects("sobel_vertical_RS", 0, 0, 0, 0);

        }
    };

    private View.OnClickListener listener_laplacian_mask_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            effect_name = "laplacian_mask_RS";

            switch_effects("laplacian_mask_RS", 0, 0, 0, 0);

        }
    };

    public void startSave(Bitmap bitmap) {

        int check = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (check != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024);
        }
        FileOutputStream fileOutputStream = null;
        File file = getDisc();
        Log.i("Path : ", "Storage = " + file.toString());
        if (!file.exists() && !file.mkdirs()) {
            Toast.makeText(this, "Can't create directory to save image", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "Img" + date + ".jpg";
        String file_name = file.getAbsolutePath() + "/" + name;
        File new_file = new File(file_name);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1024);
        try {
            fileOutputStream = new FileOutputStream(new_file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            Toast.makeText(this, "Save image succes", Toast.LENGTH_SHORT).show();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshGallery(new_file);
    }

    public void refreshGallery(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

    private File getDisc() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(file, "Photo Fun");
    }

    public void switch_effects(String name_effect, int param_1, int param_2, int param_3, float param_f) {
        switch (name_effect) {
            case "toGreyRS":
                effectImage.toGreyRS(getApplicationContext());
                break;
            case "keepColorRS":
                effectImage.keepColorRS(param_1, getApplicationContext());
                break;
            case "randomHueRS":
                effectImage.randomHueRS(getApplicationContext());
                break;
            case "linearContrastRS":
                effectImage.linearContrastRS(getApplicationContext(), param_2, param_1);
                break;
            case "equalization_contrast_RS":
                effectImage.equalization_contrast_RS(getApplicationContext(), param_f);
                break;
            case "blur_moy_RS":
                effectImage.blur_moy_RS(getApplicationContext(), param_1);
                break;
            case "blur_gaussian_RS":
                effectImage.blur_gaussian_RS(getApplicationContext(), param_1);
                break;
            case "sobel_horizontal_RS":
                effectImage.sobel_horizontal_RS(getApplicationContext());
                break;
            case "sobel_vertical_RS":
                effectImage.sobel_vertical_RS(getApplicationContext());
                break;
            case "laplacian_mask_RS":
                effectImage.laplacian_mask_RS(getApplicationContext());
                break;
            case "bilateral_filter_RS":
                effectImage.bilateral_filter_RS(getApplicationContext(), param_1);
                break;
            case "drawOutline_RS":
                effectImage.drawOutline_RS(getApplicationContext(), param_f, 1);
                break;
            case "sobelGradient_RS":
                effectImage.sobelGradient_RS(getApplicationContext());
                break;
            case "medianfilter_RS":
                effectImage.medianfilter_RS(getApplicationContext(), param_1);
                break;
            case "minfilter_RS":
                effectImage.minfilter_RS(getApplicationContext(), param_1);
                break;
            case "colorPartition_RS":
                effectImage.colorPartition_RS(getApplicationContext(), param_1, param_2, param_3);
                break;
            case "luminosity_RS":
                effectImage.luminosity_RS(getApplicationContext(), param_f);
                break;
            case "pixelise_RS":
                effectImage.pixelise_RS(getApplicationContext(), param_1);
                break;
            case "negative_RS":
                effectImage.negative_RS(getApplicationContext());
                break;
            case "pencil_RS":
                effectImage.pencil_RS(getApplicationContext(), param_1);
                break;
            case "cartoon_RS":
                effectImage.cartoon_RS(getApplicationContext(), param_1, 1, param_1, 0.8f, 1, 24, 4, 0 );
                break;
            case "sobelGradientColored_RS":
                effectImage.sobelGradientColored_RS(getApplicationContext());
                break;

        }
        img.setImageBitmap(effectImage.getCurrentImg());
        update_preview();


    }

    public void update_preview() {

        effectButton.setInitialImg(Bitmap.createScaledBitmap(effectImage.getCurrentImg(), 100, 100, true));
        Effects effectButton = new Effects(effectImage.getCurrentImg());

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


        effectButton.colorPartition_RS(getApplicationContext(), 9, 3, 0);
        img_colorPartitionRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();


        effectButton.drawOutline_RS(getApplicationContext(), 0.3f, 0);
        img_drawOutlineRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();


        effectButton.equalization_contrast_RS(getApplicationContext(), 0.3f);
        img_histEqRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        effectButton.linearContrastRS(getApplicationContext(), 0, 500);
        img_linearContrastRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();


        effectButton.medianfilter_RS(getApplicationContext(), 6);
        img_medianFilterRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();


        effectButton.minfilter_RS(getApplicationContext(), 2);
        img_minFilterRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();


        effectButton.sobelGradient_RS(getApplicationContext());
        img_sobelGradientRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        effectButton.luminosity_RS(getApplicationContext(),0.3f);
        img_luminosityRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        effectButton.pixelise_RS(getApplicationContext(), 5);
        img_pixeliseRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        effectButton.negative_RS(getApplicationContext());
        img_negativeRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        effectButton.pencil_RS(getApplicationContext(), 25);
        img_pencilRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        effectButton.cartoon_RS(getApplicationContext(), 2, 1, 2, 0.8f, 1, 24, 4, 0);
        img_cartoonRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        effectButton.sobelGradientColored_RS(getApplicationContext());
        img_sobelGradientColoredRS.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();

        effectButton.blur_gaussian_RS(getApplicationContext(), 2);
        img_gaussian_blur.setImageBitmap(effectButton.getCurrentImg());
        effectButton.reset();
    }

    public void hide_param(boolean visible) {
        if (visible) {
            seekbar.setVisibility(View.INVISIBLE);
            textView5.setVisibility(View.INVISIBLE);
        } else if (!visible) {
            seekbar.setVisibility(View.VISIBLE);
            textView5.setVisibility(View.VISIBLE);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void show_param(boolean b1, int min, int max) {
        if(b1){
            hide_param(false);
            seekbar.setMin(min);
            seekbar.setMax(max);
        }


    }
}