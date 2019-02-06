package com.example.acoste.projetimage;

import android.graphics.Bitmap;

/**
 * Created by acoste on 01/02/19.
 */

public abstract class Histogram extends Effects {

    abstract Bitmap contrastEqualization();

    abstract Bitmap contrastLinear();

}
