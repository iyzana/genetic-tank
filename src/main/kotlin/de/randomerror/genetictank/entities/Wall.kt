package de.randomerror.genetictank.entities

import de.randomerror.genetictank.helper.getBounds
import de.randomerror.genetictank.helper.transformContext
import javafx.scene.canvas.GraphicsContext

/**
 * Created by Jannis on 24.10.16.
 */
class Wall(x: Double, y: Double, val width: Double, val height: Double) : Entity() {
    init {
        this.x = x
        this.y = y
    }

    val bounds = getBounds()

    override fun render(gc: GraphicsContext) = gc.transformContext {
        fillRect(x, y, width, height)
    }

    override fun update(deltaTime: Double) {

    }

    override fun toString(): String {
        return "Wall($x, $y, $width, $height)"
    }

    override fun collides(x: Double, y: Double) = bounds.collidesWith(x, y)
}