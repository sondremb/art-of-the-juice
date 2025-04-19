uniform sampler2D u_texture;
uniform vec2 u_direction;
uniform vec2 u_screenSize;

varying vec2 v_texCoord;

void main() {
    vec2 onepixel = vec2(1.0) / u_screenSize;

    float coeffs[17];
    coeffs[0] = 0.038582504;
    coeffs[1] = 0.044826474;
    coeffs[2] = 0.051049657;
    coeffs[3] = 0.056985617;
    coeffs[4] = 0.062352195;
    coeffs[5] = 0.06687324;
    coeffs[6] = 0.070301905;
    coeffs[7] = 0.07244292;
    coeffs[8] = 0.07317098;
    coeffs[9] = 0.07244292;
    coeffs[10] = 0.070301905;
    coeffs[11] = 0.06687324;
    coeffs[12] = 0.062352195;
    coeffs[13] = 0.056985617;
    coeffs[14] = 0.051049657;
    coeffs[15] = 0.044826474;
    coeffs[16] = 0.038582504;

    vec4 sum = vec4(0.0);

    for(int i = 0; i < 17; ++i) {
        vec2 tc = v_texCoord + u_direction * float(i - 8) * onepixel;
        sum += coeffs[i] * texture2D(u_texture, tc);
    }

    gl_FragColor = sum;
}

