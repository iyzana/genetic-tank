package de.randomerror.genetictank.entities

import de.randomerror.genetictank.GameLoop
import de.randomerror.genetictank.helper.transformContext
import de.randomerror.genetictank.input.Keyboard
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color

/**
 * Created by henri on 19.10.16.
 */
class Projectile(x: Double, y: Double, heading: Double) : Entity() {
    var entities = GameLoop.entities

    val color = Color(.0, .0, .0, 1.0)
    val radius = 3.0

    var alive = true
    var remainingBounces = 11

    init {
        this.x = x - radius
        this.y = y - radius
        velX = Math.sin(heading) * 250
        velY = -Math.cos(heading) * 250
    }


    override fun render(gc: GraphicsContext) = gc.transformContext {
        if (!alive) return@transformContext

        fill = color

        fillOval(x, y, radius * 2, radius * 2)
    }

    override fun update(deltaTime: Double) {
        if (!alive) return

        if (Keyboard.keyDown(KeyCode.C))
            alive = false

        val curX = x
        val curY = y
        val newX = x + velX * deltaTime
        val newY = y + velY * deltaTime

        collideWalls(curX, curY, newX, newY)

        x += velX * deltaTime
        y += velY * deltaTime

        collideTank()
        
        if(!alive)
            entities.remove(this@Projectile)
    }

    private fun collideWalls(curX: Double, curY: Double, newX: Double, newY: Double) {
        val walls = entities
                .filter { it is Wall }
                .map { it as Wall }
                .filter { Math.abs(x - it.x) < 200 && Math.abs(y - it.y) < 200 }
                .filter { it.collides(newX + radius, curY + radius) || it.collides(curX + radius, newY + radius) }

        var bounced = false

        walls.firstOrNull { it.collides(newX + radius, curY + radius) }?.let { wall ->
            velX = -velX
            bounced = true

            x = if (x + radius < wall.x + wall.width / 2)
                wall.x - radius * 2
            else
                wall.x + wall.width
        }

        walls.firstOrNull { it.collides(curX + radius, newY + radius) }?.let { wall ->
            velY = -velY
            bounced = true

            y = if (y + radius < wall.y + wall.height / 2)
                wall.y - radius * 2
            else
                wall.y + wall.height
        }

        if (bounced) {
            if (remainingBounces > 0) remainingBounces--
            else alive = false
        }
    }

    private fun collideTank() {
        entities.filter { it is Tank }
                .map { it as Tank }
                .filter(Tank::alive)
                .filter { it.collides(x + radius, y + radius) }
//                .filter { it.player !is StillPlayer && it.player !is TrainingAI }
                .forEach {
                    it.alive = false
                    alive = false
                }
    }

    override fun collides(x: Double, y: Double) = false
}