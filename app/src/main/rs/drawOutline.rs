#pragma  version (1)
#pragma  rs  java_package_name(com.android.rssample)

int32_t width;//width of the Bitmap image
int32_t height;
float edge_intensity;
uchar4 *data;

void map_img(rs_allocation bmp, rs_allocation data) {//this function fill dada with the values of the Bitmap

    for(int i = 0 ; i<width ; i++){//for each collumn
        for(int j = 0 ; j<height ; j++){//for each line
            uchar4 copy = rsGetElementAt_uchar4(bmp, i, j);//we get the Bitmap "bmp" value (Bitmap "bmp" has 2 Dimension)
            rsSetElementAt_uchar4(data, copy, j*width + i);//we copy it in data (data has 1 Dimension)
        }
    }
}

uchar4  RS_KERNEL draw(uchar4  in, uint32_t x, uint32_t y) {

    float4 pixelin = rsUnpackColor8888(in);
    float4 pixel_edge = rsUnpackColor8888(data[y*width + x]);

    if(pixel_edge.r > edge_intensity){
        return rsPackColorTo8888(pixelin.r , pixelin.g ,pixelin.b, pixelin.a);
    }else{
        return rsPackColorTo8888( 0.0 , 0.0 ,0.0, pixelin.a);
    }


}