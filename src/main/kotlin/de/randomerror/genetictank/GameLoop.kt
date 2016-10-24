package de.randomerror.genetictank

import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.helper.rotate
import de.randomerror.genetictank.helper.transformContext
import de.randomerror.genetictank.input.Keyboard
import de.randomerror.genetictank.input.Mouse
import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color.BLACK
import javafx.scene.paint.Color.color
import javafx.scene.shape.StrokeLineJoin.ROUND

/**
 * Created by Jannis on 19.10.16.
 */
class GameLoop(canvas: Canvas) : AnimationTimer() {
    val gc: GraphicsContext = canvas.graphicsContext2D
    var previousTime = System.nanoTime()

    // TODO Remove this example state
    val start = System.nanoTime()
    var x = 0.0
    var y = 0.0

    val tank = Tank(Color(.5, 0.3, 0.0, 1.0))

    override fun handle(now: Long) {
        update(now)
        render()
    }

    private fun update(now: Long) {
        Mouse.poll()
        Keyboard.poll()

        val deltaTime = (now - previousTime) / 1000000000.0;
        val fps = 1 / deltaTime
        previousTime = now
        
        val maxRightX = gc.canvas.width - 100
        val maxBottomY = gc.canvas.height - 100

        x = ((now - start) / 3000000.0) % (maxRightX * 2)
        y = ((now - start) / 3000000.0) % (maxBottomY * 2)

        if (x > maxRightX) x = maxRightX - (x - maxRightX)
        if (y > maxBottomY) y = maxBottomY - (y - maxBottomY)

        tank.update(deltaTime)
    }

    private fun render() = gc.run {
        clearRect(0.0, 0.0, canvas.width, canvas.height)

        tank.render(gc);

        fill = if (Mouse.isDown()) BLACK else color(x / 3000, 0.0, y / 2000)
        stroke = color(0.0, x / 3000, y / 2000)
        lineWidth = x / 300
        lineJoin = ROUND
        
        transformContext {
            translate(x, y)

            transformContext {
                rotate(y / 3, 50.0, 50.0)
                fillRect(0.0, 0.0, 100.0, 100.0)
            }

            strokeRect(0.0, 0.0, 100.0, 100.0)
        }
    }
}