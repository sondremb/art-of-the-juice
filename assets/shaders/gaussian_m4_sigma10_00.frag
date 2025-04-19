uniform sampler2D u_texture;
uniform vec2 u_direction;
uniform vec2 u_screenSize;

varying vec2 v_texCoord;

void main() {
    vec2 onepixel = vec2(1.0) / u_screenSize;

    float coeffs[9];
    coeffs[0] = 0.101038784;
    coeffs[1] = 0.108364925;
    coeffs[2] = 0.11392091;
    coeffs[3] = 0.11739033;
    coeffs[4] = 0.11857011;
    coeffs[5] = 0.11739033;
    coeffs[6] = 0.11392091;
    coeffs[7] = 0.108364925;
    coeffs[8] = 0.101038784;

    vec4 sum = vec4(0.0);

    for(int i = 0; i < 9; ++i) {
        vec2 tc = v_texCoord + u_direction * float(i - 4) * onepixel;
        sum += coeffs[i] * texture2D(u_texture, tc);
    }

    gl_FragColor = sum;
}

