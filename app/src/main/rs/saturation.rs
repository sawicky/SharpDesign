#pragma version(1)
#pragma rs java_package_name(com.mad.sharpdesign)
#pragma rs_fp_relaxed

const static float3 baseSaturate = {0.299f, 0.587f, 0.114f};
float saturationFloat = 0.f;
uchar4 RS_KERNEL saturation(uchar4 in) {
    float4 out = rsUnpackColor8888(in);
    float3 result = dot(out.rgb, baseSaturate);
    result = mix(result, out.rgb, saturationFloat);
    return rsPackColorTo8888(result);
}