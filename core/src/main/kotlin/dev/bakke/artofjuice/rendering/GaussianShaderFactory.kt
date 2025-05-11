package dev.bakke.artofjuice.rendering

import java.io.File
import kotlin.math.exp

/** Standalone program som lager en gaussian-blur-shader gitt en kernel-radius m og en blur strength sigma
 */
fun main() {
    makeShader(4, 10f)
}

fun makeShader(m: Int, sigma: Float) {
    val coefficients = getCoefficients(m, sigma)
    val shaderContents = getShaderContent(coefficients)
    val sigmaFormatted = "%.2f".format(sigma).replace(".", "_")
    val filename = "assets/shaders/gaussian_m${m}_sigma${sigmaFormatted}.frag"
    val file = File(filename)
    println("Created file ${file.absoluteFile}")
    file.printWriter().use { out ->
        out.println(shaderContents)
    }
}

fun getShaderContent(coefficients: List<Float>): String {
    val n = coefficients.size
    val m = (n - 1) / 2
    require(m * 2 + 1 == n)

    return """uniform sampler2D u_texture;
uniform vec2 u_direction;
uniform vec2 u_screenSize;

varying vec2 v_texCoord;

void main() {
    vec2 onepixel = vec2(1.0) / u_screenSize;

    float coeffs[$n];
${coefficients.mapIndexed { i, c -> "coeffs[$i] = $c;" }.joinToString("\n").prependIndent()}

    vec4 sum = vec4(0.0);

    for(int i = 0; i < $n; ++i) {
        vec2 tc = v_texCoord + u_direction * float(i - $m) * onepixel;
        sum += coeffs[i] * texture2D(u_texture, tc);
    }

    gl_FragColor = sum;
}
"""
}

fun getCoefficients(m: Int, sigma: Float): List<Float> {
    val sigma2 = sigma * sigma
    val coefficients = (-m..m).map { i ->
        exp(- i * i / sigma2)
    }
    val sum = coefficients.sum()
    return coefficients.map { it / sum }
}
