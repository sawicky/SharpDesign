//Version of renderscript kernel - currently 1
#pragma version(1)
//Point to SharpDesign upon build
#pragma rs java_package_name(com.mad.sharpdesign)
//Floating point precision does not have to conform to IEEE 754-2008
#pragma rs_fp_relaxed

//RS_KERNEL is a built in macro for  __attribute__((kernel)) provided by RenderScript
uchar4 RS_KERNEL invert(uchar4 in) {
    //Create an output of type uchar4, which is a vector used in graphics created by nVIDIA
    uchar4 out = in;
    //Invert the values
    out.r = 255 - in.r;
    out.g = 255 - in.g;
    out.b = 255 - in.b;
    return out;
}