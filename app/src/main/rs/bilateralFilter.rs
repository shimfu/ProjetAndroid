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
    for(int i = 0 ; i<width ; i++){//for each collumn
        for(int j = 0 ; j<height ; j++){//for each line
            uchar4 copy = rsGetElementAt_uchar4(bmp, i, j);//we get the Bitmap "bmp" value (Bitmap "bmp" has 2 Dimension)
            rsSetElementAt_uchar4(data, copy, j*width + i);//we copy it in data (data has 1 Dimension)
        }
    }

}


static float g(int x, int y, int i, int j){//compute spatial coeffcient
    float result;

    float dist = sqrt(pown((float)(x-i),2)+pown((float)(y-j),2));//compute distance

    float e_factor = native_exp(-(1/2)*pown(dist/sigma,2));//compute e-exponent for gaussian

    result = (1/(sigma*sqrt(2*M_PI)))*e_factor;//compute gaussian result

    return result;
}

static float f(float4 in, float4 neighbour){

    float colorsigma = 2.5;//arbitrary-empiric sigma for the gaussian

    float Xin = (0.7976f * in.r + 0.1351f * in.g + 0.0313f * in.b)/(0.7976f+0.1351f+0.0313f);//transformation in XYZ colorspace for center pixel
    float Yin = (0.2880f * in.r + 0.7118f * in.g + 0.00008f * in.b)/(0.2880f+0.7118f+0.00008f);//with précalculated coefficient found on internet
    float Zin = (0.0f * in.r + 0.0f * in.g + 0.8252f * in.b)/(0.8252f);

    float Xnb = (0.7976f * neighbour.r + 0.1351f * neighbour.g + 0.0313f * neighbour.b)/(0.7976f+0.1351f+0.0313f);//transformation in XYZ colorspace of the neighbour pixel
    float Ynb = (0.2880f * neighbour.r + 0.7118f * neighbour.g + 0.00008f * neighbour.b)/(0.2880f+0.7118f+0.00008f);
    float Znb = (0.0f * neighbour.r + 0.0f * neighbour.g + 0.8252f * neighbour.b)/(0.8252f);

    if(Xin >  0.008856){//final part of the algorithm for center pixel
        Xin = powr(Xin,0.33);
    }else{
        Xin = (7.787*Xin) + 16/116;
    }
    if(Yin >  0.008856){
        Yin = powr(Yin,0.33);
    }else{
        Yin = (7.787*Yin) + 16/116;
    }
    if(Zin >  0.008856){
        Zin = powr(Zin,0.33);
    }else{
        Zin = (7.787*Zin) + 16/116;
    }

    if(Xnb >  0.008856){//final part of the algorithm for neighbour pixel
        Xnb = powr(Xnb,0.33);
    }else{
        Xnb = (7.787*Xnb) + 16/116;
    }
    if(Ynb >  0.008856){
            Ynb = powr(Ynb,0.33);
    }else{
        Ynb = (7.787*Ynb) + 16/116;
    }
    if(Znb >  0.008856){
        Znb = powr(Znb,0.33);
    }else{
        Znb = (7.787*Znb) + 16/116;
    }

    float Lin = Yin*116.0 - 16;//new transformation in CIElab color space
    float ain = 500.0*(Xin - Yin);//center pixel
    float bin = 200.0*(Yin - Zin);

    float Lnb = Ynb*116.0 - 16;//new transformation in CIElab color space
    float anb = 500.0*(Xnb - Ynb);//neighbour pixel
    float bnb = 200.0*(Ynb - Znb);

    float dist = sqrt(pown(Lin -Lnb,2)+pown(ain - anb,2)+pown(bin - bnb,2))/3;//compute distance

    float e_factor = powr(M_E,(-1.0/2.0)*pown(dist/colorsigma,2));//compute e-exponent for gaussian

    float factor = (1/(colorsigma*sqrt(2*M_PI)))*e_factor;//apply gaussian
    return factor*(1/intensity + 1);//the color factor is bigger with low intensity to compensate with distance coeffcient


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
                distcoef = g((int)x,(int)y,i,j);
                norm = norm + colorcoef*distcoef;//we compute the norm

                float add_r = pixeldata.r*colorcoef*distcoef;//computing the next values to add,
                float add_g = pixeldata.g*colorcoef*distcoef;//we multiply by
                float add_b = pixeldata.b*colorcoef*distcoef;//the value of the mask

                new_r = new_r + add_r;//update of future values
                new_g = new_g + add_g;
                new_b = new_b + add_b;
            }
        }
    }

    new_r = new_r/norm;//normalization
    new_g = new_g/norm;
    new_b = new_b/norm;

    if(new_r > 1.0){//bounding values to stay between 0.0 and 1.0
        new_r = 1.0;
    }
    if(new_g > 1.0){
        new_g = 1.0;
    }
    if(new_b > 1.0){
        new_b = 1.0;
    }

    return rsPackColorTo8888(new_r,new_g,new_b,pixelin.a);
}
