#pragma  version (1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.android.rssample)

int hue = 0;
uchar4  RS_KERNEL  randomHue(uchar4  in) {
    float4  pixelf = rsUnpackColor8888(in);


    ///HSV CONVERSION///
    float rb = pixelf.r;//getting rgb values
    float gb = pixelf.g;
    float bb = pixelf.b;

    float cMax = fmax(rb,fmax(gb,bb));//getting max and min of rgb
    float cMin = fmin(rb,fmin(gb,bb));
    float delta = cMax - cMin;

    float s;//getting saturation
    if(cMax == 0){
        s = 0;
    }else{
        s = 1 - cMin/cMax;
    }

    float v = cMax;//getting value

    int ti = ((int)(hue/60))%6;//getting new rgb using my hue parameter
    float f = (float)(hue)/60 - ti;
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