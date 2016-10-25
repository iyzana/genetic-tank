package de.randomerror.genetictank.entities

import javafx.scene.canvas.GraphicsContext
import javafx.scene.shape.StrokeLineJoin.ROUND

/**
 * Created by Jannis on 24.10.16.
 */
class Wall(x: Double, y: Double, val w: Double, val h: Double) : Entity() {

    init {
        this.x = x
        this.y = y
    }

    override fun render(gc: GraphicsContext) = gc.run {
        lineWidth = 3.0
        lineJoin = ROUND

        fillRect(x, y, w, h)
    }

    override fun update(deltaTime: Double) {

    }

    override fun toString(): String {
        return "Wall($x, $y, $w, $h)"
    }

    override fun collides(x: Double, y: Double) = x > this.x && x < this.x + w && y > this.y && y < this.y + h
}