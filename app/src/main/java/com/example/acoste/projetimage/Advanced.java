package com.example.acoste.projetimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.util.Log;
import com.example.acoste.projetimage.Simple;
import java.lang.Math.*;
import com.android.rssample.ScriptC_linearContrast;
import com.android.rssample.ScriptC_medianFilter;
import com.example.acoste.projetimage.Convolution; // we don't know why it works without those two imports
import com.example.acoste.projetimage.Histogram;
import com.example.q.renderscriptexample.ScriptC_histEq;
import com.android.rssample.ScriptC_convolution;
import com.android.rssample.ScriptC_bilateralFilter;
import com.android.rssample.ScriptC_minFilter;
import com.android.rssample.ScriptC_medianFilter;
import com.android.rssample.ScriptC_sobelGradient;
import com.android.rssample.ScriptC_colorPartition;
import com.android.rssample.ScriptC_drawOutline;
import com.android.rssample.ScriptC_blendDivide;
import com.android.rssample.ScriptC_pixelise;

import static java.lang.StrictMath.pow;

/**
 * Created by acoste on 08/02/19.
 */

public class Advanced{

    private static int[] blur_aux(Bitmap bMap, int k, int[][] mask){// return the colro data of a blured Bitmap

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

    static void blur(Bitmap bMap , int k, int[][] mask){// apply blur on a Bitmap
        int[] pixelData = blur_aux(bMap, k, mask);
        bMap.setPixels(pixelData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
    }

    static void outline(Bitmap bMap){//apply the outline detection algorithm
        int width = bMap.getWidth();
        int height = bMap.getHeight();
        int[] pixelData = new int[width*height];
        bMap.getPixels(pixelData, 0, width, 0, 0, width, height);
        pixelData = Convolution.gradient(Convolution.outline_oriented(pixelData, width, height, Convolution.mask_sobel_horizontal()), Convolution.outline_oriented(pixelData, width, height, Convolution.mask_sobel_vertical()));// we apply gradient for 2 outline oriented
        bMap.setPixels(pixelData, 0, width, 0, 0, width, height);

    }

    /***
     * increase linearly cantrast of an image using RGB space
     * @param bMap image to modify
     * @return unused function return histogram to have the possibility of displaying them later
     */
    static int [][] linear_contrast_ARGB(Bitmap bMap){//apply linear contrast (ARGB color space) on a Bitmap and return original histogram and associated LUT
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

    /***
     * increase linearly cantrast of an image using HSV space
     * @param bMap image to modify
     * @param precision number of different valur of the LUT
     */
    static void linear_contrast_HSV(Bitmap bMap, int precision){//apply linear contrast (HSV color space) on a Bitmap and return original histogram and associated LUT
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

    }

    /***
     * equalize luminosity histogram of an image in java using HSV space
     * @param bMap image to modify
     * @param precision number of different valur of the LUT
     */
    static void equalization_contrast_HSV(Bitmap bMap, int precision){//apply plane contrast (HSV color space) on a Bitmap and return original histogram and associated LUT
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
    }

    /***
     * equalize luminosity histogram of an image in java using RGB space
     * @param bMap image to modify
     */
    static void equalization_contrast_ARGB(Bitmap bMap){//apply plane contrast (ARGB color space) on a Bitmap and return original histogram and associated LUT
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
    }

    /***
     * change the contrast of the image
     * @param image the image to modify
     * @param context context of Activity
     * @param new_min new_min belong to [0...new_max]
     * @param new_max new_max belong to [new_min+1...999]
     */
    static void linearContrastRS(Bitmap image, Context context, int new_min, int new_max) {

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

        linearContrastScript.set_new_max(new_max);
        linearContrastScript.set_new_min(new_min);

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

    /***
     * increase the contrast of a image
     * @param image the image to modify
     * @param context context of Activity
     * @param intensity belong to [0.0...1.0], 0.0 has no effect, 1.0 is fully equalized luminosity histogram
     */
    static void equalization_contrast_RS(Bitmap image, Context context, float intensity) {
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
        histEqScript.set_intensity(intensity);
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

    /***
     * apply a convolution with dynamic mask on the image
     * @param image the image to modify
     * @param context context of Activity
     * @param mask the mask to use in a 1D array
     * @param mask_line_length the length if each line used to simulate a 2D array mask
     * @param norm the value used to normalize
     */
    static void convolution_RS(Bitmap image, Context context, float[] mask, int mask_line_length, float norm) {

        if (mask.length % mask_line_length != 0 && (mask.length / mask_line_length)%2 !=0 && mask_line_length%2 !=0){
            Log.e("ERROR","Wrong arguments in convolution_RS");
        }

        int mask_collumn_height = mask.length / mask_line_length;

        //Create new bitmap
        Bitmap res = image.copy(image.getConfig(), true);
        //Create renderscript
        RenderScript rs = RenderScript.create(context);

        ScriptC_convolution convolutionScript = new ScriptC_convolution(rs);

        Allocation data = Allocation.createSized(rs, Element.U8_4(rs),image.getHeight()*image.getWidth());

        Allocation img_alloc = Allocation.createFromBitmap(rs, res);

        Allocation mask_alloc = Allocation.createSized(rs, Element.F32(rs), mask.length);
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

    }

    /***
     * apply a bilateral filter on the image
     * @param image the image to modify
     * @param context context of Activity
     * @param intensity define the size of the mask to apply (intensity * intensity)
     */
    static void bilateralfilter_RS(Bitmap image, Context context, int intensity) {

        //Create new bitmap
        Bitmap res = image.copy(image.getConfig(), true);
        //Create renderscript
        RenderScript rs = RenderScript.create(context);

        ScriptC_bilateralFilter bilateralFilterScript = new ScriptC_bilateralFilter(rs);

        Allocation data = Allocation.createSized(rs, Element.U8_4(rs),image.getHeight()*image.getWidth());

        Allocation img_alloc = Allocation.createFromBitmap(rs, res);

        bilateralFilterScript.set_height(image.getHeight());
        bilateralFilterScript.set_width(image.getWidth());
        bilateralFilterScript.set_intensity(intensity);
        float sigma = (float)(intensity/2.7);
        bilateralFilterScript.set_sigma(sigma);

        bilateralFilterScript.invoke_map_img(img_alloc, data);

        bilateralFilterScript.bind_data(data);

        bilateralFilterScript.forEach_compute_data(img_alloc,img_alloc);

        img_alloc.copyTo(image);

        data.destroy();
        img_alloc.destroy();
        bilateralFilterScript.destroy();
        rs.destroy();

    }

    /***
     * apply a min filter on the image
     * @param image the image to modify
     * @param context context of Activity
     * @param intensity define the size of the mask to apply (intensity * intensity)
     */
    static void minfilter_RS(Bitmap image, Context context, int intensity) {

        //Create new bitmap
        Bitmap res = image.copy(image.getConfig(), true);
        //Create renderscript
        RenderScript rs = RenderScript.create(context);

        ScriptC_minFilter minFilterScript = new ScriptC_minFilter(rs);

        Allocation data = Allocation.createSized(rs, Element.U8_4(rs),image.getHeight()*image.getWidth());

        Allocation img_alloc = Allocation.createFromBitmap(rs, res);

        minFilterScript.set_height(image.getHeight());
        minFilterScript.set_width(image.getWidth());
        minFilterScript.set_intensity(intensity);

        minFilterScript.invoke_map_img(img_alloc, data);

        minFilterScript.bind_data(data);

        minFilterScript.forEach_compute_data(img_alloc,img_alloc);

        img_alloc.copyTo(image);

        data.destroy();
        img_alloc.destroy();
        minFilterScript.destroy();
        rs.destroy();

    }

    /***
     * apply a median filter on the image
     * @param image the image to modify
     * @param context context of Activity
     * @param intensity define the size of the mask to apply (intensity * intensity)
     */
    static void medianfilter_RS(Bitmap image, Context context, int intensity) {

        //Create new bitmap
        Bitmap res = image.copy(image.getConfig(), true);
        //Create renderscript
        RenderScript rs = RenderScript.create(context);

        ScriptC_medianFilter medianFilterScript = new ScriptC_medianFilter(rs);

        Allocation data = Allocation.createSized(rs, Element.U8_4(rs),image.getHeight()*image.getWidth());

        Allocation img_alloc = Allocation.createFromBitmap(rs, res);

        medianFilterScript.set_height(image.getHeight());
        medianFilterScript.set_width(image.getWidth());
        medianFilterScript.set_intensity(intensity);

        medianFilterScript.invoke_map_img(img_alloc, data);

        medianFilterScript.bind_data(data);

        medianFilterScript.forEach_compute_data(img_alloc,img_alloc);

        img_alloc.copyTo(image);

        data.destroy();
        img_alloc.destroy();
        medianFilterScript.destroy();
        rs.destroy();

    }

    /***
     * get the edge of an image and draw them on a white layer
     * @param image the image to modify
     * @param context context of Activity
     */
    static void sobelGradient_RS(Bitmap image, Context context){

        Simple.toGreyRS(image,context);// we want to work on luminosity

        Bitmap copy = image.copy(image.getConfig(),true);

        sobel_horizontal_RS(image, context);//get horizontal gradient
        sobel_vertical_RS(copy,context);//get vertical gradient

        RenderScript rs = RenderScript.create(context);

        ScriptC_sobelGradient sobelGradientScript = new ScriptC_sobelGradient(rs);

        Allocation img_alloc_horiz = Allocation.createFromBitmap(rs, image);

        Allocation img_alloc_verti = Allocation.createFromBitmap(rs, copy);

        Allocation data = Allocation.createSized(rs, Element.U8_4(rs),image.getHeight()*image.getWidth());

        sobelGradientScript.set_width(image.getWidth());
        sobelGradientScript.set_height(image.getHeight());

        sobelGradientScript.invoke_map_img(img_alloc_verti, data);
        sobelGradientScript.bind_data(data);

        sobelGradientScript.forEach_outline(img_alloc_horiz,img_alloc_horiz);

        img_alloc_horiz.copyTo(image);

        img_alloc_horiz.destroy();
        sobelGradientScript.destroy();
        rs.destroy();

    }

    /***
     * get the edge of an image and draw them on a white layer
     * @param image the image to modify
     * @param context context of Activity
     */
    static void sobelGradientColored_RS(Bitmap image, Context context){

        Bitmap copy = image.copy(image.getConfig(),true);

        sobel_horizontal_RS(image, context);//get horizontal gradient
        sobel_vertical_RS(copy,context);//get vertical gradient

        RenderScript rs = RenderScript.create(context);

        ScriptC_sobelGradient sobelGradientScript = new ScriptC_sobelGradient(rs);

        Allocation img_alloc_horiz = Allocation.createFromBitmap(rs, image);

        Allocation img_alloc_verti = Allocation.createFromBitmap(rs, copy);

        Allocation data = Allocation.createSized(rs, Element.U8_4(rs),image.getHeight()*image.getWidth());

        sobelGradientScript.set_width(image.getWidth());
        sobelGradientScript.set_height(image.getHeight());

        sobelGradientScript.invoke_map_img(img_alloc_verti, data);
        sobelGradientScript.bind_data(data);

        sobelGradientScript.forEach_outline(img_alloc_horiz,img_alloc_horiz);

        img_alloc_horiz.copyTo(image);

        img_alloc_horiz.destroy();
        sobelGradientScript.destroy();
        rs.destroy();

    }

    /***
     * blend divide an image with another image
     * @param image the image to modify
     * @param context context of Activity
     * @param divider the image which divide
     */
    static void blendDivide_RS(Bitmap image, Context context, Bitmap divider){

        RenderScript rs = RenderScript.create(context);

        ScriptC_blendDivide blendDivideScript = new ScriptC_blendDivide(rs);

        Allocation img = Allocation.createFromBitmap(rs , image);
        Allocation div = Allocation.createFromBitmap(rs , divider);

        Allocation data = Allocation.createSized(rs, Element.U8_4(rs),divider.getHeight()*divider.getWidth());

        blendDivideScript.set_height(image.getHeight());
        blendDivideScript.set_width(image.getWidth());
        blendDivideScript.invoke_map_img(div, data);
        blendDivideScript.bind_data(data);

        blendDivideScript.forEach_compute_data(img, img);

        img.copyTo(image);

        img.destroy();
        data.destroy();

        blendDivideScript.destroy();
        rs.destroy();

    }

    /***
     * pixelise an image
     * @param image the image to modify
     * @param context context of Activity
     * @param pixel_size the size chosen for pixels
     */
    static void pixelise_RS(Bitmap image, Context context, int pixel_size){

        //1)  Creer un  contexte  RenderScript
        RenderScript rs = RenderScript.create(context);
        //2)  Creer  des  Allocations  pour  passer  les  donnees
        Allocation input = Allocation.createFromBitmap(rs , image);
        Allocation  output = Allocation.createTyped(rs , input.getType ());
        //3)  Creer le  script
        ScriptC_pixelise pixeliseScript = new  ScriptC_pixelise(rs);
        //4)  Copier  les  donnees  dans  les  Allocations
        Allocation lut = Allocation.createSized(rs, Element.F32_4(rs),image.getHeight()/pixel_size*image.getWidth()/pixel_size);
        pixeliseScript.set_height(image.getHeight());
        pixeliseScript.set_width(image.getWidth());
        pixeliseScript.set_pixel_size(pixel_size);
        pixeliseScript.invoke_map_lut(input, lut);
        pixeliseScript.bind_lut(lut);

        //6)  Lancer  le noyau
        pixeliseScript.forEach_compute_data(input , input);
        //7)  Recuperer  les  donnees  des  Allocation(s)
        input.copyTo(image);
        //8)  Detruire  le context , les  Allocation(s) et le  script
        input.destroy (); output.destroy ();
        pixeliseScript.destroy (); rs.destroy ();

    }

    /***
     * apply a cartoon effect on the image
     * @param image the image to modify
     * @param context context of Activity
     * @param first_medianfilter_size param of first auxiliary function "medianfilter_RS"
     * @param minfilter_size param of auxiliary function "minfilter_RS"
     * @param final_medianfilter_size param of final auxiliary function "medianfilter_RS"
     * @param edge_precision param of auxiliary function "drawOutline_RS"
     * @param outline_size param of auxiliary function "drawOutline_RS"
     * @param color_precision param of auxiliary function "colorPartition_RS"
     * @param shade_precision param of auxiliary function "colorPartition_RS"
     * @param color_shift param of auxiliary function "colorPartition_RS"
     */
    static void cartoon_RS(Bitmap image, Context context, int first_medianfilter_size,int minfilter_size, int final_medianfilter_size,
                          float edge_precision, int outline_size, int color_precision, int shade_precision, int color_shift){

        medianfilter_RS(image,context,first_medianfilter_size);
        bilateralfilter_RS(image,context,1);
        drawOutline_RS(image,context,edge_precision,outline_size);
        minfilter_RS(image,context,minfilter_size);
        Simple.colorPartition_RS(image,context,color_precision,shade_precision,color_shift);
        medianfilter_RS(image,context,final_medianfilter_size);

    }

    /***
     * detect the edges of an image then draw them in black on the image
     * @param image the image to modify
     * @param context context of Activity
     * @param edge_precision belong to [0.0...1.0], increase this value to draw only harder edge, 0.0 draw no edge, 1.0 give black screen ("everything is an edge")
     * @param outline_size Integer which define how the width of the outline will be increased (in pixels)
     */
    static void drawOutline_RS(Bitmap image, Context context, float edge_precision, int outline_size){

        Bitmap copy = image.copy(image.getConfig(),true);

        sobelGradient_RS(copy, context);
        minfilter_RS(copy,context,outline_size);

        RenderScript rs = RenderScript.create(context);

        ScriptC_drawOutline drawOutlineScript = new ScriptC_drawOutline(rs);


        Allocation img = Allocation.createFromBitmap(rs , image);
        Allocation data = Allocation.createSized(rs, Element.U8_4(rs),image.getHeight()*image.getWidth());
        Allocation edges = Allocation.createFromBitmap(rs , copy);

        drawOutlineScript.set_width(image.getWidth());
        drawOutlineScript.set_height(image.getHeight());

        if(edge_precision < 0.0 && edge_precision > 1.0) {
            drawOutlineScript.set_edge_intensity((float)0.5);
        }
        drawOutlineScript.set_edge_intensity(edge_precision);

        drawOutlineScript.invoke_map_img(edges, data);
        drawOutlineScript.bind_data(data);

        //sobelGradientScript.forEach_computemaxGradient(img_alloc_horiz);

        drawOutlineScript.forEach_draw(img,img);

        img.copyTo(image);

        img.destroy();
        data.destroy();
        edges.destroy();

        drawOutlineScript.destroy();
        rs.destroy();

    }

    /***
     * apply a grey effect with some enhanced region of the image
     * @param image the image to modify
     * @param context context of Activity
     * @param blur_intensity param of auxiliary function "blur_moy_RS"
     */
    static void light_grey_RS(Bitmap image, Context context, int blur_intensity){
        Simple.toGreyRS(image,context);

        Bitmap inv_div_copy = image.copy(image.getConfig(),true);

        Advanced.blur_moy_RS(inv_div_copy,context,blur_intensity);

        blendDivide_RS( image,context,inv_div_copy);

    }

    /***
     * apply a pencil effect on image
     * @param image the image to modify
     * @param context context of Activity
     * @param blur_intensity param of auxiliary function "blur_moy_RS"
     */
    static void pencil_RS(Bitmap image, Context context, int blur_intensity){

        Simple.toGreyRS(image,context);

        Bitmap inv_div_copy = image.copy(image.getConfig(),true);

        Simple.negativeRS(inv_div_copy, context);

        Advanced.blur_gaussian_RS(inv_div_copy,context,blur_intensity);

        blendDivide_RS( image,context,inv_div_copy);

    }

    /***
     * auxiliary function used in "dark_aura_RS"
     * @param image the image to modify
     * @param context context of Activity
     * @param blur_intensity param of auxiliary function "blur_moy_RS"
     */
    static void dark_aura_aux_RS(Bitmap image, Context context, int blur_intensity){

        Simple.toGreyRS(image,context);

        Bitmap inv_div_copy = image.copy(image.getConfig(),true);

        Advanced.blur_moy_RS(inv_div_copy,context,blur_intensity);

        Simple.negativeRS(image, context);

        blendDivide_RS( image,context,inv_div_copy);

    }

    /***
     * apply dark aura effect by applying two times dark_aura_aux_RS
     * @param image the image to modify
     * @param context context of Activity
     * @param blur_intensity define the size of the mask to apply which is a part of this effect
     */
    static void dark_aura_RS(Bitmap image, Context context, int blur_intensity){

        dark_aura_aux_RS( image,  context,  blur_intensity);
        dark_aura_aux_RS( image,  context,  blur_intensity);

    }


    /***
     * apply an average blur mask of size (intensity x intensity) to image
     * @param image the image to modify
     * @param context context of Activity
     * @param intensity define the size of the mask to apply (intensity * intensity)
     */
    static void blur_moy_RS(Bitmap image, Context context, int intensity){

        if (intensity< 0){
            intensity = 0;
        }
        float[] mask = new float[(intensity*2+1)*(intensity*2+1)];
        for (int i = 0 ; i < mask.length ; i++){
            mask[i] = 1;
        }
        int norm = mask.length;

        int mask_line_length = intensity*2+1;

        convolution_RS(image, context,  mask,  mask_line_length,  norm);

    }


    /***
     * apply a gaussian blur mask of size 5x5 to image
     * @param image the image to modify
     * @param context context of Activity
     * @param intensity define the size of the mask to apply (intensity * intensity)
     */
    static void blur_gaussian_RS(Bitmap image, Context context, int intensity){

        float[] mask = new float[(2*intensity+1)*(2*intensity+1)];

        float sigma = ((float)intensity)/3;

        float norm = 0.0f;

        for (int i = 0 ; i < 2*intensity+1 ; i++){
            for (int j = 0 ; j < 2*intensity+1 ; j++){
                float new_value = (float)((1/(sigma*Math.sqrt(2*Math.PI)))*Math.pow(Math.E,
                        (-(1/(2*Math.pow(sigma,2.0)))*(Math.sqrt(Math.pow(i-intensity-1, 2.0)+Math.pow(j-intensity-1,2.0))))));

                mask[i*(2*intensity+1) + j] = new_value;
                norm = norm + new_value;
            }
        }

        int mask_line_length = 2*intensity+1;

        convolution_RS(image, context, mask, mask_line_length, norm);

    }

    /***
     * apply a sobel mask to image
     * @param image the image to modify
     * @param context context of Activity
     */
    static void sobel_horizontal_RS(Bitmap image, Context context){

        float[] mask = new float[9];

        mask[0] =  mask[6] = 1;
        mask[3] = 2;
        mask[2] =  mask[8] = -1;
        mask[5] = -2;

        int norm = 1;

        int mask_line_length = 3;

        convolution_RS(image, context, mask, mask_line_length, norm);

    }

    /***
     * apply a sobel mask to image
     * @param image the image to modify
     * @param context context of Activity
     */
    static void sobel_vertical_RS(Bitmap image, Context context){

        float[] mask = new float[9];

        mask[0] = mask[2] = 1;
        mask[1] = 2;
        mask[6] = mask[8] = -1;
        mask[7] = -2;

        int norm = 3;

        int mask_line_length = 3;

        convolution_RS(image, context, mask, mask_line_length, norm);

    }

    /***
     * apply a laplacian mask to image
     * @param image the image to modify
     * @param context context of Activity
     */
    static void laplacian_mask_RS(Bitmap image, Context context){

        float[] mask = new float[9];

        mask[0] = mask[1] = mask[2] = mask[3] = mask[5] = mask[6] = mask[7] = mask[8] = 1;
        mask[4] = -8;

        int norm = 3;

        int mask_line_length = 3;

        convolution_RS(image, context, mask, mask_line_length, norm);
    }


}
