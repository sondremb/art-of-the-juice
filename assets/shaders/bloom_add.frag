#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform sampler2D u_bloom;
uniform float u_intensity;

void main() {
    vec4 color = texture2D(u_texture, v_texCoord);
    vec4 bloom = texture2D(u_bloom, v_texCoord);
    color = color + bloom * u_intensity;
    gl_FragColor = vec4(color.rgb, 1.0);
}
