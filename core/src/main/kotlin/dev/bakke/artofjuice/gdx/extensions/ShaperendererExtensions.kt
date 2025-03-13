package dev.bakke.artofjuice.gdx.extensions

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle

fun ShapeRenderer.rect(rectangle: Rectangle) {
    rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height)
}
