package com.example.acoste.projetimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;
import android.util.Log;

import com.android.rssample.ScriptC_linearContrast;
import com.example.acoste.projetimage.Convolution;
import com.example.acoste.projetimage.Histogram;
import com.example.q.renderscriptexample.ScriptC_histEq;
//import com.android.rssample.linearContrast;
import com.android.rssample.ScriptC_convolution;


/**
 * Created by acoste on 08/02/19.
 */

public class Advanced extends Effects {

    public Advanced(Bitmap bMap){
        super(bMap);
    }

    int[] blur_data(Bitmap bMap, int k, int[][] mask){// return the colro data of a blured Bitmap

        int norme = Convolution.norm_for_blurmask(mask);

        int[] pixelData = Convolution.mirrorBitmap(bMap, k);
        int[][] pixelDataRGB = Convolution.convolutionOnMirror( pixelData, k, bMap.getWidth()+2*k, bMap.getHeight()+2*k, mask);
        int[] pixelDataFinal = new int[bMap.getWidth() * bMap.getHeight()];

        for (int i = 0 ; i < pixelDataFinal.length ; i++){
            pixelDataRGB[i][1] = pixelDataRGB[i][1] / norme;
            pixelDataRGB[i][2] = pixelDataRGB[i][2] / norme;
            pixelDataRGB[i][3] = pixelDataRGB[i][3] / norme;
        }
        for (int i = 0 ; i < pixelDataFinal.length ; i++){
            pixelDataFinal[i] = Color.argb(pixelDataRGB[i][0], pixelDataRGB[i][1], pixelDataRGB[i][2], pixelDataRGB[i][3]);
        }
        return pixelDataFinal;
    }

    void blur(Bitmap bMap , int k, int[][] mask){// apply blur on a Bitmap

        int[] pixelData = blur_data(bMap, k, mask);
        bMap.setPixels(pixelData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
    }

    void outline(Bitmap bMap){//apply the outline detection algorithm
        int width = bMap.getWidth();
        int height = bMap.getHeight();
        int[] pixelData = new int[width*height];
        bMap.getPixels(pixelData, 0, width, 0, 0, width, height);
        pixelData = Convolution.gradient(Convolution.outline_oriented(pixelData, width, height, Convolution.mask_sobel_horizontal()), Convolution.outline_oriented(pixelData, width, height, Convolution.mask_sobel_vertical()));// we apply gradient for 2 outline oriented
        bMap.setPixels(pixelData, 0, width, 0, 0, width, height);

    }

    public int[][] linear_contrast_ARGB(Bitmap bMap){//apply linear contrast (ARGB color space) on a Bitmap and return original histogram and associated LUT
        int[][] histog = new int[2][];//allowing memory for returned value wich are histogram
        int[] colorData = new int[bMap.getHeight() * bMap.getWidth()];
        bMap.getPixels(colorData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        histog[0] = Histogram.getGreyHistogram(colorData);

        int[] minmax = Histogram.minmaxHisto(histog[0]);

        histog[1] = Histogram.create_linear_contrast_lut(minmax, histog[0].length);//getting LUT (shades of grey - ARGB color space)
        for (int i = 0 ; i < colorData.length; i++) {
            int red = Color.red(colorData[i]);
            int green = Color.green(colorData[i]);
            int blue = Color.blue(colorData[i]);

            if (red < minmax[0]){//setting new colors from LUT
                red = 0;
            }else if(red > minmax[1]){
                red = 255;
            }else{
                red = histog[1][red];
            }
            if (green < minmax[0]){
                green = 0;
            }else if(green > minmax[1]){
                green = 255;
            }else{
                green = histog[1][green];
            }
            if (blue < minmax[0]){
                blue = 0;
            }else if(blue > minmax[1]){
                blue = 255;
            }else{
                blue = histog[1][blue];
            }
            colorData[i]= Color.argb(Color.alpha(colorData[i]), red, green, blue);
        }
        bMap.setPixels(colorData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        return histog;
    }

    public int[][] linear_contrast_HSV(Bitmap bMap, int precision){//apply linear contrast (HSV color space) on a Bitmap and return original histogram and associated LUT
        int [][] histog = new int[2][];//allowing memory for returned value wich are histogram
        int[] colorData = new int[bMap.getHeight() * bMap.getWidth()];
        bMap.getPixels(colorData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        histog[0] = Histogram.getValueHistogram(colorData, precision);//getting Value histogram (HSV color space)
        int[] minmax = Histogram.minmaxHisto(histog[0]);//getting minimum and maximum of the histogram
        histog[1] = Histogram.create_linear_contrast_lut(minmax, histog[0].length);//creating LUT from original histogram

        float[] HSV = new float[3];
        for (int i = 0 ; i < colorData.length ; i++) {
            Color.colorToHSV(colorData[i], HSV);
            HSV[2] = ((float)(histog[1][(int)(HSV[2] * (histog[1].length - 1))]) / ((float)(histog[1].length - 1)));//lot of casts wich are necessary for float division and modifying the Value
            colorData[i]= Color.HSVToColor(Color.alpha(colorData[i]), HSV);
        }
        bMap.setPixels(colorData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        return histog;
    }

    public int[][] equalization_contrast_hsv(Bitmap bMap, int precision){//apply plane contrast (HSV color space) on a Bitmap and return original histogram and associated LUT
        int [][] histog = new int[2][];//allowing memory for returned value wich are histogram
        int[] colorData = new int[bMap.getHeight() * bMap.getWidth()];
        bMap.getPixels(colorData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        histog[0] = Histogram.getValueHistogram(colorData, precision);//getting Value histogram (HSV color space)
        histog[1] = Histogram.create_plane_histog_lut(histog[0]);//Creating LUT wich will be returned

        float[] HSV = new float[3];
        for (int i = 0 ; i < colorData.length ; i++) {
            Color.colorToHSV(colorData[i], HSV);
            HSV[2] = ((float)(histog[1][(int)(HSV[2] * (histog[1].length - 1))]) / ((float)(histog[1].length - 1)));//lot of casts wich are necessary for float division and modifying the Value
            colorData[i] = Color.HSVToColor(Color.alpha(colorData[1]), HSV);
        }
        bMap.setPixels(colorData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        return histog;
    }

    public int[][] equalization_contrast_argb(Bitmap bMap){//apply plane contrast (ARGB color space) on a Bitmap and return original histogram and associated LUT
        int [][] histog = new int[2][];//allowing memory for returned value wich are histogram
        int[] colorData = new int[bMap.getHeight() * bMap.getWidth()];
        bMap.getPixels(colorData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        histog[0] = Histogram.getGreyHistogram(colorData);//getting Value histogram (ARGB color space)
        histog[1] = Histogram.create_plane_histog_lut(histog[0]);//Creating LUT wich will be returned

        for (int i = 0 ; i < colorData.length ; i++) {
            int red = Color.red(colorData[i]);// getting RGB component
            int green = Color.green(colorData[i]);
            int blue = Color.blue(colorData[i]);

            red = histog[1][red];
            green = histog[1][green];
            blue = histog[1][blue];

            colorData[i]= Color.argb(Color.alpha(colorData[i]), red, green, blue);//on met a jour les data qu'on vas utilisé dans set pixel
        }
        bMap.setPixels(colorData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        return histog;
    }

    void linearContrastRS(Bitmap image, Context context) {

        //Get image size
        int width = image.getWidth();
        int height = image.getHeight();

        //Create new bitmap
        Bitmap res = image.copy(image.getConfig(), true);
        //Create renderscript
        RenderScript rs = RenderScript.create(context);
        //Create allocation from Bitmap
        Allocation allocationA = Allocation.createFromBitmap(rs, res);
        //Create allocation with same type
        Allocation allocationB = Allocation.createTyped(rs, allocationA.getType());
        //Create script from rs file.
        ScriptC_linearContrast linearContrastScript = new ScriptC_linearContrast(rs);
        //Set size in script
        linearContrastScript.set_size(width*height);
        //Call the first kernel.
        linearContrastScript.forEach_create_histog(allocationA, allocationB);
        //Call the rs method to compute the remap array
        linearContrastScript.invoke_createLut();
        //Call the second kernel
        linearContrastScript.forEach_apply_contrast(allocationB, allocationA);
        //Copy script result into bitmap
        allocationA.copyTo(image);
        //Destroy everything to free memory
        allocationA.destroy();
        allocationB.destroy();
        linearContrastScript.destroy();
        rs.destroy();

    }


    void equalization_contrast_RS(Bitmap image, Context context) {
        //Get image size
        int width = image.getWidth();
        int height = image.getHeight();

        //Create new bitmap
        Bitmap res = image.copy(image.getConfig(), true);
        //Create renderscript
        RenderScript rs = RenderScript.create(context);
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

    int convolution_RS(Bitmap image, Context context, int[] mask, int mask_line_length, int norm) {

        if (mask.length % mask_line_length != 0 && (mask.length / mask_line_length)%2 !=0 && mask_line_length%2 !=0){
            Log.e("ERROR","Wrong arguments in convolution_RS");
            return -1;
        }
        int mask_collumn_height = mask.length / mask_line_length;

        //Create new bitmap
        Bitmap res = image.copy(image.getConfig(), true);
        //Create renderscript
        RenderScript rs = RenderScript.create(context);

        ScriptC_convolution convolutionScript = new ScriptC_convolution(rs);

        Allocation data = Allocation.createSized(rs, Element.U8_4(rs),image.getHeight()*image.getWidth());

        Allocation img_alloc = Allocation.createFromBitmap(rs, res);

        Allocation mask_alloc = Allocation.createSized(rs, Element.I32(rs), mask.length);
        mask_alloc.copyFrom(mask);
        convolutionScript.bind_mask(mask_alloc);

        if (norm != 0)
            convolutionScript.set_norm(norm);

        convolutionScript.set_height(image.getHeight());
        convolutionScript.set_width(image.getWidth());
        convolutionScript.set_mask_collumn_height(mask_collumn_height);
        convolutionScript.set_mask_line_length(mask_line_length);

        convolutionScript.invoke_map_img(img_alloc, data);

        convolutionScript.bind_data(data);

        convolutionScript.forEach_compute_data(img_alloc,img_alloc);

        img_alloc.copyTo(image);

        data.destroy();
        img_alloc.destroy();
        convolutionScript.destroy();
        rs.destroy();

        return 0;
    }

    int blur_moy_RS(Bitmap image, Context context, int intensity){

        if (intensity< 0){
            return -1;
        }
        int[] mask = new int[(intensity*2+1)*(intensity*2+1)];
        for (int i = 0 ; i < mask.length ; i++){
            mask[i] = 1;
        }
        int norm = mask.length;

        int mask_line_length = intensity*2+1;

        convolution_RS(image, context,  mask,  mask_line_length,  norm);

        return 0;
    }

    int blur_gaussian5x5_RS(Bitmap image, Context context){


        int[] mask = new int[25];

        mask[0] = mask[4] = mask[20] = mask[24] = 1;
        mask[1] = mask[3] = mask[5] = mask[9] = mask[15] = mask[19] = mask[21] = mask[23] = 4;
        mask[2] = mask[10] = mask[14] = mask[22] = 7;
        mask[6] = mask[8] = mask[16] = mask[18] = 16;
        mask[7] = mask[11] = mask[13] = mask[17] = 26;
        mask[12] = 41;

        int norm = 273;

        int mask_line_length = 5;

        convolution_RS(image, context, mask, mask_line_length, norm);

        return 0;
    }

    int sobel_horizontal(Bitmap image, Context context){

        int[] mask = new int[9];

        mask[0] = mask[3] = mask[6] = 1;
        mask[2] = mask[5] = mask[8] = -1;

        int norm = 3;

        int mask_line_length = 3;

        convolution_RS(image, context, mask, mask_line_length, norm);

        return 0;

    }

    int sobel_vertical(Bitmap image, Context context){

        int[] mask = new int[9];

        mask[0] = mask[1] = mask[2] = 1;
        mask[6] = mask[7] = mask[8] = -1;

        int norm = 3;

        int mask_line_length = 3;

        convolution_RS(image, context, mask, mask_line_length, norm);

        return 0;

    }

    int laplacian_mask(Bitmap image, Context context){

        int[] mask = new int[9];

        mask[0] = mask[1] = mask[2] = mask[3] = mask[5] = mask[6] = mask[7] = mask[8] = 1;
        mask[4] = -8;

        int norm = 8;

        int mask_line_length = 3;

        convolution_RS(image, context, mask, mask_line_length, norm);

        return 0;

    }


}
