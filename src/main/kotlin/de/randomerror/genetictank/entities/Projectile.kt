package de.randomerror.genetictank.entities

import de.randomerror.genetictank.GameLoop
import de.randomerror.genetictank.helper.transformContext
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

/**
 * Created by henri on 19.10.16.
 */
class Projectile(x: Double, y: Double, heading: Double) : Entity() {
    val color = Color(.0, .0, .0, 1.0)
    val radius = 2.0

    init {
        this.x = x - radius
        this.y = y - radius
        velX = Math.sin(heading) * 250
        velY = -Math.cos(heading) * 250
    }


    override fun render(gc: GraphicsContext) = gc.transformContext {
        fill = color

        fillOval(x, y, radius * 2, radius * 2)

    }

    override fun update(deltaTime: Double) {
        val curX = x
        val curY = y
        val newX = x + velX * deltaTime
        val newY = y + velY * deltaTime

        val walls = GameLoop.objects
                .filter { it is Wall }
                .map { it as Wall }

        walls.firstOrNull { it.collides(newX + radius, curY + radius) }
                ?.let { wall ->
                    velX = -velX
                    x = if(x < wall.x + wall.w / 2) wall.x - radius else wall.x + wall.w + radius
                }

        walls.firstOrNull { it.collides(curX + radius, newY + radius) }
                ?.let { wall ->
                    velY = -velY
                    y = if(y < wall.y + wall.h / 2) wall.y - radius else wall.y + wall.h + radius
                }

        x += velX * deltaTime
        y += velY * deltaTime
    }

    override fun collides(x: Double, y: Double) = false
}