package de.randomerror.genetictank.entities

import de.randomerror.genetictank.helper.rotate
import de.randomerror.genetictank.helper.transformContext
import de.randomerror.genetictank.input.Keyboard
import de.randomerror.genetictank.input.Keyboard.keyDown
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import java.util.*

/**
 * Created by henri on 19.10.16.
 */

class Tank(xPos: Double, yPos: Double, val color: Color) : Entity() {

    init {
        this.x = xPos
        this.y = yPos
        velX = 200.0
        velY = 200.0
    }

    val width = 30.0
    val height = 50.0

    var heading = 0.0
    val velRotation = 4.0

    val projectiles = LinkedList<Projectile>()

    val actions = mapOf<KeyCode, (Double) -> Unit>(
            KeyCode.W to { deltaTime ->
                x += Math.sin(heading) * velX * deltaTime
                y -= Math.cos(heading) * velY * deltaTime
            },
            KeyCode.S to { deltaTime ->
                x -= Math.sin(heading) * velX * deltaTime
                y += Math.cos(heading) * velY * deltaTime
            },
            KeyCode.A to { deltaTime ->
                heading -= deltaTime * velRotation
            },
            KeyCode.D to { deltaTime ->
                heading += deltaTime * velRotation
            },
            KeyCode.M to { deltaTime ->
                projectiles += Projectile(x + width / 2, y + height / 2, heading)
            })

    override fun render(gc: GraphicsContext) = gc.transformContext {

        transformContext {
            translate(x, y)

            rotate(Math.toDegrees(heading), width / 2, height / 2)

            stroke = Color(0.0, 0.0, 0.0, 1.0)
            lineWidth = 1.0

            fill = color
            fillRect(0.0, 0.0, width, height)
            strokeRect(0.0, 0.0, width, height)

            fill = color.brighter()
            fillRect(0.4 * width, -0.2 * width, 0.2 * width, 0.8 * width)
            strokeRect(0.4 * width, -0.2 * width, 0.2 * width, 0.8 * width)

            fillOval(0.1 * width, (height - 0.8 * width) / 2, 0.8 * width, 0.8 * width)
            strokeOval(0.1 * width, (height - 0.8 * width) / 2, 0.8 * width, 0.8 * width)
        }

        projectiles.forEach { it.render(gc) }
    }

    override fun update(deltaTime: Double) {
        actions.filter { keyDown(it.key) }.forEach { key, action ->
            action(deltaTime)
        }

        if(Keyboard.keyDown(KeyCode.C))
            projectiles.clear()
        while (projectiles.size > 1)
            projectiles.removeLast()

        projectiles.forEach { it.update(deltaTime) }
    }

    override fun collides(x: Double, y: Double) = x > this.x && x < this.x + width && y > this.y && y < this.y + height
}
