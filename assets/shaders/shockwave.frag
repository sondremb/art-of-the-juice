#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform vec2 u_screenSize;
uniform float u_time;
uniform float u_maxTime;
uniform vec2 u_center;

// hentet fra https://godotshaders.com/shader/distortion/
void main() {
    vec2 uv = v_texCoord;
    if(u_time >= u_maxTime) {
        gl_FragColor = texture2D(u_texture, uv);
        return;
    }

    float t = u_time / u_maxTime;
    float radius = mix(0.2, 1.0, t);
    float strength = mix(0.2, 0.0, t);
    float width = 0.05;
    float feather = 0.135;
    float aberration = 0.100;

    vec2 dir = uv - u_center;
    dir.y *= u_screenSize.y / u_screenSize.x;

    float dist = length(dir);

    float mask = (1.0 - smoothstep(radius - feather, radius, dist)) * smoothstep(radius - width - feather, radius - width, dist);
    vec2 offset = normalize(dir) * strength * mask;
    vec2 biasedUv = uv - offset;

    vec2 abberationVec = offset * aberration * mask;

    vec2 finalUv = uv * (1.0 - mask) + biasedUv * mask;

    vec4 red = texture2D(u_texture, finalUv + abberationVec);
    vec4 blue = texture2D(u_texture, finalUv - abberationVec);
    vec4 ori = texture2D(u_texture, finalUv);
    gl_FragColor = vec4(red.x, ori.y, blue.z, 1.0);
}
