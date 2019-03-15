package com.example.acoste.projetimage;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by acoste on 01/02/19.
 */

public class Histogram{

    static int[] getGreyHistogram(int[] colorData){//return the grey histogram (ARGB color space)
        int[] greyHistog = new int[256];
        for (int i = 0 ; i < colorData.length ; i++){
            greyHistog[(int)(Color.red(colorData[i]) * 0.30 + Color.green(colorData[i]) * 0.59 + Color.blue(colorData[i]) * 0.11)]++;
        }
        return greyHistog;
    }

    static int[] getCumuledHistogram(int[] histog){//return the cumuled histogram of an histogram
        int[] histogreturned = new int[histog.length];
        histogreturned[0] = histog[0]; // we init first term for a recurrence
        for(int i = 1 ; i < histog.length ; i++){
            histogreturned[i] = histogreturned[i - 1] + histog[i];
        }
        return  histogreturned;
    }

    static int[] getValueHistogram(int[] colorData, int precision){//return the histogram of luminosity with a fixed precision (HSV color space)

        int[] histo = new int[precision];
        float[] HSV = new float[3];

        for (int i = 0 ; i < colorData.length ; i++){
            Color.colorToHSV(colorData[i], HSV);
            histo[(int)(HSV[2] * (precision - 1))]++;
        }
        return histo;

    }

    static int[] minmaxHisto(int[] histo){//return respectivly the minimum and the maximum in a int[2] array
        int[] minmax = new int[2];
        minmax[0] = 0;
        minmax[1] = 0;

        int i=0;
        while (histo[i] == 0 && i < histo.length){
            i++;
        }
        int j = histo.length-1;
        while (histo[j] == 0 && j > 0){
            j--;
        }
        minmax[0] = i; // minimum
        minmax[1] = j; // maximum
        return minmax;
    }

    static int[] create_linear_contrast_lut_grey(int[] minmax){//return a LUT for linear contrast (ARGB color space)
        int[] lut_grey = new int[256];
        int min = minmax[0];
        int max = minmax[1];
        for (int i = 0 ; i < 256 ; i++){
            lut_grey[i] = (255 * (i - min)) / (max - min);
        }
        return lut_grey;
    }

    static int[] create_linear_contrast_lut(int[] minmax, int range){//return a LUT for linear contrast with range elements (ARGB & HSV color space)
        int[] lut = new int[range];
        int min = minmax[0];
        int max = minmax[1];
        for (int i = 0 ; i < range ;i++){
            lut[i] = ((range - 1) * (i - min)) / (max - min);
        }
        return lut;
    }


    static int[] create_plane_histog_lut(int[] histo){//return a LUT for plane contrast with range elements (ARGB & HSV color space)
        int[] lut = new int[histo.length];
        int[] cumuledhistog;
        cumuledhistog = getCumuledHistogram(histo);

        for (int i = 0 ; i < histo.length ; i++){
            lut[i] = (cumuledhistog[i] * (histo.length - 1)) / cumuledhistog[histo.length - 1];//"theoretically" impossible to divide by zero in used functions, we could add a simple test
        }
        return  lut;
    }

}