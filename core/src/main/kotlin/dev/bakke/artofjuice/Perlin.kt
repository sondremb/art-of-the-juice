package dev.bakke.artofjuice

import kotlin.math.floor

object Perlin {
    // Ken Perlins originale permutasjonstabell - alle tallene fra 0 til 255 i tilfeldig rekkefølge,
    // duplisert, for håndtering av overflow
    private val p = intArrayOf(
        151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140,
        36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234,
        75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237,
        149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48,
        27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105,
        92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73,
        209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86,
        164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38,
        147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189,
        28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101,
        155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232,
        178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12,
        191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31,
        181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
        138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215,
        61, 156, 180,

        151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140,
        36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234,
        75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237,
        149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48,
        27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105,
        92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73,
        209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86,
        164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38,
        147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189,
        28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101,
        155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232,
        178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12,
        191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31,
        181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
        138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215,
        61, 156, 180,
    )

    fun perlin(x: Float, y: Float): Float {
        // TODO repeat
        val x_ = mod(x, 256)
        val y_ = mod(y, 256)

        // finner nærmeste heltall under x og y
        val xi = x_.toInt()
        val yi = y_.toInt()

        fun inc(num: Int): Int {
            return mod(num + 1, 256)
        }

        // finer x % 1 og y % 1
        val xf = x_ - xi
        val yf = y_ - yi

        // hash-verdier, basert på permutasjonstabellen
        val aa = p[xi + p[yi]]
        val ab = p[xi + p[inc(yi)]]
        val ba = p[inc(xi) + p[yi]]
        val bb = p[inc(xi) + p[inc(yi)]]

        // finner prikkproduktet mellom tilfeldig vektor og distansevektor for alle fire hjørner
        val x0y0 = grad(aa, xf, yf)
        val x0y1 = grad(ab, xf, yf - 1)
        val x1y0 = grad(ba, xf - 1, yf)
        val x1y1 = grad(bb, xf - 1, yf - 1)


        // smoother xf og yf til bruk som interpoleringsfaktor
        val u = fade(xf);
        val v = fade(yf);

        // interpoler en verdi midt mellom de fire hjørneverdiene
        val y0 = lerp(x0y0, x1y0, u);
        val y1 = lerp(x0y1, x1y1, u);
        val value = lerp(y0, y1, v);

        // verdi er nå i domene [-1, -1], normaliser til [0, 1]
        return (value + 1) / 2;
    }

    fun perlin(x: Float, y: Float, z: Float): Float {
        val x_ = mod(x, 256)
        val y_ = mod(y, 256)
        val z_ = mod(z, 256)

        // finner nærmeste heltall under x, y og z
        val xi = x_.toInt()
        val yi = y_.toInt()
        val zi = z_.toInt()

        fun inc(num: Int): Int {
            return mod(num + 1, 256)
        }

        // finer x % 1, y % 1 og z % 1
        val xf = x_ - xi
        val yf = y_ - yi
        val zf = z_ - zi

        // hash-verdier, basert på permutasjonstabellen
        val aaa = p[xi + p[yi + p[zi]]]
        val aab = p[xi + p[yi + p[inc(zi)]]]
        val aba = p[xi + p[inc(yi) + p[zi]]]
        val abb = p[xi + p[inc(yi) + p[inc(zi)]]]
        val baa = p[inc(xi) + p[yi + p[zi]]]
        val bab = p[inc(xi) + p[yi + p[inc(zi)]]]
        val bba = p[inc(xi) + p[inc(yi) + p[zi]]]
        val bbb = p[inc(xi) + p[inc(yi) + p[inc(zi)]]]

        // finner prikkproduktet mellom tilfeldig vektor og distansevektor for alle åtte hjørner
        val x0y0z0 = grad(aaa, xf, yf, zf)
        val x0y0z1 = grad(aab, xf, yf, zf - 1)
        val x0y1z0 = grad(aba, xf, yf - 1, zf)
        val x0y1z1 = grad(abb, xf, yf - 1, zf - 1)
        val x1y0z0 = grad(baa, xf - 1, yf, zf)
        val x1y0z1 = grad(bab, xf - 1, yf, zf - 1)
        val x1y1z0 = grad(bba, xf - 1, yf - 1, zf)
        val x1y1z1 = grad(bbb, xf - 1, yf - 1, zf - 1)

        // interpoler langs x med z=0
        val u = fade(xf)
        val z00 = lerp(x0y0z0, x1y0z0, u)
        val z01 = lerp(x0y1z0, x1y1z0, u)
        // interpoler langs x med z=1
        val z10 = lerp(x0y0z1, x1y0z1, u)
        val z11 = lerp(x0y1z1, x1y1z1, u)

        // interpoler langs y
        val v = fade(yf)
        val z0 = lerp(z00, z01, v)
        val z1 = lerp(z10, z11, v)

        // interpoler langs z
        val w = fade(zf)
        val value = lerp(z0, z1, w)

        // verdi er nå i domene [-1, -1], normaliser til [0, 1]
        return (value + 1) / 2
    }

    private fun mod(a: Float, m: Int) : Float {
        return ((a % m) + m ) % m
    }

    private fun mod(a: Int, m: Int): Int {
        return ((a % m) + m ) % m
    }

    private fun grad(hash: Int, x: Float, y: Float): Float {
        // hentet fra https://stackoverflow.com/a/17351156
        // tar imot en hashverdig og to tall x og y
        // basert på hash-verdien, velg en vektor fra følgende:
        // [-1, -1], [-1, 1], [1, -1] og [1, 1]
        // returner prikkproduktet (a.x * b.x + a.y * b.y) mellom den valgte vektoren og [x, y]
        return when (hash and 0x03) {
            0 -> x + y
            1 -> -x + y
            2 -> x - y
            else -> -x - y
        }
    }

    private fun grad(hash: Int, x: Float, y: Float, z: Float): Float {
        // hentet fra https://stackoverflow.com/a/17351156
        // tar imot en hashverdig og to tall x og y
        // basert på hash-verdien, velg en vektor [x, y, z] hvor hver verdi kan være -1 eller 1
        // returner prikkproduktet (a.x * b.x + a.y * b.y + a.z * b.z) mellom den valgte vektoren og [x, y, z]
        return when (hash and 0x07) {
            0 -> x + y + z
            1 -> -x + y + z
            2 -> x - y + z
            3 -> -x - y + z
            4 -> x + y - z
            5 -> -x + y - z
            6 -> x - y - z
            else -> -x - y - z
        }
    }

    // smoothing-funksjon, også fra Ken Perlin
    // tilsvarer 6t^5 - 15t^4 + 10t^3
    private fun fade(t: Float): Float {
        return t * t * t * (10 + t * (6 * t - 15))
    }

    // kunne brukt lerp fra LibGDX, men har den her så koden kan copy-pastes
    private fun lerp(a: Float, b: Float, t: Float): Float {
        return a + t * (b - a)
    }
}

class PerlinOctave(
    private val frequency: Float,
    private val amplitude: Float,
    private val persistence: Float,
    private val octaves: Int,
) {
    fun noise(x: Float, y: Float): Float {
        var total = 0f
        var maxValue = 0f
        var freq = frequency
        var amp = amplitude

        for (i in 0 until octaves) {
            total += Perlin.perlin(x * freq, y * freq) * amp
            maxValue += amp
            amp *= persistence
            freq *= 2f
        }
        return total / maxValue
    }

    fun noise(x: Float, y: Float, z: Float): Float {
        var total = 0f
        var maxValue = 0f
        var freq = frequency
        var amp = amplitude

        for (i in 0 until octaves) {
            total += Perlin.perlin(x * freq, y * freq, z * freq) * amp
            maxValue += amp
            amp *= persistence
            freq *= 2f
        }
        return total / maxValue
    }
}
