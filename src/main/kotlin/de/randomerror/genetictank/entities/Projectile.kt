package de.randomerror.genetictank.entities

import de.randomerror.genetictank.GameLoop
import de.randomerror.genetictank.helper.transformContext
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

/**
 * Created by henri on 19.10.16.
 */
class Projectile(x: Double, y: Double, heading: Double) : Entity() {
    
    init {
        this.x = x
        this.y = y
        velX = Math.sin(heading) * 500
        velY = -Math.cos(heading) * 500
    }

    val color = Color(.0, .0, .0, 1.0)
    val radius = 2.0

    override fun render(gc: GraphicsContext) = gc.transformContext {

        fill = color

        fillOval(x, y, radius * 2, radius * 2)

    }

    override fun update(deltaTime: Double) {
        x += velX * deltaTime
        y += velY * deltaTime

        GameLoop.checkCollisions(this);
    }

    override fun collides(x: Double, y: Double) = false
}