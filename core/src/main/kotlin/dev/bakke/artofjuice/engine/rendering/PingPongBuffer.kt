package dev.bakke.artofjuice.engine.rendering

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils.FrameBuffer

class PingPongBuffer(width: Int, height: Int) {
    private var buffers = arrayOf(
        FrameBuffer(Pixmap.Format.RGBA8888, width, height, false),
        FrameBuffer(Pixmap.Format.RGBA8888, width, height, false)
    )
    private var index = 0

    val read: FrameBuffer
        get() = buffers[index]

    val write: FrameBuffer
        get() = buffers[1 - index]

    fun swap() {
        index = 1 - index
    }

    fun dispose() {
        buffers.forEach { it.dispose() }
    }

    fun resize(width: Int, height: Int) {
        dispose()
        buffers[0] = FrameBuffer(Pixmap.Format.RGBA8888, width, height, false)
        buffers[1] = FrameBuffer(Pixmap.Format.RGBA8888, width, height, false)
    }
}
