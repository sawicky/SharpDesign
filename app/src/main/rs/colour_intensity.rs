#pragma version(1)
#pragma rs java_package_name(com.mad.sharpdesign)
#pragma rs_fp_relaxed

float r,g,b;
//A RS Kernel that applies input RGB values to each RGB value of the pixel
uchar4 RS_KERNEL colour_intensity(uchar4 in) {
    float4 out = rsUnpackColor8888(in);
    float3 inputValues = {r,g,b};
    float3 result = inputValues + out.rgb;
    result = clamp(result, 0.f, 1.f);
    return rsPackColorTo8888(result);
}