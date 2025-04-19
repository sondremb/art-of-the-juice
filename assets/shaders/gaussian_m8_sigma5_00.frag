uniform sampler2D u_texture;
uniform vec2 u_direction;
uniform vec2 u_screenSize;

varying vec2 v_texCoord;

void main() {
    vec2 onepixel = vec2(1.0) / u_screenSize;

    float coeffs[17];
    coeffs[0] = 0.008863449;
    coeffs[1] = 0.016150255;
    coeffs[2] = 0.027165176;
    coeffs[3] = 0.042179566;
    coeffs[4] = 0.060457215;
    coeffs[5] = 0.07999274;
    coeffs[6] = 0.09770335;
    coeffs[7] = 0.110160224;
    coeffs[8] = 0.11465595;
    coeffs[9] = 0.110160224;
    coeffs[10] = 0.09770335;
    coeffs[11] = 0.07999274;
    coeffs[12] = 0.060457215;
    coeffs[13] = 0.042179566;
    coeffs[14] = 0.027165176;
    coeffs[15] = 0.016150255;
    coeffs[16] = 0.008863449;

    vec4 sum = vec4(0.0);

    for(int i = 0; i < 17; ++i) {
        vec2 tc = v_texCoord + u_direction * float(i - 8) * onepixel;
        sum += coeffs[i] * texture2D(u_texture, tc);
    }

    gl_FragColor = sum;
}

