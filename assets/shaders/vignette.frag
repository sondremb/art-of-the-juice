#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform vec2 u_screenSize;
//uniform float u_strength; // how strong the vignette is (0.0 - 1.0)

void main() {
    vec2 uv = v_texCoord;
    vec4 color = texture2D(u_texture, uv);

    // Calculate distance from center
    vec2 center = vec2(0.5, 0.5);
    float dist = distance(uv, center);

    // Smooth vignette gradient
    float vignette = smoothstep(0.8, 0.5, dist); // inner starts at 0.5, full fade at 0.8
    vignette = mix(1.0, vignette, 1.0);   // blend in based on strength

    gl_FragColor = color * vignette;
    gl_FragColor.a = 1.0;
}