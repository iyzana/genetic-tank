package de.randomerror.genetictank.entities

import javafx.scene.canvas.GraphicsContext

/**
 * Created by henri on 19.10.16.
 */

abstract class Entity {
    var x = 0
    var y = 0

    var velX = 0.0
    var velY = 0.0

    abstract fun render(gc: GraphicsContext)
    abstract fun update(deltaTime: Double)
}