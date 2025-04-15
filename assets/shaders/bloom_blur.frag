uniform sampler2D u_texture;
uniform vec2 u_direction;
uniform vec2 u_screenSize;

varying vec2 v_texCoord;

void main() {
    vec2 onepixel = vec2(1.0) / u_screenSize;
 /*    float coeffs[5];
    coeffs[0] = 0.19601233132168375;
    coeffs[1] = 0.20198179552122797;
    coeffs[2] = 0.20401174631417657;
    coeffs[3] = 0.20198179552122797;
    coeffs[4] = 0.19601233132168375; */

    float coeffs[17];
    coeffs[0] = 0.032748231400188836;
    coeffs[1] = 0.038816637550805756;
    coeffs[2] = 0.04509849869409865;
    coeffs[3] = 0.05135945035816793;
    coeffs[4] = 0.05733142814564388;
    coeffs[5] = 0.06273057432505663;
    coeffs[6] = 0.06727905417839894;
    coeffs[7] = 0.07072852504926737;
    coeffs[8] = 0.07288252931686245;
    coeffs[9] = 0.07361501091401369;
    coeffs[10] = 0.07288252931686245;
    coeffs[11] = 0.07072852504926737;
    coeffs[12] = 0.06727905417839894;
    coeffs[13] = 0.06273057432505663;
    coeffs[14] = 0.05733142814564388;
    coeffs[15] = 0.05135945035816793;
    coeffs[16] = 0.04509849869409865;
    vec4 sum = vec4(0.0);

    for(int i = 0; i < 17; ++i) {
        vec2 tc = v_texCoord + u_direction * float(i - 8) * onepixel;
        sum += coeffs[i] * texture2D(u_texture, tc);
    }

    gl_FragColor = sum;
}