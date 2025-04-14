package dev.bakke.artofjuice

import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()

        addScreen(GameScreen())
        addScreen(PerlinTest())
        addScreen(ConfigGunVisualsScreen())
        setScreen<GameScreen>()
    }
}

