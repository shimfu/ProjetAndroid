#pragma  version (1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.android.rssample)

#include "rs_debug.rsh"

uint32_t pixel_size;//lenght of the line of the mask

int32_t width;//width of the Bitmap image
int32_t height;//height of the Bitmap image
float4 *lut;// 1D array wich contain all the values of each pixel of the Bitmap

void map_lut(rs_allocation bmp, rs_allocation lut) {//this function compute the LUT with the Bitmap

    uchar4 data_in;//the pixel as uchar4 we are working on
    float4 data;//the same pixel as float4 we will use to compute the allocation "lut"

    for(int i = 0 ; i<width/pixel_size ; i++){//for each collumn
        for(int j = 0 ; j<height/pixel_size ; j++){//for each line
            float4 zero = {0.0f,0.0f,0.0f,0.0f};
            rsSetElementAt_float4(lut, zero, i*(height/pixel_size) + j);//we init each value at 0.0
        }
    }

    for(int i = 0 ; i<width ; i++){//for each collumn of the image
        for(int j = 0 ; j<height ; j++){//for each line of the image

            int position_x = i/pixel_size;//the virtual x position of the pixel we are looking in pixeled image
            int position_y = j/pixel_size;//the virtual y position of the pixel we are looking in pixeled image
            data_in = rsGetElementAt_uchar4(bmp, i, j);//we get the Bitmap "bmp" value (Bitmap "bmp" has 2 Dimension)
            data = rsUnpackColor8888(data_in);
            rsSetElementAt_float4(lut, data/(pixel_size*pixel_size) + rsGetElementAt_float4(lut, position_y*(width/pixel_size) +position_x), position_y*(width/pixel_size) +position_x);
        }
    }
}

uchar4 RS_KERNEL compute_data(uchar4 in, uint32_t x, uint32_t y) {//For each pixel

    float4 pixelin = rsUnpackColor8888(in);

    uint32_t x_pixel = x/pixel_size;//calcul of the new coordanate is the virtualy pixeled image
    uint32_t y_pixel = y/pixel_size;
    uint32_t position = y_pixel*(width/pixel_size) + x_pixel;//position of the pixel in the 1D LUT

    uchar4 new_pixel = rsPackColorTo8888(lut[position]);//pixel is set with LUT

    return new_pixel;

}
