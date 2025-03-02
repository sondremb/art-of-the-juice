package dev.bakke.artofjuice

import Player
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.async.KtxAsync
import ktx.graphics.use

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val batch = SpriteBatch()
    private val player = Player()

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)
        batch.use {
            player.update(delta)
            player.render(it)
        }
    }

    override fun dispose() {
        player.disposeSafely()
        batch.disposeSafely()
    }
}
