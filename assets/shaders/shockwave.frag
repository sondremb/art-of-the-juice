#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform vec2 u_screenSize;

void main() {
    vec2 uv = v_texCoord;

    vec2 center = vec2(0.5, 0.5);
    float radius = 0.5;
    float width = 0.05;
    float feather = 0.135;
    float strength = 0.08;
    float aberration = 0.100;

    vec2 dir = uv - center;
    dir.y *= u_screenSize.y / u_screenSize.x;

    float dist = length(dir);

    float mask = (1.0 - smoothstep(radius - feather, radius, dist)) * smoothstep(radius - width - feather, radius - width, dist);
    vec2 offset = normalize(dir) * strength * mask;
    vec2 biased_st = uv - offset;

    vec2 abber_vec = offset * aberration * mask;

    vec2 final_st = uv * (1.0 - mask) + biased_st * mask;

    vec4 red = texture2D(u_texture, final_st + abber_vec);
    vec4 blue = texture2D(u_texture, final_st - abber_vec);
    vec4 ori = texture2D(u_texture, final_st);
    gl_FragColor = vec4(red.x, ori.y, blue.z, 1.0);
}
