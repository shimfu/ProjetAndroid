package com.example.acoste.projetimage;

import android.graphics.Bitmap;

/**
 * Created by acoste on 01/02/19.
 */

public abstract  class Simple extends Effects {

    abstract Bitmap toGrey();

    abstract Bitmap keepColor();

    abstract Bitmap randomHue();

    abstract Bitmap toGrey_RS();

    abstract Bitmap keepColor_RS();

    abstract Bitmap randomHue_RS();

}
