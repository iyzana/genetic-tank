package de.randomerror.genetictank.entities

import de.randomerror.genetictank.helper.getBounds
import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.StrokeLineJoin.ROUND

/**
 * Created by Jannis on 24.10.16.
 */
class Wall(x: Double, y: Double, val width: Double, val height: Double) : Entity() {
    init {
        this.x = x
        this.y = y
    }

    val bounds = getBounds()

    override fun render(gc: GraphicsContext) = gc.run {
        lineWidth = 3.0
        lineJoin = ROUND

        fillRect(x, y, width, height)
    }

    override fun update(deltaTime: Double) {

    }

    override fun toString(): String {
        return "Wall($x, $y, $width, $height)"
    }

    override fun collides(x: Double, y: Double) = bounds.collidesWith(x, y)
}