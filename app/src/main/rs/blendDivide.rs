#pragma  version (1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.android.rssample)

#include "rs_debug.rsh"

int width;//width of the Bitmap image
int height;//height of the Bitmap image
uchar4 *data;// 1D array wich contain all the values of each pixel of the Bitmap

void map_img(rs_allocation bmp, rs_allocation data) {//this function fill dada with the values of the Bitmap
    for(int i = 0 ; i<width ; i++){//for each collumn
        for(int j = 0 ; j<height ; j++){//for each line
            uchar4 copy = rsGetElementAt_uchar4(bmp, i, j);//we get the Bitmap "bmp" value (Bitmap "bmp" has 2 Dimension)
            rsSetElementAt_uchar4(data, copy, j*width + i);//we copy it in data (data has 1 Dimension)
        }
    }
}

uchar4 RS_KERNEL compute_data(uchar4 in, uint32_t x, uint32_t y) {//This is working for grey image mostly

    float4 pixelDivider;//this memory will store de data's values
    float4 pixelin;

    pixelin = rsUnpackColor8888(in);
    pixelDivider = rsUnpackColor8888(data[y*width + x]);//we get the values of corresponding divider pixel

    if(pixelDivider.r > 0.0f){
        pixelin.r = pixelin.r/(1.0 - pixelDivider.r);//computing the next red value
    }else{
        pixelin.r = 1.0f;
    }
    if(pixelDivider.g > 0.0f){
        pixelin.g = pixelin.g/(1.0 - pixelDivider.g);//computing the next  green value
    }else{
        pixelin.g = 1.0f;
    }
    if(pixelDivider.b > 0.0f){
        pixelin.b = pixelin.b/(1.0 - pixelDivider.b);//computing the next  blue value
    }else{
        pixelin.b = 1.0f;
    }

    if(pixelin.r < 0.0){//we want positive values
        pixelin.r = pixelin.r*(-1.0);
    }
    if(pixelin.g < 0.0){
        pixelin.g = pixelin.g*(-1.0);
    }
    if(pixelin.b < 0.0){
        pixelin.b = pixelin.b*(-1.0);
    }

    if(pixelin.r > 1.0){//bounding values to stay between 0.0 and 1.0
        pixelin.r = 1.0;
    }
    if(pixelin.g > 1.0){
        pixelin.g = 1.0;
    }
    if(pixelin.b > 1.0){
        pixelin.b = 1.0;
    }

    return rsPackColorTo8888(pixelin.r, pixelin.g, pixelin.b, pixelin.a);
}
