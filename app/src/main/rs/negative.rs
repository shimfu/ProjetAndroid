#pragma  version (1)
#pragma  rs  java_package_name(com.android.rssample)
uchar4  RS_KERNEL  toGrey(uchar4  in) {
    float4  pixelf = rsUnpackColor8888(in);
    float n_r = 1.0 - pixelf.r;//negation of rgb values
    float n_g = 1.0 - pixelf.g;
    float n_b = 1.0 - pixelf.b;
    return  rsPackColorTo8888(n_r , n_g , n_b , pixelf.a);
}