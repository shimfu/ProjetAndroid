#pragma  version (1)
#pragma  rs  java_package_name(com.android.rssample)

int32_t width;//width of the Bitmap image
int32_t height;
uchar4 *data;

void map_img(rs_allocation bmp, rs_allocation data) {//this function fill dada with the values of the Bitmap

    for(int i = 0 ; i<width ; i++){//for each collumn
        for(int j = 0 ; j<height ; j++){//for each line
            uchar4 copy = rsGetElementAt_uchar4(bmp, i, j);//we get the Bitmap "bmp" value (Bitmap "bmp" has 2 Dimension)
            rsSetElementAt_uchar4(data, copy, j*width + i);//we copy it in data (data has 1 Dimension)
        }
    }
}

uchar4  RS_KERNEL  outline(uchar4  in, uint32_t x, uint32_t y) {

    float4 pixel_h = rsUnpackColor8888(in);
    float4 pixel_v = rsUnpackColor8888(data[y*width + x]);

    float gradient_r = sqrt(pixel_h.r*pixel_h.r + pixel_v.r*pixel_v.r);//we compute red gradient from 2 enhanced image by sobel mask (horizontal and vertical)
    if(gradient_r > 1.0){
        gradient_r = 1.0;//we don't want value above 1.0
    }
    float gradient_g = sqrt(pixel_h.g*pixel_h.g + pixel_v.r*pixel_v.g);//we compute green gradient from 2 enhanced image by sobel mask (horizontal and vertical)
    if(gradient_g > 1.0){
        gradient_g = 1.0;//we don't want value above 1.0
    }
    float gradient_b = sqrt(pixel_h.b*pixel_h.b + pixel_v.b*pixel_v.b);//we compute blue gradient from 2 enhanced image by sobel mask (horizontal and vertical)
    if(gradient_b > 1.0){
        gradient_b = 1.0;//we don't want value above 1.0
    }
    gradient_r = 1.0 - gradient_r;// we negate image to have black edge on white
    gradient_g = 1.0 - gradient_g;// we negate image to have black edge on white
    gradient_b = 1.0 - gradient_b;// we negate image to have black edge on white

    return rsPackColorTo8888(gradient_r , gradient_g ,gradient_b, pixel_h.a);
}