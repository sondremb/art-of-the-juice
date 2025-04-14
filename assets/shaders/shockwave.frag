#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
uniform sampler2D u_texture;

void main() {
    vec2 explosionCenter = vec2(0.5, 0.5);
    vec2 dir = v_texCoord - explosionCenter;
    vec2 finalUV = v_texCoord;
    float dist = length(dir);
    float time = 1.0;

    float shock = sin(dist * 20.0 - time * 40.0) * 0.02;
    shock *= smoothstep(1.0, 0.0, dist / (0.5 * time + 0.2)); // Fade out
    finalUV += normalize(dir) * shock;
    gl_FragColor = texture2D(u_texture, finalUV);
}
