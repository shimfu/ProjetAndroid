package com.example.acoste.projetimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
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
    private Button button_grey = null;


    private ImageView img;
    private Bitmap bmpInit;
    private Bitmap bmp;
    private int[] save;


    public void reset(Bitmap bmp){
        bmp.setPixels(save,0, bmp.getWidth(),0 ,0, bmp.getWidth(), bmp.getHeight());
        img.setImageBitmap(bmp);
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effects);

        button_gallery = findViewById(R.id.gallery_effects);
        button_gallery.setOnClickListener(listener_gallery);

        button_camera =  findViewById(R.id.camera_effects);
        button_camera.setOnClickListener(listener_camera);

        button_menu = findViewById(R.id.menu_effects_activty);
        button_menu.setOnClickListener(listener_menu);

        button_grey = findViewById(R.id.button_grey);
        button_grey.setOnClickListener(listener_grey);

        Button button_greyRs =  findViewById(R.id.button_greyRs);
        button_greyRs.setOnClickListener(listener_greyRS);

        Button button_keepColorRS =  findViewById(R.id.button_keepColorRS);
        button_keepColorRS.setOnClickListener(listener_keepColorRS);

        Button button_keepColor =  findViewById(R.id.button_keepColor);
        button_keepColor.setOnClickListener(listener_keepColor);

        Button button_randomHue =  findViewById(R.id.button_randomHue);
        button_randomHue.setOnClickListener(listener_randomHue);

        Button button_randomHueRS = findViewById(R.id.button_randomHueRS);
        button_randomHueRS.setOnClickListener(listener_randomHueRS);

        Button button_blur =  findViewById(R.id.button_blur);
        button_blur.setOnClickListener(listener_blur);

        Button button_outline = findViewById(R.id.button_outline);
        button_outline.setOnClickListener(listener_outline);

        Button button_linear_contrast_ARGB =  findViewById(R.id.button_linear_contrast_ARGB);
        button_linear_contrast_ARGB.setOnClickListener(listener_linearContrast_ARGB);

        Button button_linear_contrast_HSV =  findViewById(R.id.button_linear_contrast_HSV);
        button_linear_contrast_HSV.setOnClickListener(listener_linearContrast_HSV);

        Button button_equalization_contrast_hsv = findViewById(R.id.button_equalization_contrast_hsv);
        button_equalization_contrast_hsv.setOnClickListener(listener_equalizationContrast_HSV);

        Button button_equalization_contrast_argb =  findViewById(R.id.button_equalization_contrast_argb);
        button_equalization_contrast_argb.setOnClickListener(listener_equalizationContrast_ARGB);

        Button button_linearContrastRS =  findViewById(R.id.button_linearContrastRS);
        button_linearContrastRS.setOnClickListener(listener_linearContrastRS);

        Button button_equalization_contrast_RS = findViewById(R.id.button_equalization_contrast_RS);
        button_equalization_contrast_RS.setOnClickListener(listener_equalizationContrastRS);
	
	Button button_blur_moy_RS = findViewById(R.id.button_blur_moy_RS);
        button_blur_moy_RS.setOnClickListener(listener_blur_moy_RS);

        Button button_blur_gaussian5x5_RS = findViewById(R.id.button_blur_gaussian5x5_RS);
        button_blur_gaussian5x5_RS.setOnClickListener(listener_blur_gaussian5x5_RS);

        Button button_sobel_horizontal_RS =  findViewById(R.id.button_sobel_horizontal_RS);
        button_sobel_horizontal_RS.setOnClickListener(listener_sobel_horizontal_RS);

        Button button_sobel_vertical_RS = findViewById(R.id.button_sobel_vertical_RS);
        button_sobel_vertical_RS.setOnClickListener(listener_sobel_vertical_RS);

        Button button_laplacian_mask_RS =  findViewById(R.id.button_laplacian_mask_RS);
        button_laplacian_mask_RS.setOnClickListener(listener_laplacian_mask_RS);

        Button button_reset = findViewById(R.id.button15);
        button_reset.setOnClickListener(listener_reset);



        img = (ImageView) findViewById(R.id.img_to_modify);

        //Via camera
        Uri photoUri;
        if(getIntent().getStringExtra("imageUri") != null){
            photoUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            try {
                bmpInit = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //Via gallery
        else if(getIntent().getByteArrayExtra("imageGallery") != null){
            byte[] byteArray = getIntent().getByteArrayExtra("imageGallery");
            bmpInit = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        else
            bmpInit = BitmapFactory.decodeResource(getResources(), R.drawable.test0);

            img.setImageBitmap(bmpInit);

        save = new int[bmpInit.getWidth() * bmpInit.getHeight()];
        bmpInit.getPixels(save, 0, bmpInit.getWidth(), 0, 0, bmpInit.getWidth(), bmpInit.getHeight());

        bmp = bmpInit.copy(bmpInit.getConfig(), true);


        img.setOnClickListener(listener_zoom);
    }

    private View.OnClickListener listener_reset = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            reset(bmp);
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


    private View.OnClickListener listener_grey = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Simple simple = new Simple(bmp);
            bmp = simple.grey(bmp);
            img.setImageBitmap(bmp);

        }
    };

    private View.OnClickListener listener_keepColor = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Simple simple = new Simple(bmp);
            bmp = simple.keepColor(bmp, 50);
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_greyRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Simple simple = new Simple(bmp);
            simple.toGreyRS(bmp, getApplicationContext());
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_keepColorRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Simple simple = new Simple(bmp);
            simple.keepColorRS(bmp,50 ,getApplicationContext());
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_randomHue = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Simple simple = new Simple(bmp);
            bmp = simple.randomHue(bmp);
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_randomHueRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Simple simple = new Simple(bmp);
            simple.randomHueRS(bmp, getApplicationContext());
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_outline = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Advanced advanced = new Advanced(bmp);
            advanced.outline(bmp);
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_blur = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Advanced advanced = new Advanced(bmp);
            int test [][] = Convolution.mask_moy(5);
            advanced.blur(bmp, 5 , test );
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_linearContrast_ARGB = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Advanced advanced = new Advanced(bmp);
            int test[][] = advanced.linear_contrast_ARGB(bmp);
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_linearContrast_HSV = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Advanced advanced = new Advanced(bmp);
            int test[][] = advanced.linear_contrast_HSV(bmp, 50);
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_equalizationContrast_HSV = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Advanced advanced = new Advanced(bmp);
            int test [][] = advanced.equalization_contrast_hsv(bmp, 50);
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_equalizationContrast_ARGB = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Advanced advanced = new Advanced(bmp);
            int test [][] = advanced.equalization_contrast_argb(bmp);
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_linearContrastRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Advanced advanced = new Advanced(bmp);
            advanced.linearContrastRS(bmp,getApplicationContext());
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_equalizationContrastRS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Advanced advanced = new Advanced(bmp);
            advanced.equalization_contrast_RS(bmp, getApplicationContext());
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_blur_moy_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Advanced advanced = new Advanced(bmp);
            advanced.blur_moy_RS(bmp,getApplicationContext(), 5);
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_blur_gaussian5x5_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Advanced advanced = new Advanced(bmp);
            advanced.blur_gaussian5x5_RS(bmp,getApplicationContext());
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_sobel_horizontal_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Advanced advanced = new Advanced(bmp);
            advanced.sobel_horizontal_RS(bmp,getApplicationContext());
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_sobel_vertical_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Advanced advanced = new Advanced(bmp);
            advanced.sobel_vertical_RS(bmp,getApplicationContext());
            img.setImageBitmap(bmp);
        }
    };

    private View.OnClickListener listener_laplacian_mask_RS = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Advanced advanced = new Advanced(bmp);
            advanced.laplacian_mask_RS(bmp,getApplicationContext());
            img.setImageBitmap(bmp);
        }
    };


}
