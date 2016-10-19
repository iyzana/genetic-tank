package de.randomerror.genetictank

import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color

/**
 * Created by Jannis on 19.10.16.
 */
class GameLoop(val canvas: Canvas) : AnimationTimer() {
    val gc = canvas.graphicsContext2D
    val start = System.nanoTime()
    var previousTime = System.nanoTime()

    // TODO Split into update and render code
    override fun handle(now: Long) {
        val diff = (now - previousTime) / 1000000000.0;
        val fps = 1 / diff
        previousTime = now
        
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)

        val maxRightX = canvas.width - 100
        val maxBottomY = canvas.height - 100

        var x = ((now - start) / 3000000.0) % (maxRightX * 2)
        var y = ((now - start) / 3000000.0) % (maxBottomY * 2)

        if (x > maxRightX) x = maxRightX - (x - maxRightX)
        if (y > maxBottomY) y = maxBottomY - (y - maxBottomY)

        gc.fill = Color.BLACK
        gc.fillRect(x, y, 100.0, 100.0)
    }
}