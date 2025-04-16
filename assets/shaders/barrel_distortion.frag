#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
uniform sampler2D u_texture;

void main() {
    float magnitudeFactor = 5.0;
    float strength = 0.1;
    vec2 uvNormalized = v_texCoord * 2.0 - 1.0; //change UV range from (0,1) to (-1,1)
    float distortionMagnitude = abs(uvNormalized.x * uvNormalized.y);//get value with 1 at corner and 0 at middle
    float smoothDistortionMagnitude = pow(distortionMagnitude, magnitudeFactor);
    vec2 distorted = v_texCoord + uvNormalized * smoothDistortionMagnitude * strength;
    if(distorted.x < 0.0 || distorted.x > 1.0 || distorted.y < 0.0 || distorted.y > 1.0) {
        gl_FragColor = vec4(0, 0, 0, 1); // Set color to transparent if outside bounds
    } else {
        gl_FragColor = texture2D(u_texture, distorted); // Set color to white with full alpha
    }
}
