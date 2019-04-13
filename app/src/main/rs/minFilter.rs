#pragma  version (1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.android.rssample)

#include "rs_debug.rsh"

uint32_t intensity;//half of the size of the side of the suare mask (mask is simuled with functions "f" & "g")

int32_t width;//width of the Bitmap image
int32_t height;//height of the Bitmap image
uchar4 *data;// 1D array wich contain all the values of each pixel of the Bitmap



void map_img(rs_allocation bmp, rs_allocation data) {//this function fill dada with the values of the Bitmap
    for(int i = 0 ; i<width ; i++){//for each collumn
        for(int j = 0 ; j<height ; j++){//for each line
            uchar4 copy = rsGetElementAt_uchar4(bmp, i, j);//we get the Bitmap "bmp" value (Bitmap "bmp" has 2 Dimension)
            rsSetElementAt_uchar4(data, copy, j*width + i);//we copy it in data (data has 1 Dimension)
        }
    }
}

static float4 min4(float4 in, float4 neighbour){
    float greyin = 0.30*in.r + 0.59*in.g + 0.11*in.b;
    float greynb = 0.30*neighbour.r + 0.59*neighbour.g + 0.11*neighbour.b;

    if(fmin(greyin,greynb)==greyin){
        return in;
    }else{
        return neighbour;
    }
}

uchar4 RS_KERNEL compute_data(uchar4 in, uint32_t x, uint32_t y) {//For each pixel at once

    float4 pixelin = rsUnpackColor8888(in);

    float4 pixeldata;//this memory will store de data's values
    float4 new_pixel= pixelin;//the future red component of this pixel

    for(int i = x-intensity ; i < x+intensity+1 ; i++){//for each collumn of the mask
        for(int j = y-intensity ; j < y+intensity+1; j++){//for each line of the mask
            if(i >= 0 && j >= 0 && i < width && j < height){//checking if we are in the limits of the image

                pixeldata = rsUnpackColor8888(data[j*width + i]);//we get the values of 1 pixel in the mask
                new_pixel = min4(new_pixel,pixeldata);
            }
        }
    }
    return rsPackColorTo8888(new_pixel.r,new_pixel.g,new_pixel.b,pixelin.a);
}
