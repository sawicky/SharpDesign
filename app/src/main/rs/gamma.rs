#pragma version(1)
#pragma rs java_package_name(com.mad.sharpdesign)
#pragma rs_fp_relaxed

float gammaValue = 0.f;
//A custom RS Kernel that is meant to adjust gamma by multiplication. Doesn't work too well
uchar4 RS_KERNEL gamma(uchar4 in) {
    float4 out = rsUnpackColor8888(in);
    float3 result = out.rgb * gammaValue;
    result = clamp(result, 0.f, 1.f);
    return rsPackColorTo8888(result);
}