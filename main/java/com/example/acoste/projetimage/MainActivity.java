package com.example.acoste.projetimage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
//import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import java.lang.*;
import java.util.Random;

//---------------IMPORT RENDERSCRIPT--------------------- toGrey est désactivé
//import com.android.rssample.ScriptC_grey;

import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import com.android.rssample.ScriptC_toGrey;
import com.example.q.renderscriptexample.ScriptC_histEq;
import com.android.rssample.ScriptC_keepColor;
import com.android.rssample.ScriptC_randomHue;

public class MainActivity extends AppCompatActivity {

    private Button mApplyButton;
    private Button mReset;
    private Button mChangeImg;
    private ImageView image;
    private BitmapFactory.Options opts = new BitmapFactory.Options();
    private Bitmap bMap;

    public int[] getGreyHistogram(int[] colorData){//return the grey histogram (ARGB color space)
        int[] greyHistog = new int[256];
        for (int i = 0 ; i < colorData.length ; i++){
            greyHistog[(int)(Color.red(colorData[i])*0.30 + Color.green(colorData[i])*0.59 + Color.blue(colorData[i])*0.11)]++;
        }
        return greyHistog;
    }

    public int[] getCumuledHistogram(int[] histog){//return the cumuled histogram of an histogram
        int[] histogreturned = new int[histog.length];
        histogreturned[0] = histog[0]; // we init first term for a recurrence
        for(int i = 1; i < histog.length ; i++){
            histogreturned[i] = histogreturned[i-1] + histog[i];
        }
        return  histogreturned;
    }

    public int[] getValueHistogram(int[] colorData, int precision){//return the histogram of luminosity with a fixed precision (HSV color space)

        int[] histo = new int[precision];
        float[] HSV = new float[3];

        for (int i = 0; i < colorData.length;i++){
            Color.colorToHSV(colorData[i], HSV);
            histo[(int)(HSV[2]*(precision-1))]++;
        }
        return histo;

    }

    public int[] minmaxHisto(int[] histo){//return respectivly the minimum and the maximum in a int[2] array
        int[] minmax = new int[2];
        minmax[0]=0;
        minmax[1]=0;

        int i=0;
        while (histo[i]==0 && i < histo.length){
            i++;
        }
        int j=histo.length-1;
        while (histo[j]==0 && j > 0){
            j--;
        }
        minmax[0]=i; // minimum
        minmax[1]=j; // maximum
        return minmax;
    }

    public int[] create_linear_contrast_lut_grey(int[] minmax){//return a LUT for linear contrast (ARGB color space)
        int[] lut_grey = new int[256];
        int min = minmax[0];
        int max = minmax[1];
        for (int i = 0; i<256;i++){
            lut_grey[i] = (255*(i-min))/(max-min);
        }
        return lut_grey;
    }

    public int[] create_linear_contrast_lut(int[] minmax, int range){//return a LUT for linear contrast with range elements (ARGB & HSV color space)
        int[] lut = new int[range];
        int min = minmax[0];
        int max = minmax[1];
        for (int i = 0; i<range;i++){
            lut[i] = ((range-1)*(i-min))/(max-min);
        }
        return lut;
    }
    

    public int[] create_plane_histog_lut(int[] histo){//return a LUT for plane contrast with range elements (ARGB & HSV color space)
        int[] lut = new int[histo.length];
        int[] cumuledhistog;
        cumuledhistog = getCumuledHistogram(histo);

        for (int i = 0; i < histo.length; i++){
            lut[i] = (cumuledhistog[i]*(histo.length-1))/cumuledhistog[histo.length-1];//"theoretically" impossible to divide by zero in used functions, we could add a simple test
        }
        return  lut;
    }


    public int[][] plane_histog_contrast_HSV(Bitmap bMap, int precision){//apply plane contrast (HSV color space) on a Bitmap and return original histogram and associated LUT
        int [][] histog = new int[2][];//allowing memory for returned value wich are histogram
        int[] colorData = new int[bMap.getHeight()*bMap.getWidth()];
        bMap.getPixels(colorData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        histog[0] = getValueHistogram(colorData, precision);//getting Value histogram (HSV color space)
        histog[1] = create_plane_histog_lut(histog[0]);//Creating LUT wich will be returned

        float[] HSV = new float[3];
        for (int i = 0; i < colorData.length; i++) {
            Color.colorToHSV(colorData[i],HSV);
            HSV[2] =  ((float)(histog[1][(int)(HSV[2]*(histog[1].length-1))])/((float)(histog[1].length-1)));//lot of casts wich are necessary for float division and modifying the Value
            colorData[i] = Color.HSVToColor(Color.alpha(colorData[1]),HSV);
        }
        bMap.setPixels(colorData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        return histog;
    }

    public int[][] plane_histog_contrast_ARGB(Bitmap bMap){//apply plane contrast (ARGB color space) on a Bitmap and return original histogram and associated LUT
        int [][] histog = new int[2][];//allowing memory for returned value wich are histogram
        int[] colorData = new int[bMap.getHeight()*bMap.getWidth()];
        bMap.getPixels(colorData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        histog[0] = getGreyHistogram(colorData);//getting Value histogram (ARGB color space)
        histog[1] = create_plane_histog_lut(histog[0]);//Creating LUT wich will be returned

        for (int i = 0; i < colorData.length; i++) {
            int red = Color.red(colorData[i]);// getting RGB component
            int green = Color.green(colorData[i]);
            int blue = Color.blue(colorData[i]);

            red = histog[1][red];
            green = histog[1][green];
            blue = histog[1][blue];

            colorData[i]= Color.argb(Color.alpha(colorData[i]),red,green,blue);//on met a jour les data qu'on vas utilisé dans set pixel
        }
        bMap.setPixels(colorData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        return histog;
    }

    public int[][] linear_contrast_HSV(Bitmap bMap, int precision){//apply linear contrast (HSV color space) on a Bitmap and return original histogram and associated LUT
        int [][] histog = new int[2][];//allowing memory for returned value wich are histogram
        int[] colorData = new int[bMap.getHeight()*bMap.getWidth()];
        bMap.getPixels(colorData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        histog[0] = getValueHistogram(colorData,precision);//getting Value histogram (HSV color space)
        int[] minmax = minmaxHisto(histog[0]);//getting minimum and maximum of the histogram
        histog[1] = create_linear_contrast_lut(minmax, histog[0].length);//creating LUT from original histogram

        float[] HSV = new float[3];
        for (int i = 0; i < colorData.length; i++) {
            Color.colorToHSV(colorData[i], HSV);
            HSV[2] = ((float)(histog[1][(int)(HSV[2]*(histog[1].length-1))])/((float)(histog[1].length-1)));//lot of casts wich are necessary for float division and modifying the Value
            colorData[i]= Color.HSVToColor(Color.alpha(colorData[i]),HSV);
        }
        bMap.setPixels(colorData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        return histog;
    }


    public int[][] linear_contrast_ARGB(Bitmap bMap){//apply linear contrast (ARGB color space) on a Bitmap and return original histogram and associated LUT
        int [][] histog = new int[2][];//allowing memory for returned value wich are histogram
        int[] colorData = new int[bMap.getHeight()*bMap.getWidth()];
        bMap.getPixels(colorData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        histog[0] = getGreyHistogram(colorData);

        int[] minmax = minmaxHisto(histog[0]);

        histog[1] = create_linear_contrast_lut(minmax, histog[0].length);//getting LUT (shades of grey - ARGB color space)
        for (int i = 0; i < colorData.length; i++) {
            int red = Color.red(colorData[i]);
            int green = Color.green(colorData[i]);
            int blue = Color.blue(colorData[i]);

            if ( red < minmax[0]){//setting new colors from LUT
                red = 0;
            }else if(red > minmax[1]){
                red = 255;
            }else{
                red = histog[1][red];
            }
            if ( green < minmax[0]){
                green = 0;
            }else if(green > minmax[1]){
                green = 255;
            }else{
                green = histog[1][green];
            }
            if ( blue < minmax[0]){
                blue = 0;
            }else if(blue > minmax[1]){
                blue = 255;
            }else{
                blue = histog[1][blue];
            }
            colorData[i]= Color.argb(Color.alpha(colorData[i]),red,green,blue);
        }
        bMap.setPixels(colorData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        return histog;
    }

    void grey(Bitmap bMap){//transform a Bitmap in shades of grey (ARGB color space)
        int[] pixelData = new int[bMap.getWidth() * bMap.getHeight()];

        bMap.getPixels(pixelData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        for (int i = 0; i < pixelData.length; i++) {
            int grey;
            grey = (int) (0.30 * Color.red(pixelData[i]) + 0.59 * Color.green(pixelData[i]) + 0.11 * Color.blue(pixelData[i]));
            pixelData[i] = Color.argb(Color.alpha(pixelData[i]), grey, grey, grey);
        }
        bMap.setPixels(pixelData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
    }

    public int[] keepColor(int[] color, int hue){//color in grey all the color which are 40° or more away from the hue parameter
        if(hue < 0 && hue > 360){//wrong parameter hue
            return color;
        }
        int hue2 = hue + 360;//hue2 will be used for reds, 5° very different of 355° but in fact there is only 10° between them

        float[] HSV = new float[3];
        int grey;
        for (int i = 0 ; i < color.length ; i++){
            Color.colorToHSV(color[i], HSV) ;

            if (Math.abs(HSV[0]-hue) > 40 && Math.abs(HSV[0]-hue2) > 40 ){//this is why i add 360 to each hue which are to small
                grey = (int) (0.30 * Color.red(color[i]) + 0.59 * Color.green(color[i]) + 0.11 * Color.blue(color[i]));
                color[i] = Color.argb(Color.alpha(color[i]),grey,grey,grey);
            }
        }
        return color;
    }

    public void keepColor(Bitmap bMap, int hue){
        int[] pixelData = new int[bMap.getWidth()*bMap.getHeight()];
        bMap.getPixels(pixelData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        pixelData = keepColor(pixelData, hue);
        bMap.setPixels(pixelData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
    }

    public int[] randomHue(int[] color){//change the Hue of all pixel of an array of Color type (HSV color space)

        Random rn = new Random();
        int hue = rn.nextInt(361);

        float[] HSV = new float[3];
        for (int i = 0; i < color.length;i++){
            Color.colorToHSV(color[i], HSV);
            HSV[0] = hue;
            color[i] = Color.HSVToColor(HSV);
        }

        return color;
    }

    public void randomHue(Bitmap bMap){//change the Hue of all pixel of a Bitmap (HSV color space)

        int[] pixelData = new int[bMap.getWidth()*bMap.getHeight()];
        bMap.getPixels(pixelData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
        pixelData = randomHue(pixelData);
        bMap.setPixels(pixelData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());

    }

/*
    public int[] mirrorBitmap2(Bitmap bMap, int k){ //return Color data of a bigger Bitmap where each sides are mirrored k time
        if (k < 1){
            Log.e("ERREUR", "taille matrice non valide");
        }
        int height = bMap.getHeight();
        int width = bMap.getWidth();

        int[] pixelDataBlured = new int[height*width];
        int[] pixelDataBegin = new int[height*width];
        bMap.getPixels(pixelDataBegin,0,width,0,0,width,height);
        int[] pixelData = new int[(height+2*k)*(width+2*k)];

        for (int i = 0 ; i < k ; i++){//on cree une image plus grande avec des valeurs au extremité qui sont en mode "mirroir"
            for (int j = 0 ; j < k ; j++){
                pixelData[i*(width+2*k)+j] = pixelDataBegin[0];//angle en haut a gauche
                pixelData[(height+k+i)*(width+2*k)+j]= pixelDataBegin[(height-1)*width];//angle en bas a gauche
                pixelData[width+k + i*(width+2*k)+j] = pixelDataBegin[width-1];//angle en haut a droite
                pixelData[(height+k+i)*(width+2*k)+width+k + j] = pixelDataBegin[width*height - 1];//angle en bas a droite
            }
        }

        for (int i = 0 ; i < width ; i++){// on remplit les ligne horizontales
            for (int j = 0 ; j < k ; j++){
                pixelData[k+i+j*(width+2*k)] = pixelDataBegin[i];
                pixelData[k+i+j*(width+2*k) + (height+k)*(width+2*k)] = pixelDataBegin[(height-1)*width + i];//On peu factoriser mais moins compréhensible (k+i+(j+height+k)*(width+2*k))
            }
        }

        for (int i = 0 ; i < height ; i++){// on rempit les ligne verticales
            for(int j = 0 ; j < k ; j++){
                pixelData[(k+i)*(width+2*k) + j] = pixelDataBegin[i*width];
                pixelData[(k+i)*(width+2*k)+k+width+j] = pixelDataBegin[width-1 + i*width];
            }
        }

        for (int i = 0 ; i < height ; i++){
            for (int j = 0 ; j < width ; j++){
                pixelData[(k+i)*(width+2*k)+j+k] = pixelDataBegin[i*width+j];
            }
        }

        opts.inMutable = true;
        int[] colors = new int[(width+2*k)*(height+2*k)];

        return pixelData;
    }
*/
    public int[] mirrorBitmap(Bitmap bMap, int k){ //return Color data of a bigger Bitmap where each sides are mirrored k time
        return mirrorBitmap(bMap,k ,k);
    }

    public int[] mirrorBitmap(Bitmap bMap, int k_line , int k_collumn){//return Color data of a bigger Bitmap where border line are mirrored k_line time and border collumn k_collumn
        if (k_line < 0 && k_collumn < 0){
            Log.e("ERREUR", "taille matrice non valide");
        }
        int height = bMap.getHeight();
        int width = bMap.getWidth();

        int[] pixelDataBegin = new int[height*width];
        bMap.getPixels(pixelDataBegin,0,width,0,0,width,height);
        int[] pixelData = new int[(height+2*k_line)*(width+2*k_collumn)];//Color data of the mirrored Bitmap, not init yet

        for (int i = 0 ; i < k_line ; i++){//filling the 4 border angles
            for (int j = 0 ; j < k_collumn ; j++){
                pixelData[i*(width+2*k_collumn)+j] = pixelDataBegin[0];//left up angle
                pixelData[(height+k_line+i)*(width+2*k_collumn)+j]= pixelDataBegin[(height-1)*width];//bottom left angle
                pixelData[width+k_collumn + i*(width+2*k_collumn)+j] = pixelDataBegin[width-1];//top right angle
                pixelData[(height+k_line+i)*(width+2*k_collumn)+width+k_collumn + j] = pixelDataBegin[width*height - 1];//bottom right angle
            }
        }

        for (int i = 0 ; i < width ; i++){// filling border line
            for (int j = 0 ; j < k_line ; j++){
                pixelData[k_collumn+i+j*(width+2*k_collumn)] = pixelDataBegin[i];
                pixelData[k_collumn+i+j*(width+2*k_collumn) + (height+k_line)*(width+2*k_collumn)] = pixelDataBegin[(height-1)*width + i];//On peu factoriser mais moins compréhensible (k+i+(j+height+k)*(width+2*k))
            }
        }

        for (int i = 0 ; i < height ; i++){// filling border collumn
            for(int j = 0 ; j < k_collumn ; j++){
                pixelData[(k_line+i)*(width+2*k_collumn) + j] = pixelDataBegin[i*width];
                pixelData[(k_line+i)*(width+2*k_collumn)+k_collumn+width+j] = pixelDataBegin[width-1 + i*width];
            }
        }

        for (int i = 0 ; i < height ; i++){//filling the center part
            for (int j = 0 ; j < width ; j++){
                pixelData[(k_line+i)*(width+2*k_collumn)+j+k_collumn] = pixelDataBegin[i*width+j];
            }
        }

        return pixelData;
    }

    int[][] convolutionOnMirror(int[] colorData , int k , int width, int height, int[][] masque){//convolution which wan't calculate border value ( k pixel from the border ) -(ARGB color space)

        int[][] returnedData = new int[(width-2*k)*(height-2*k)][4];
        int red_sum;
        int green_sum;
        int blue_sum;

        for (int i = k ; i < height - k ; i++){//for each line of the image
            for (int j = k ; j < width - k ; j++){//for each collumn of the image
                red_sum = 0;
                green_sum = 0;
                blue_sum = 0;
                for(int l = -k ; l < k+1 ; l++ ){   //for each line of the mask
                    for (int n = -k ; n < k+1 ; n++){   //for each collumn of the mask
                        red_sum = red_sum + Color.red(colorData[((i+l)*width)+j+n])*masque[l+k][n+k];
                        green_sum = green_sum + Color.green(colorData[((i+l)*width)+j+n])*masque[l+k][n+k];
                        blue_sum = blue_sum + Color.blue(colorData[((i+l)*width)+j+n])*masque[l+k][n+k];
                    }
                }
                returnedData[(i-k)*(width-2*k)+(j-k)][0] = Color.alpha(colorData[(i-k)*(width-2*k)+(j-k)]);
                returnedData[(i-k)*(width-2*k)+(j-k)][1] = red_sum;
                returnedData[(i-k)*(width-2*k)+(j-k)][2] = green_sum;
                returnedData[(i-k)*(width-2*k)+(j-k)][3] = blue_sum;
            }
        }

        return returnedData;
    }

    int[][] convolution(int[] colorData , int width, int height, int[][] masque){//convolution which set the value of the mask at 0 if out of range of image -(ARGB color space)

        int[][] returnedData = new int[(width)*(height)][4];
        int red_sum;
        int green_sum;
        int blue_sum;

        for (int i = 0 ; i < height  ; i++){//for each line of the image
            for (int j = 0 ; j < width ; j++){//for each collumn of the image
                red_sum = 0;
                green_sum = 0;
                blue_sum = 0;
                for(int l = - masque.length/2 ; l < masque.length/2 +1 ; l++ ){   ///for each line of the mask

                    for (int n = - masque[l + masque.length/2].length / 2 ; n < masque[l+masque.length/2].length/2 + 1 ; n++){   //for each collumn of the mask
                        if( (j+n) >= 0 && (j+n) < width && (i+l) >=0 && (i+l) < height){
                            red_sum = red_sum + Color.red(colorData[((i+l)*width)+j+n])*masque[l+masque.length/2][n+masque[l+masque.length/2].length/2];
                            green_sum = green_sum + Color.green(colorData[((i+l)*width)+j+n])*masque[l+masque.length/2][n+masque[l+masque.length/2].length/2];
                            blue_sum = blue_sum + Color.blue(colorData[((i+l)*width)+j+n])*masque[l+masque.length/2][n+masque[l+masque.length/2].length/2];
                        }
                    }
                }
                returnedData[i*width+j][0] = Color.alpha(colorData[i*width+j]);
                returnedData[i*width+j][1] = red_sum;
                returnedData[i*width+j][2] = green_sum;
                returnedData[i*width+j][3] = blue_sum;

            }
        }

        return returnedData;
    }

    int[] blur_data(Bitmap bMap , int k, int[][] mask){// return the colro data of a blured Bitmap

        int norme = norm_for_blurmask(mask);

        int[] pixelData  = mirrorBitmap(bMap,k);
        int [][] pixelDataRGB = convolutionOnMirror( pixelData, k , bMap.getWidth()+2*k  , bMap.getHeight()+2*k, mask);
        int[] pixelDataFinal = new int[bMap.getWidth()*bMap.getHeight()];

        for (int i = 0 ; i < pixelDataFinal.length ; i++){
            pixelDataRGB[i][1] = pixelDataRGB[i][1]/norme;
            pixelDataRGB[i][2] = pixelDataRGB[i][2]/norme;
            pixelDataRGB[i][3] = pixelDataRGB[i][3]/norme;
        }
        for (int i = 0 ; i < pixelDataFinal.length ; i++){
            pixelDataFinal[i] = Color.argb(pixelDataRGB[i][0], pixelDataRGB[i][1], pixelDataRGB[i][2], pixelDataRGB[i][3]);
        }
        return pixelDataFinal;
    }

    void blur(Bitmap bMap , int k, int[][] mask){// apply blur on a Bitmap

        int [] pixelData = blur_data(bMap, k, mask);
        bMap.setPixels(pixelData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());
    }

    void outline(Bitmap bMap){//apply the outline detection algorithm
        int width = bMap.getWidth();
        int height = bMap.getHeight();
        int[] pixelData = new int[width*height];
        bMap.getPixels(pixelData,0,width,0,0,width,height);
        pixelData = gradient(outline_oriented(pixelData,width,height,masque_sobel_horizontal()),outline_oriented(pixelData,width,height,masque_sobel_vertical()));// we apply gradient for 2 outline oriented
        bMap.setPixels(pixelData,0,width,0,0,width,height);

    }

    int[][] outline_oriented(int[] pixelData,int width,int height, int[][] masque){// return the oriented gradient data for fixed mask

        int[][] pixelDataARGB = convolution(pixelData,width,height,masque);
        int[][] returnedData = new int[pixelData.length][2];
        int grey;
        for (int i = 0 ; i < pixelData.length ; i++){
            grey = (int)(Math.abs(pixelDataARGB[i][1])*0.30 + Math.abs(pixelDataARGB[i][2])*0.59 + Math.abs(pixelDataARGB[i][3])*0.11);

            returnedData[i][1] = grey;
            returnedData[i][0] = Color.alpha(pixelData[i]);
        }
        return returnedData;
    }

    int[] gradient(int[][] horizontalPixelData, int[][] verticalPixelData){//return the gradient module of two gradient data, the returned values are normalized
        int[] returnedData = new int[horizontalPixelData.length];
        if (horizontalPixelData.length != verticalPixelData.length){
            Log.e("ERROR","in gradient function");
            return returnedData;
        }
        int max = 0;
        for (int i = 0; i < horizontalPixelData.length; i++){
            returnedData[i] = (int)Math.sqrt(horizontalPixelData[i][1]*horizontalPixelData[i][1]+verticalPixelData[i][1]*verticalPixelData[i][1]);
            if(max < returnedData[i]){
                max = returnedData[i];
            }
        }
        int normalized_value = 0;
        for (int i = 0; i < horizontalPixelData.length; i++){
            normalized_value = (returnedData[i]*255)/max;
            returnedData[i] = Color.argb(255,normalized_value,normalized_value,normalized_value);
        }
        return returnedData;
    }

    int norm_for_blurmask(int[][] mask){//give the number wich will be used to normalize in blur function
        int norm = 0;
        for (int i = 0; i < mask.length;i++){
            for (int j = 0; j < mask[i].length; j++){
                norm = norm + mask[i][j];
            }
        }
        if(norm != 0) {
            return norm;
        }else{
            return 1;
        }
    }

    int[][] average_mask(int k){//create the average mask of size 2*k +1
        int[][] mask = new int[2*k+1][2*k+1];
        for(int i = 0 ; i < 2*k+1 ; i++){
            for (int j = 0 ; j < 2*k+1 ; j++){
                mask[i][j]= 1;
            }
        }
        return mask;
    }

    int[][] masque_sobel_vertical(){//create a fixed mask wich are used for vertical gradient in convolution
        int[][] masque = new int[3][3];
        for(int i = 0 ; i < 3 ; i++){
            masque[i][1] = 0;
        }
        masque[0][0] = -1;
        masque[1][0] = -2;
        masque[2][0] = -1;
        masque[0][2] = 1;
        masque[1][2] = 2;
        masque[2][2] = 1;

        return masque;
    }

    int[][] masque_sobel_horizontal(){//create a fixed mask wich are used for vertical gradient
        int[][] masque = new int[3][3];
        for(int i = 0 ; i < 3 ; i++){
            masque[1][i] = 0;
        }
        masque[0][0] = -1;
        masque[0][1] = -2;
        masque[0][2] = -1;
        masque[2][0] = 1;
        masque[2][1] = 2;
        masque[2][2] = 1;

        return masque;
    }


    private  void  toGreyRS(Bitmap  bmp) {
        //1)  Creer un  contexte  RenderScript
        RenderScript rs = RenderScript.create(this);
        //2)  Creer  des  Allocations  pour  passer  les  donnees
        Allocation input = Allocation.createFromBitmap(rs , bmp);
        Allocation  output = Allocation.createTyped(rs , input.getType ());
        //3)  Creer le  script
        ScriptC_toGrey greyScript = new  ScriptC_toGrey(rs);
        //4)  Copier  les  donnees  dans  les  Allocations
        // ...
        //5)  Initialiser  les  variables  globales  potentielles

        //6)  Lancer  le noyau
        greyScript.forEach_toGrey(input , output);
        //7)  Recuperer  les  donnees  des  Allocation(s)
        output.copyTo(bmp);
        //8)  Detruire  le context , les  Allocation(s) et le  script
        input.destroy (); output.destroy ();
        greyScript.destroy (); rs.destroy ();
    }


    private void randomHueRS(Bitmap bmp){

        //Get image size
        Random random = new Random();
        int rdNumber = random.nextInt(361);
        Log.e("rdNumber", Integer.toString(rdNumber));
        //Create renderscript
        RenderScript rs = RenderScript.create(this);
        //Create allocation from Bitmap
        Allocation allocationA = Allocation.createFromBitmap(rs, bmp);
        //Create allocation with same type
        Allocation allocationB = Allocation.createTyped(rs, allocationA.getType());
        //Create script from rs file.
        ScriptC_randomHue randomHueScript = new ScriptC_randomHue(rs);
        //Set size in script
        randomHueScript.set_hue(rdNumber);
        //Call the first kernel.
        randomHueScript.forEach_randomHue(allocationA, allocationB);

        //Copy script result into bitmap
        allocationB.copyTo(bmp);
        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        randomHueScript.destroy();
        rs.destroy();

    }



    void histogramEqualizationRS(Bitmap image) {
        //Get image size
        int width = image.getWidth();
        int height = image.getHeight();

        //Create new bitmap
        Bitmap res = image.copy(image.getConfig(), true);
        //Create renderscript
        RenderScript rs = RenderScript.create(this);
        //Create allocation from Bitmap
        Allocation allocationA = Allocation.createFromBitmap(rs, res);
        //Create allocation with same type
        Allocation allocationB = Allocation.createTyped(rs, allocationA.getType());
        //Create script from rs file.
        ScriptC_histEq histEqScript = new ScriptC_histEq(rs);
        //Set size in script
        histEqScript.set_size(width*height);
        //Call the first kernel.
        histEqScript.forEach_root(allocationA, allocationB);
        //Call the rs method to compute the remap array
        histEqScript.invoke_createRemapArray();
        //Call the second kernel
        histEqScript.forEach_remaptoRGB(allocationB, allocationA);
        //Copy script result into bitmap
        allocationA.copyTo(image);
        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        histEqScript.destroy();
        rs.destroy();

    }

    void keepColorRS(Bitmap image, int color) {

        //Create renderscript
        RenderScript rs = RenderScript.create(this);
        //Create allocation from Bitmap
        Allocation input = Allocation.createFromBitmap(rs, image);
        //Create allocation with same type
        Allocation output = Allocation.createTyped(rs, input.getType());
        //Create script from rs file.
        ScriptC_keepColor keepColorScript = new ScriptC_keepColor(rs);
        //Set size in script
        keepColorScript.set_hue(color);
        //Call the first kernel.
        keepColorScript.forEach_keepColor(input, output);
        //Copy script result into bitmap
        output.copyTo(image);
        //Destroy everything to free memory
        input.destroy();
        output.destroy();
        keepColorScript.destroy();
        rs.destroy();

    }



    Spinner sp;//spinner or displaying all the effects
    ArrayAdapter <String> adapter;
    String effects[] = {"Grey","Random Hue","Keep Color","Blur","Outline","Linear Contrast RGB","Linear Contrast HSV","Plane Contrast RGB","Plane Contrast HSV","toGrey RS","plane hist RS","keep color RS","Random hue RS"};//exhaustive list of the different effects
    String selectedEffect = "Grey";//initialisation with the grey effect
    int selectedImg = 0;//initialisation with the grey effect which have 0 as ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApplyButton = findViewById(R.id.spinnerbutton);
        mChangeImg = findViewById(R.id.changeImgButton);
        mReset = findViewById(R.id.reset);

        image = findViewById(R.id.imageView1);

        sp = (Spinner) findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, effects);
        sp.setAdapter(adapter);

        opts.inMutable = true;
        bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test0, opts);
        image.setImageBitmap(bMap);

        final int height = bMap.getHeight();
        final int width = bMap.getWidth();


        //update the selected effect chosen with the spinner (from effects list)
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedEffect = effects[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //apply the selected effect on the image
        mApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long start = System.currentTimeMillis();
                switch (selectedEffect)
                {
                    case "Grey":
                        grey(bMap);
                        Log.e("TEMPS",Long.toString(System.currentTimeMillis() - start));
                        break;
                    case "Blur":
                        blur(bMap,5, average_mask(5));
                        Log.e("TEMPS",Long.toString(System.currentTimeMillis() - start));
                        break;
                    case "Outline":
                        outline(bMap);
                        Log.e("TEMPS",Long.toString(System.currentTimeMillis() - start));
                        break;
                    case "Random Hue":
                        randomHue(bMap);
                        Log.e("TEMPS",Long.toString(System.currentTimeMillis() - start));
                        break;
                    case "Keep Color":
                        keepColor(bMap,10);
                        Log.e("TEMPS",Long.toString(System.currentTimeMillis() - start));
                        break;
                    case "Linear Contrast RGB":
                        linear_contrast_ARGB(bMap);
                        Log.e("TEMPS",Long.toString(System.currentTimeMillis() - start));
                        break;
                    case "Linear Contrast HSV":
                        linear_contrast_HSV(bMap,1000);
                        Log.e("TEMPS",Long.toString(System.currentTimeMillis() - start));
                        break;
                    case "Plane Contrast RGB":
                        plane_histog_contrast_ARGB(bMap);
                        Log.e("TEMPS",Long.toString(System.currentTimeMillis() - start));
                        break;
                    case "Plane Contrast HSV":
                        plane_histog_contrast_HSV(bMap,1000);
                        Log.e("TEMPS",Long.toString(System.currentTimeMillis() - start));
                        break;
                    case "toGrey RS":
                        toGreyRS(bMap);
                        Log.e("TEMPS",Long.toString(System.currentTimeMillis() - start));
                        break;
                    case "plane hist RS":
                        histogramEqualizationRS(bMap);
                        Log.e("TEMPS",Long.toString(System.currentTimeMillis() - start));
                        break;
                    case "keep color RS":
                        keepColorRS(bMap, 120);
                        Log.e("TEMPS",Long.toString(System.currentTimeMillis() - start));
                        break;
                    case "Random hue RS":
                        randomHueRS(bMap);
                        Log.e("TEMPS",Long.toString(System.currentTimeMillis() - start));
                        break;


                }
                image.setImageBitmap(bMap);
            }
        });

        //Reset the displayed image to the original one
        mReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(selectedImg == 0) {
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test0, opts);
                    image.setImageBitmap(bMap);
                }else if(selectedImg == 1){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test1, opts);
                    image.setImageBitmap(bMap);
                }else if(selectedImg == 2){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test2, opts);
                    image.setImageBitmap(bMap);
                }else if(selectedImg == 3){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test3, opts);
                    image.setImageBitmap(bMap);
                }else if(selectedImg == 4){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test4, opts);
                    image.setImageBitmap(bMap);
                }else if(selectedImg == 5){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test5, opts);
                    image.setImageBitmap(bMap);
                }else if(selectedImg == 6){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test6, opts);
                    image.setImageBitmap(bMap);
                }else if(selectedImg == 7){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test7, opts);
                    image.setImageBitmap(bMap);
                }
            }
        });

        //button which change the the image displayed by the bitmap
        mChangeImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(selectedImg == 0) {
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test1, opts);
                    image.setImageBitmap(bMap);
                    selectedImg++;
                }else if(selectedImg == 1){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test2, opts);
                    image.setImageBitmap(bMap);
                    selectedImg++;
                }else if(selectedImg == 2){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test3, opts);
                    image.setImageBitmap(bMap);
                    selectedImg++;
                }else if(selectedImg == 3){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test4, opts);
                    image.setImageBitmap(bMap);
                    selectedImg++;
                }else if(selectedImg == 4){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test5, opts);
                    image.setImageBitmap(bMap);
                    selectedImg++;
                }else if(selectedImg == 5){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test6, opts);
                    image.setImageBitmap(bMap);
                    selectedImg++;
                }else if(selectedImg == 6){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test7, opts);
                    image.setImageBitmap(bMap);
                    selectedImg++;
                }else if(selectedImg == 7){
                    bMap = BitmapFactory.decodeResource(getResources(), R.drawable.test0, opts);
                    image.setImageBitmap(bMap);
                    selectedImg = 0;
                }

            }
        });


    }
}