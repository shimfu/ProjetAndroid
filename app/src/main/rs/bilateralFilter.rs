#pragma  version (1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.android.rssample)

#include "rs_debug.rsh"

uint32_t intensity;//half of the size of the side of the suare mask (mask is simuled with functions "f" & "g")
float sigma;//mask parameter precalculed in java

int32_t width;//width of the Bitmap image
int32_t height;//height of the Bitmap image
uchar4 *data;// 1D array wich contain all the values of each pixel of the Bitmap



void map_img(rs_allocation bmp, rs_allocation data) {//this function fill dada with the values of the Bitmap
    rsDebug("START MAPPING", 0.000000000000000);
    for(int i = 0 ; i<width ; i++){//for each collumn
        for(int j = 0 ; j<height ; j++){//for each line
            uchar4 copy = rsGetElementAt_uchar4(bmp, i, j);//we get the Bitmap "bmp" value (Bitmap "bmp" has 2 Dimension)
            rsSetElementAt_uchar4(data, copy, j*width + i);//we copy it in data (data has 1 Dimension)
        }
    }
    rsDebug("END MAPPING", 0.000000000000000);

}


static float g(int x, int y, int i, int j){//compute spatial coeffcient
    float result;

    float dist = sqrt(pown((float)(x-i),2)+pown((float)(y-j),2));

    float e_factor = native_exp(-(1/2)*pown(dist/sigma,2));

    result = (1/(sigma*sqrt(2*M_PI)))*e_factor;

    return result;
}

static float f(float4 in, float4 neighbour){

    float _3 = 3.0;
    float colorsigma = 2.5;

    float gamma = 1.8;

    float Xin = (0.7976749f * in.r + 0.1351917f * in.g + 0.0313534f * in.b)/(0.7976749f+0.1351917f+0.0313534f);
    float Yin = (0.2880402f * in.r + 0.7118741f * in.g + 0.0000857f * in.b)/(0.2880402f+0.7118741f+0.0000857f);
    float Zin = (0.0f * in.r + 0.0f * in.g + 0.8252100f * in.b)/(0.8252100f);

    float Xnb = (0.7976749f * neighbour.r + 0.1351917f * neighbour.g + 0.0313534f * neighbour.b)/(0.7976749f+0.1351917f+0.0313534f);
    float Ynb = (0.2880402f * neighbour.r + 0.7118741f * neighbour.g + 0.0000857f * neighbour.b)/(0.2880402f+0.7118741f+0.0000857f);
    float Znb = (0.0f * neighbour.r + 0.0f * neighbour.g + 0.8252100f * neighbour.b)/(0.8252100f);

    if(Xin >  0.008856){
        Xin = powr(Xin,0.333);
    }else{
        Xin = (7.787*Xin) + 16/116;
    }
    if(Yin >  0.008856){
        Yin = powr(Yin,0.333);
    }else{
        Yin = (7.787*Yin) + 16/116;
    }
    if(Zin >  0.008856){
        Zin = powr(Zin,0.333);
    }else{
        Zin = (7.787*Zin) + 16/116;
    }

    if(Xnb >  0.008856){
        Xnb = powr(Xnb,0.333);
    }else{
        Xnb = (7.787*Xnb) + 16/116;
    }
    if(Ynb >  0.008856){
            Ynb = powr(Ynb,0.333);
    }else{
        Ynb = (7.787*Ynb) + 16/116;
    }
    if(Znb >  0.008856){
        Znb = powr(Znb,0.333);
    }else{
        Znb = (7.787*Znb) + 16/116;
    }

    float Lin = Yin*116.0 - 16;
    float ain = 500.0*(Xin - Yin);
    float bin = 200.0*(Yin - Zin);

    float Lnb = Ynb*116.0 - 16;
    float anb = 500.0*(Xnb - Ynb);
    float bnb = 200.0*(Ynb - Znb);

    float dist = sqrt(pown(Lin -Lnb,2)+pown(ain - anb,2)+pown(bin - bnb,2))/3;//500;

    float e_factor = powr(M_E,(-1.0/2.0)*pown(dist/colorsigma,2));

    float factor = (1/(colorsigma*sqrt(2*M_PI)))*e_factor;
    return factor*(1/intensity+1);


}

uchar4 RS_KERNEL compute_data(uchar4 in, uint32_t x, uint32_t y) {//For each pixel at once

    float4 pixelin = rsUnpackColor8888(in);

    float4 pixeldata;//this memory will store de data's values
    float new_r = 0;//the future red component of this pixel
    float new_g = 0;//the future green component of this pixel
    float new_b = 0;//the future blue component of this pixel
    float new_a = 1.0;//the future alphan component of this pixel
    float norm = 0;
    float colorcoef;
    float distcoef;

    for(int i = x-intensity ; i < x+intensity+1 ; i++){//for each collumn of the mask
        for(int j = y-intensity ; j < y+intensity+1; j++){//for each line of the mask
            if(i >= 0 && j >= 0 && i < width && j < height){//checking if we are in the limits of the image

                pixeldata = rsUnpackColor8888(data[j*width + i]);//we get the values of 1 pixel in the mask
                colorcoef = f(pixelin,pixeldata);
                //rsDebug("COLORCOEF",colorcoef);
                distcoef = g((int)x,(int)y,i,j);
                //rsDebug("COLORCOEF",colorcoef);

                norm = norm + colorcoef*distcoef;
                float add_r = pixeldata.r*colorcoef*distcoef;//computing the next values to add,
                float add_g = pixeldata.g*colorcoef*distcoef;//we multiply by
                float add_b = pixeldata.b*colorcoef*distcoef;//the value of the mask
                //float add_a = pixeldata.a*mask[((mask_collumn_height/2)-y+j)*mask_line_length - x + i + mask_line_length/2]/norm;//then normalize

                new_r = new_r + add_r;//update of future values, normalized
                new_g = new_g + add_g;
                new_b = new_b + add_b;
                //new_a = new_a + add_a;
            }
        }
    }

    new_r = new_r/norm;//update of future values, normalized
    new_g = new_g/norm;
    new_b = new_b/norm;

    if(new_r < 0.0){//we want positive values
        new_r = new_r*(-1.0);
    }
    if(new_g < 0.0){
        new_g = new_g*(-1.0);
    }
    if(new_b < 0.0){
        new_b = new_b*(-1.0);
    }
    //if(new_a < 0.0){
     //   new_a = new_r*(-1.0);
   // }
    if(new_r > 1.0){//bounding values to stay between 0.0 and 1.0
        new_r = 1.0;
    }
    if(new_g > 1.0){
        new_g = 1.0;
    }
    if(new_b > 1.0){
        new_b = 1.0;
    }
   // if(new_a > 1.0){
   //     new_a = 1.0;
   // }

    return rsPackColorTo8888(new_r,new_g,new_b,pixelin.a);
}
