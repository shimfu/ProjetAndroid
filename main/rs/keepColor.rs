#pragma  version (1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.android.rssample)

int hue;

uchar4  RS_KERNEL  keepColor(uchar4  in) {
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

    int s;
    if(cMax == 0){
        s = 0;
    }else{
        s = 1 - cMin/cMax;
    }

    int v = cMax;

    if(hue <= 40){
        hue = hue + 360;
    }
    int h2 = h;
    if(h <= 40){
        h2 = h + 360;
    }
    if(abs(hue - h) <= 40 && abs(hue - h2) <= 40){
        return  rsPackColorTo8888(pixelf.r , pixelf.g , pixelf.b , pixelf.a);
    }else{
        float  grey = (0.30* pixelf.r + 0.59* pixelf.g + 0.11* pixelf.b);
        return  rsPackColorTo8888(grey , grey , grey , pixelf.a);
    }
}
