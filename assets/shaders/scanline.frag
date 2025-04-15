#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform vec2 u_screenSize;

void main() {
    vec4 tex = texture2D(u_texture, v_texCoord);
    vec2 screen = v_texCoord * u_screenSize;
    if(mod(screen.y, 4.0) < 2.0) {
        tex *= 0.6;
    }
    gl_FragColor = tex;
    gl_FragColor.a = 1.0;
}
