package dev.bakke.artofjuice

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

class GamePreferences {
    companion object {
        const val PREFS_NAME = "artofjuice"
        private var prefs: Preferences = Gdx.app.getPreferences(PREFS_NAME)

        fun renderDebug(): Boolean {
            return prefs.getBoolean("renderDebug", false)
        }

        fun setRenderDebug(value: Boolean) {
            prefs.putBoolean("renderDebug", value)
            prefs.flush()
        }
    }
}
