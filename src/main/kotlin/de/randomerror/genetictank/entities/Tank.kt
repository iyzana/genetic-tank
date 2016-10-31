package de.randomerror.genetictank.entities

import de.randomerror.genetictank.GameLoop
import de.randomerror.genetictank.helper.getBounds
import de.randomerror.genetictank.helper.rotate
import de.randomerror.genetictank.helper.transformContext
import de.randomerror.genetictank.input.Keyboard.keyDown
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color

/**
 * Created by henri on 19.10.16.
 */
class Tank(xPos: Double, yPos: Double, val color: Color) : Entity() {

    init {
        this.x = xPos
        this.y = yPos
        velX = 150.0
        velY = 150.0
    }

    var alive = true

    val width = 30.0
    val height = 50.0

    var heading = 0.0

    val velocity = 150.0
    val velRotation = 4.0

    val actions = mapOf<KeyCode, (Double) -> Unit>(
            KeyCode.W to { deltaTime ->
                x += Math.sin(heading) * velocity * deltaTime
                y -= Math.cos(heading) * velocity * deltaTime
            },
            KeyCode.S to { deltaTime ->
                x -= Math.sin(heading) * velocity * deltaTime
                y += Math.cos(heading) * velocity * deltaTime
            },
            KeyCode.A to { deltaTime ->
                heading -= deltaTime * velRotation
            },
            KeyCode.D to { deltaTime ->
                heading += deltaTime * velRotation
            })

    override fun render(gc: GraphicsContext) = gc.transformContext {
        if (!alive) return@transformContext

        val outline = getBounds().outline
        gc.strokeRect(outline.x, outline.y, outline.width, outline.height)

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

    override fun update(deltaTime: Double) {
        if (!alive) return

        val forward = keyDown(KeyCode.W)
        val backward = keyDown(KeyCode.S)
        val left = keyDown(KeyCode.A)
        val right = keyDown(KeyCode.D)
        
        

        if (keyDown(KeyCode.M, once = true)) {
            val px = x + width / 2 + Math.sin(heading) * (height * 2 / 3)
            val py = y + height / 2 - Math.cos(heading) * (height * 2 / 3)
            GameLoop.entities += Projectile(px, py, heading)
        }

        if (keyDown(KeyCode.C))
            GameLoop.entities.removeAll { it is Projectile }
    }

    private fun getNewPosition(deltaTime: Double): Triple<Double, Double, Double> {
        val (velX, velY, velH) = getAttemptedMove()

        val testX = x + velX * deltaTime
        val testY = y + velY * deltaTime
        val testH = heading + velH * deltaTime

        return Triple(testX, testY, testH)
    }

    private fun getAttemptedMove(): Triple<Double, Double, Double> {
        val (attemptedVelX, attemptedVelY) = if (keyDown(KeyCode.W) && !keyDown(KeyCode.S)) {
            velocity * Math.sin(heading) to -velocity * Math.cos(heading)
        } else if (keyDown(KeyCode.S) && !keyDown(KeyCode.W)) {
            -velocity * Math.sin(heading) to velocity * Math.cos(heading)
        } else 0.0 to 0.0

        val attemptedRotation = if (keyDown(KeyCode.A) && !keyDown(KeyCode.D)) {
            -velRotation
        } else if (keyDown(KeyCode.D) && !keyDown(KeyCode.A)) {
            velRotation
        } else 0.0

        return Triple(attemptedVelX, attemptedVelY, attemptedRotation)
    }

    override fun collides(x: Double, y: Double) = getBounds().collidesWith(x, y)
}
