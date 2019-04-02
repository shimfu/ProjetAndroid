#pragma  version (1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.android.rssample)

int color_family_size;
int color_shift = 0;
float luminance_family_size;

uchar4  RS_KERNEL  colorPartition(uchar4  in) {
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

    float h_float = (float)(h)/360;
    float color_family_size_f = (float)(color_family_size)/360;
    float color_shift_f = (float)(color_shift)/360;

    h = h + color_family_size/2 - color_shift;
    h = h % 360;
    h = (h/color_family_size )*color_family_size + color_shift;
    h = h % 360;

    //if(v > 0.05 && v < 0.95){
        //v = v - 0.05;
        //v = (v/luminance_family_size)*(luminance_family_size) + luminance_family_size/2 + 0.05;
    //}

    if(s < 0.20){
        s = 0.0;
    }else if(s < 0.60){
        s = 0.50;
    }else{
        s = 1.0;
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
