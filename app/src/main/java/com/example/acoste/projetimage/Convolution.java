package com.example.acoste.projetimage;

import android.graphics.Bitmap;

/**
 * Created by acoste on 01/02/19.
 */

public abstract class Convolution extends Effects {

    abstract Bitmap blur();

    abstract Bitmap outline();
}
