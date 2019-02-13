package com.example.acoste.projetimage;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by acoste on 01/02/19.
 */

public class Convolution {
    static int norm_for_blurmask(int[][] mask){ //give the number wich will be used to normalize in blur function
        int norm = 0;
        for (int i = 0 ; i < mask.length ; i++){
            for (int j = 0 ; j < mask[i].length ; j++){
                norm = norm + mask[i][j];
            }
        }
        if(norm != 0) {
            return norm;
        }else{
            return 1;
        }
    }

    static public int[] mirrorBitmap(Bitmap bMap, int k){ //return Color data of a bigger Bitmap where each sides are mirrored k time
        return mirrorBitmap(bMap,k ,k);
    }

    static int[] mirrorBitmap(Bitmap bMap, int k_line, int k_collumn){ //return Color data of a bigger Bitmap where border line are mirrored k_line time and border collumn k_collumn
        if (k_line < 0 && k_collumn < 0){
            Log.e("ERREUR", "taille matrice non valide");
        }
        int height = bMap.getHeight();
        int width = bMap.getWidth();

        int[] pixelDataBegin = new int[height*width];
        bMap.getPixels(pixelDataBegin, 0, width, 0, 0, width, height);
        int[] pixelData = new int[(height + 2 * k_line) * (width + 2 * k_collumn)];//Color data of the mirrored Bitmap, not init yet

        for (int i = 0 ; i < k_line ; i++){//filling the 4 border angles
            for (int j = 0 ; j < k_collumn ; j++){
                pixelData[i * (width + 2 * k_collumn) + j] = pixelDataBegin[0];//left up angle
                pixelData[(height + k_line + i) * (width + 2 * k_collumn) + j]= pixelDataBegin[(height - 1) * width];//bottom left angle
                pixelData[width + k_collumn + i * (width + 2 * k_collumn) + j] = pixelDataBegin[width - 1];//top right angle
                pixelData[(height + k_line + i) * (width + 2 * k_collumn) + width + k_collumn + j] = pixelDataBegin[width * height - 1];//bottom right angle
            }
        }

        for (int i = 0 ; i < width ; i++){// filling border line
            for (int j = 0 ; j < k_line ; j++){
                pixelData[k_collumn + i + j * (width + 2 * k_collumn)] = pixelDataBegin[i];
                pixelData[k_collumn + i + j * (width + 2 * k_collumn) + (height + k_line) * (width + 2 * k_collumn)] = pixelDataBegin[(height - 1) * width + i];//On peu factoriser mais moins compréhensible (k+i+(j+height+k)*(width+2*k))
            }
        }

        for (int i = 0 ; i < height ; i++){// filling border collumn
            for(int j = 0 ; j < k_collumn ; j++){
                pixelData[(k_line + i) * (width + 2 * k_collumn) + j] = pixelDataBegin[i * width];
                pixelData[(k_line + i) * (width + 2 * k_collumn) + k_collumn + width + j] = pixelDataBegin[width - 1 + i * width];
            }
        }

        for (int i = 0 ; i < height ; i++){//filling the center part
            for (int j = 0 ; j < width ; j++){
                pixelData[(k_line + i) * (width + 2 * k_collumn) + j + k_collumn] = pixelDataBegin[i * width + j];
            }
        }

        return pixelData;
    }

    static int[][] convolutionOnMirror(int[] colorData, int half_size, int width, int height, int[][] masque){//convolution which wan't calculate border value ( k pixel from the border ) -(ARGB color space)

        int[][] returnedData = new int[(width - 2 * half_size) * (height - 2 * half_size)][4];
        int red_sum;
        int green_sum;
        int blue_sum;

        for (int i = half_size ; i < height - half_size ; i++){//for each line of the image
            for (int j = half_size ; j < width - half_size ; j++){//for each collumn of the image
                red_sum = 0;
                green_sum = 0;
                blue_sum = 0;
                for(int l = - half_size ; l < half_size + 1 ; l++ ){   //for each line of the mask
                    for (int n = - half_size ; n < half_size + 1 ; n++){   //for each collumn of the mask
                        red_sum = red_sum + Color.red(colorData[((i + l) * width) + j + n]) * masque[l + half_size][n + half_size];
                        green_sum = green_sum + Color.green(colorData[((i + l) * width) + j + n]) * masque[l + half_size][n + half_size];
                        blue_sum = blue_sum + Color.blue(colorData[((i + l) * width) + j + n]) * masque[l + half_size][n + half_size];
                    }
                }
                returnedData[(i - half_size) * (width - 2 * half_size) + (j - half_size)][0] = Color.alpha(colorData[(i - half_size) * (width - 2 * half_size) + (j - half_size)]);
                returnedData[(i - half_size) * (width - 2 * half_size) + (j - half_size)][1] = red_sum;
                returnedData[(i - half_size) * (width - 2 * half_size) + (j - half_size)][2] = green_sum;
                returnedData[(i - half_size) * (width - 2 * half_size) + (j - half_size)][3] = blue_sum;
            }
        }

        return returnedData;
    }

    static int[][] convolution(int[] colorData, int width, int height, int[][] masque){//convolution which set the value of the mask at 0 if out of range of image -(ARGB color space)

        int[][] returnedData = new int[(width) * (height)][4];
        int red_sum;
        int green_sum;
        int blue_sum;

        for (int i = 0 ; i < height  ; i++){//for each line of the image
            for (int j = 0 ; j < width ; j++){//for each collumn of the image
                red_sum = 0;
                green_sum = 0;
                blue_sum = 0;
                for(int l = - masque.length / 2 ; l < masque.length / 2 + 1 ; l++ ){   ///for each line of the mask

                    for (int n = - masque[l + masque.length / 2].length / 2 ; n < masque[l + masque.length / 2].length / 2 + 1 ; n++){   //for each collumn of the mask
                        if( (j + n) >= 0 && (j + n) < width && (i + l) >=0 && (i + l) < height){
                            red_sum = red_sum + Color.red(colorData[((i + l) * width) + j + n]) * masque[l + masque.length / 2][n + masque[l + masque.length / 2].length / 2];
                            green_sum = green_sum + Color.green(colorData[((i + l) * width) + j + n]) * masque[l + masque.length / 2][n + masque[l + masque.length / 2].length / 2];
                            blue_sum = blue_sum + Color.blue(colorData[((i + l) * width) + j + n]) * masque[l + masque.length / 2][n + masque[l + masque.length / 2].length / 2];
                        }
                    }
                }
                returnedData[i * width + j][0] = Color.alpha(colorData[i * width + j]);
                returnedData[i * width + j][1] = red_sum;
                returnedData[i * width + j][2] = green_sum;
                returnedData[i * width + j][3] = blue_sum;

            }
        }

        return returnedData;
    }

    static int[][] outline_oriented(int[] pixelData, int width, int height, int[][] mask){// return the oriented gradient data for fixed mask

        int[][] pixelDataARGB = convolution(pixelData, width, height, mask);
        int[][] returnedData = new int[pixelData.length][2];
        int grey;
        for (int i = 0 ; i < pixelData.length ; i++){
            grey = (int)(Math.abs(pixelDataARGB[i][1]) * 0.30 + Math.abs(pixelDataARGB[i][2]) * 0.59 + Math.abs(pixelDataARGB[i][3]) * 0.11);

            returnedData[i][1] = grey;
            returnedData[i][0] = Color.alpha(pixelData[i]);
        }
        return returnedData;
    }

    static int[] gradient(int[][] horizontalPixelData, int[][] verticalPixelData){//return the gradient module of two gradient data, the returned values are normalized
        int[] returnedData = new int[horizontalPixelData.length];
        if (horizontalPixelData.length != verticalPixelData.length){
            Log.e("ERROR", "in gradient function");
            return returnedData;
        }
        int max = 0;
        for (int i = 0 ; i < horizontalPixelData.length ; i++){
            returnedData[i] = (int)Math.sqrt(horizontalPixelData[i][1] * horizontalPixelData[i][1] + verticalPixelData[i][1] * verticalPixelData[i][1]);
            if(max < returnedData[i]){
                max = returnedData[i];
            }
        }
        int normalized_value = 0;
        for (int i = 0 ; i < horizontalPixelData.length ; i++){
            normalized_value = (returnedData[i] * 255) / max;
            returnedData[i] = Color.argb(255, normalized_value, normalized_value, normalized_value);
        }
        return returnedData;
    }

    static int[][] mask_sobel_vertical(){//create a fixed mask wich are used for vertical gradient in convolution
        int[][] mask = new int[3][3];
        for(int i = 0 ; i < 3 ; i++){
            mask[i][1] = 0;
        }
        mask[0][0] = - 1;
        mask[1][0] = - 2;
        mask[2][0] = - 1;
        mask[0][2] = 1;
        mask[1][2] = 2;
        mask[2][2] = 1;

        return mask;
    }

    static int[][] mask_sobel_horizontal(){//create a fixed mask wich are used for vertical gradient
        int[][] mask = new int[3][3];
        for(int i = 0 ; i < 3 ; i++){
            mask[1][i] = 0;
        }
        mask[0][0] = - 1;
        mask[0][1] = - 2;
        mask[0][2] = - 1;
        mask[2][0] = 1;
        mask[2][1] = 2;
        mask[2][2] = 1;

        return mask;
    }
}