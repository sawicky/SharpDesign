#pragma version(1)
#pragma rs java_package_name(com.mad.sharpdesign)
#pragma rs_fp_relaxed

float brightnessValue = 0.f;
//A RS Kernel that applies a simple float input addition to each pixel's colour channel for quick brightening
uchar4 RS_KERNEL brightness(uchar4 in) {
    float4 out = rsUnpackColor8888(in);
    float3 result = out.rgb + brightnessValue;
    result = clamp(result, 0.f, 1.f);
    return rsPackColorTo8888(result);
}