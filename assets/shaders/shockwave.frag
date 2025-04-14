#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform vec2 u_screenSize;

void main() {
/*     vec2 explosionCenter = vec2(0.5, 0.5);
    vec2 dir = v_texCoord - explosionCenter;
    vec2 finalUV = v_texCoord;
    float dist = length(dir);
    float time = 1.0;

    float shock = sin(dist * 20.0 - time * 40.0) * 0.02;
    shock *= smoothstep(1.0, 0.0, dist / (0.5 * time + 0.2)); // Fade out
    finalUV += normalize(dir) * shock;
    gl_FragColor = texture2D(u_texture, finalUV); */

    vec2 center = vec2(0, 0);
    float radius = 1.0;
    float width = 0.05;
    float feather = 0.135;
    float strength = 0.08;
    float aberration = 0.100;

    vec2 uv = v_texCoord;
    //float aspect_ratio = u_screenSize.y / u_screenSize.x;
    //vec2 scaled_st = (st - vec2(0.0, 0.5)) / vec2(1.0, aspect_ratio) + vec2(0, 0.5);
    vec2 scaled_st = uv;
    vec2 dist_center = scaled_st - center;

    float mask = (1.0 - smoothstep(radius - feather, radius, length(dist_center))) * smoothstep(radius - width - feather, radius - width, length(dist_center));
    vec2 offset = normalize(dist_center) * strength * mask;
    vec2 biased_st = scaled_st - offset;

    vec2 abber_vec = offset * aberration * mask;

    vec2 final_st = uv * (1.0 - mask) + biased_st * mask;

    vec4 red = texture2D(u_texture, final_st + abber_vec);
    vec4 blue = texture2D(u_texture, final_st - abber_vec);
    vec4 ori = texture2D(u_texture, final_st);
    gl_FragColor = vec4(red.x, ori.y, blue.z, 1.0);
    //gl_FragColor = vec4(st.x, st.y, 1.0, 1.0);
}
