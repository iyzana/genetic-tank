package de.randomerror.genetictank.entities

import de.randomerror.genetictank.helper.transformContext
import de.randomerror.genetictank.input.Keyboard.keyDown
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import de.randomerror.genetictank.helper.forEach
import de.randomerror.genetictank.helper.rotate

/**
 * Created by henri on 19.10.16.
 */

class Tank(val color: Color) : Entity() {

    init {
        velX = 300.0
        velY = 300.0
    }

    val width = 30.0
    val height = 50.0

    var heading = 0.0
    val velRotation = 2.0

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
            })

    override fun render(gc: GraphicsContext) = gc.run {

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
    }

    override fun update(deltaTime: Double) {
        actions.forEach { key, action ->
            if(keyDown(key))
                action(deltaTime)
        }
    }
}

