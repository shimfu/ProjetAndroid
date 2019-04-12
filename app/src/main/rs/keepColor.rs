#pragma  version (1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.android.rssample)

int hue;//the hue we want to keep

uchar4  RS_KERNEL  keepColor(uchar4  in) {
    float4  pixelf = rsUnpackColor8888(in);

    float rb = pixelf.r;//we get pixel components
    float gb = pixelf.g;
    float bb = pixelf.b;

    float cMax = fmax(rb,fmax(gb,bb));//Start of RGB to HSV transformation
    float cMin = fmin(rb,fmin(gb,bb));
    float delta = cMax - cMin;

    int h;//the hue of the pixel we are looking
    if(delta == 0){
        h = 0;
    }else if(cMax == rb){
        h = (int)(60*((gb-bb)/delta)+360)%360;
    }else if(cMax == gb){
        h = (int)(60*((bb-rb)/delta)+120);
    }else if(cMax == bb){
        h = (int)(60*((rb-gb)/delta)+240);
    }//End of RGB to HSV transformation

    if(hue <= 40){//we choose an interval size of 40°
        hue = hue + 360;//we want small value to be close ro 360° so 5° become 365° and is close to 359° like HSV color space work
    }
    int h2 = h;//a copy of the pixel hue
    if(h <= 40){//h2 will be usefull for small hue like 1° when the only hue keeped is like 359°
        h2 = h + 360;
    }
    if(abs(hue - h) <= 40 && abs(hue - h2) <= 40){//we keep values around hue
        return  rsPackColorTo8888(pixelf.r , pixelf.g , pixelf.b , pixelf.a);
    }else{
        float  grey = (0.30* pixelf.r + 0.59* pixelf.g + 0.11* pixelf.b);//other values are grey
        return  rsPackColorTo8888(grey , grey , grey , pixelf.a);
    }
}
