package com.example.acoste.projetimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.util.Log;

import com.android.rssample.ScriptC_toGrey;
import com.android.rssample.ScriptC_randomHue;
import com.android.rssample.ScriptC_keepColor;

import java.util.Random;

/**
 * Created by acoste on 01/02/19.
 */

public class Simple extends Effects {

    public Simple(Bitmap bMap){
        super(bMap);
    }

    public void grey(){//transform a Bitmap in shades of grey (ARGB color space)
        int[] pixelData = new int[getCurrentImg().getWidth() * getCurrentImg().getHeight()];

        getCurrentImg().getPixels(pixelData, 0, getCurrentImg().getWidth(), 0, 0, getCurrentImg().getWidth(), getCurrentImg().getHeight());
        for (int i = 0; i < pixelData.length; i++) {
            int grey;
            grey = (int) (0.30 * Color.red(pixelData[i]) + 0.59 * Color.green(pixelData[i]) + 0.11 * Color.blue(pixelData[i]));
            pixelData[i] = Color.argb(Color.alpha(pixelData[i]), grey, grey, grey);
        }
        getCurrentImg().setPixels(pixelData, 0, getCurrentImg().getWidth(), 0, 0, getCurrentImg().getWidth(), getCurrentImg().getHeight());

    }

    public int[] keepColor_aux ( int[] color, int hue){//color in grey all the color which are 40° or more away from the hue parameter
        if (hue < 0 && hue > 360) {//wrong parameter hue
            return color;
        }
        int hue2 = hue + 360;//hue2 will be used for reds, 5° very different of 355° but in fact there is only 10° between them

        float[] HSV = new float[3];
        int grey;
        for (int i = 0; i < color.length; i++) {
            Color.colorToHSV(color[i], HSV);

            if (Math.abs(HSV[0] - hue) > 40 && Math.abs(HSV[0] - hue2) > 40) {//this is why i add 360 to each hue which are to small
                grey = (int) (0.30 * Color.red(color[i]) + 0.59 * Color.green(color[i]) + 0.11 * Color.blue(color[i]));
                color[i] = Color.argb(Color.alpha(color[i]), grey, grey, grey);
            }
        }
        return color;
    }

    public Bitmap keepColor(Bitmap bMap, int hue) {
        int[] pixelData = new int[bMap.getWidth() * bMap.getHeight()];
        Bitmap cpy_bMap = bMap.copy(bMap.getConfig(), true);
        cpy_bMap.getPixels(pixelData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        pixelData = keepColor_aux(pixelData, hue);
        cpy_bMap.setPixels(pixelData, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
        return cpy_bMap;
    }

    public int[] randomHue_aux(int[] color){//change the Hue of all pixel of an array of Color type (HSV color space)

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
        pixelData = randomHue_aux(pixelData);
        bMap.setPixels(pixelData,0,bMap.getWidth(),0,0,bMap.getWidth(),bMap.getHeight());

    }

    private  void  toGreyRS(Bitmap  bmp, Context context) {
        //1)  Creer un  contexte  RenderScript
        RenderScript rs = RenderScript.create(context);
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

    void keepColorRS(Bitmap image, int color, Context context) {

        //Create renderscript
        RenderScript rs = RenderScript.create(context);
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

    private void randomHueRS(Bitmap bmp, Context context){

        //Get image size
        Random random = new Random();
        int rdNumber = random.nextInt(361);
        Log.e("rdNumber", Integer.toString(rdNumber));
        //Create renderscript
        RenderScript rs = RenderScript.create(context);
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

}
