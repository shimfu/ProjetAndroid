#pragma  version (1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.android.rssample)

#include "rs_debug.rsh"

uint32_t mask_line_length;//lenght of the line of the mask
uint32_t mask_collumn_height;//lenght of the collumn of the mask
int32_t *mask;// 1D array wich contain all the values of the mask
uint32_t norm = 1;//the number we use to normalize the mask, it has to be differant of 0 so it is init to 1

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

uchar4 RS_KERNEL compute_data(uchar4 in, uint32_t x, uint32_t y) {//For each pixel at once

    float4 pixeldata;//this memory will store de data's values
    float new_r = 0;//the future red component of this pixel
    float new_g = 0;//the future green component of this pixel
    float new_b = 0;//the future blue component of this pixel
    float new_a = 0;//the future alphan component of this pixel

    int i_bis;
    int j_bis;

    for(int i = x ; i < x+mask_line_length ; i++){//for each collumn of the mask
        for(int j = y ; j < y+mask_collumn_height; j++){//for each line of the mask

            i_bis = i - mask_line_length/2;
            j_bis = j - mask_collumn_height/2;

            if(i_bis >= 0 && j_bis >= 0 && i_bis < width && j_bis < height){//checking if we are in the limits of the image

            }else{
                if(i_bis < 0){
                    i_bis = 0;
                }
                if(i_bis >= width){
                    i_bis = width - 1;
                }
                if(j_bis < 0){
                    j_bis = 0;
                }
                if(j_bis >= height){
                    j_bis = height - 1;
                }
            }

            pixeldata = rsUnpackColor8888(data[(j_bis)*width + i_bis ]);//we get the values of 1 pixel in the mask

            float add_r = pixeldata.r*mask[((mask_collumn_height/2)-y+j_bis)*mask_line_length - x + i_bis + mask_line_length/2]/norm;//computing the next values to add,
            float add_g = pixeldata.g*mask[((mask_collumn_height/2)-y+j_bis)*mask_line_length - x + i_bis + mask_line_length/2]/norm;//we multiply by
            float add_b = pixeldata.b*mask[((mask_collumn_height/2)-y+j_bis)*mask_line_length - x + i_bis + mask_line_length/2]/norm;//the value of the mask
            float add_a = pixeldata.a*mask[((mask_collumn_height/2)-y+j_bis)*mask_line_length - x + i_bis + mask_line_length/2]/norm;//then normalize

            new_r = new_r + add_r;//update of future values, normalized
            new_g = new_g + add_g;
            new_b = new_b + add_b;
            new_a = new_a + add_a;
        }
    }

    if(new_r < 0.0){//we want positive values
        new_r = new_r*(-1.0);
    }
    if(new_g < 0.0){
        new_g = new_g*(-1.0);
    }
    if(new_b < 0.0){
        new_b = new_b*(-1.0);
    }
    if(new_a < 0.0){
        new_a = new_r*(-1.0);
    }
    if(new_r > 1.0){//bounding values to stay between 0.0 and 1.0
        new_r = 1.0;
    }
    if(new_g > 1.0){
        new_g = 1.0;
    }
    if(new_b > 1.0){
        new_b = 1.0;
    }
    if(new_a > 1.0){
        new_a = 1.0;
    }

    return rsPackColorTo8888(new_r,new_g,new_b,new_a);
}
