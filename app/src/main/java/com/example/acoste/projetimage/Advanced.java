package com.example.acoste.projetimage;

import android.graphics.Bitmap;
import com.example.acoste.projetimage.Convolution;
import com.example.acoste.projetimage.Histogram;


/**
 * Created by acoste on 08/02/19.
 */

public abstract class Advanced extends Effects {

    abstract Bitmap blur();

    abstract Bitmap outline();

    abstract Bitmap linear_contrast_rgb();

    abstract Bitmap linear_contrast_hsv();

    abstract Bitmap equalization_contrast_rgb();

    abstract Bitmap equalization_contrast_hsv();

    abstract Bitmap equalization_contrast_yuv_RS();

}
