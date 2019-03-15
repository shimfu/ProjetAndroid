#pragma  version (1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.android.rssample)

#include "rs_debug.rsh"

int32_t histo[1000];
int32_t lut[1000];
int32_t maxi = 0;
int32_t mini = 1000;
int size;



uchar4  RS_KERNEL create_histog(uchar4 in, uint32_t x, uint32_t y) {
    float4  pixelf = rsUnpackColor8888(in);

///HSV CONVERSION///
    float rb = pixelf.r;//getting rgb values
    float gb = pixelf.g;
    float bb = pixelf.b;

    float cMax = fmax(rb,fmax(gb,bb));//getting max and min of rgb
    float cMin = fmin(rb,fmin(gb,bb));
    float delta = cMax - cMin;

    int h;//getting hue
        if(delta == 0){
            h = 0;
        }else if(cMax == rb){
            h = (int)(60*((gb-bb)/delta)+360)%360;
        }else if(cMax == gb){
            h = (int)(60*((bb-rb)/delta)+120);
        }else if(cMax == bb){
            h = (int)(60*((rb-gb)/delta)+240);
        }

    float s;//getting saturation
    if(cMax == 0){
        s = 0;
    }else{
        s = 1 - cMin/cMax;
    }

    float v = cMax;//getting value

    int32_t value = (int32_t)(v*1000);
    if(value < mini){
        mini = value;

    }
    if(value > maxi){
        maxi = value;

    }

    rsAtomicInc(&histo[value]);

    float hue = (float)(h)/360;

    return rsPackColorTo8888(hue, s, v, pixelf.a);

}

uchar4  RS_KERNEL  apply_contrast(uchar4  in) {
     float4  pixelf = rsUnpackColor8888(in);

     float h = pixelf.r * 360;//getting rgb values
     float s = pixelf.g;
     float v = pixelf.b;

     float value = (float)(lut[(int)(v*1000)])/1000;

     float r ; float g ; float b;

     int ti = ((int)(h/60))%6;//getting new rgb using my hue parameter
     float f = (float)(h/60 - ti);
     float l = value*(1-s);
     float m = value*(1-f*s);
     float n = value*(1-(1-f)*s);

     if(ti == 0){//setting new rgb
         r = value;
         g = n;
         b = l;
     }else if(ti == 1){
         r = m;
         g = value;
         b = l;
     }else if(ti == 2){
         r = l;
         g = value;
         b = n;
     }else if(ti == 3){
         r = l;
         g = m;
         b = value;
     }else if(ti == 4){
         r = n;
         g = l;
         b = value;
     }else if(ti == 5){
         r = value;
         g = l;
         b = m;
     }

     r = r;
     g = g;
     b = b;

     return rsPackColorTo8888(r, g, b, pixelf.a);
}

void createLut(){
    for (int i = 0; i < 1000; i++) {
        lut[i] =(int)((999)*(i - mini) / (maxi - mini));
    }
}


