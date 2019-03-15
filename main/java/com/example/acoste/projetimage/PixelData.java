package com.example.acoste.projetimage;

/**
 * Created by acoste on 19/12/18.
 */

public class PixelData {



    public int max_lum;
    public int min_lum;

    public int width;

    public int height;


    public int[] colordata;




    public PixelData(int width , int height){
        max_lum = 0;
        min_lum = 0;

        this.width = width;
        this.height = height;

        colordata = new int[width * height];
    }

    public int getHeight() {

        return height;
    }

    public int getWidth() {

        return width;
    }

    public void setWidth(int width) {

        this.width = width;
    }

    public void setHeight(int height) {

        this.height = height;
    }

    public int getMax_lum() {
        return max_lum;
    }

    public void setMax_lum(int max_lum) {
        this.max_lum = max_lum;
    }

    public int getMin_lum() {
        return min_lum;
    }

    public void setMin_lum(int min_lum) {
        this.min_lum = min_lum;
    }

    public int[] getColordata() {
        return colordata;
    }

    public void setColordata(int[] colordata) {
        this.colordata = colordata;
    }


}
