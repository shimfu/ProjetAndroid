#pragma  version (1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.android.rssample)

#include "rs_debug.rsh"

float intensity;

uchar4  RS_KERNEL  luminosity(uchar4  in) {

    float4  pixelf = rsUnpackColor8888(in);

    float rb = pixelf.r;
    float gb = pixelf.g;
    float bb = pixelf.b;

    float cMax = fmax(rb,fmax(gb,bb));
    float cMin = fmin(rb,fmin(gb,bb));
    float delta = cMax - cMin;

    int h;
    if(delta == 0){
        h = 0;
    }else if(cMax == rb){
        h = (int)(60*((gb-bb)/delta)+360)%360;
    }else if(cMax == gb){
        h = (int)(60*((bb-rb)/delta)+120);
    }else if(cMax == bb){
        h = (int)(60*((rb-gb)/delta)+240);
    }

    float s;
    if(cMax == 0){
        s = 0;
    }else{
        s = 1 - cMin/cMax;
    }

    float v = cMax;

    v = v + (2.0*intensity - 1.0);// intensity = 0.5 mean no change, less than it make image darker and more make it lighter

    if(v > 1.0f){
        v = 1.0f;
    }
    if(v < 0.0f){
        v = 0.0f;
    }

    int ti = ((int)(h/60))%6;//getting new rgb using my hue parameter
    float f = (float)(h)/60 - ti;
    float l = v*(1-s);
    float m = v*(1-f*s);
    float n = v*(1-(1-f)*s);

    if(ti == 0){//setting new rgb
        rb = v;
        gb = n;
        bb = l;
    }else if(ti == 1){
        rb = m;
        gb = v;
        bb = l;
    }else if(ti == 2){
        rb = l;
        gb = v;
        bb = n;
    }else if(ti == 3){
        rb = l;
        gb = m;
        bb = v;
    }else if(ti == 4){
        rb = n;
        gb = l;
        bb = v;
    }else if(ti == 5){
        rb = v;
        gb = l;
        bb = m;
    }

    return  rsPackColorTo8888(rb , gb , bb , pixelf.a);
}