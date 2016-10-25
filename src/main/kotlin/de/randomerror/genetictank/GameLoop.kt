package de.randomerror.genetictank

import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.helper.transformContext
import de.randomerror.genetictank.input.Keyboard
import de.randomerror.genetictank.input.Mouse
import de.randomerror.genetictank.labyrinth.Labyrinth
import javafx.animation.AnimationTimer
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import java.util.*

/**
 * Created by Jannis on 19.10.16.
 */
class GameLoop(canvas: Canvas) : AnimationTimer() {
    var gc = canvas.graphicsContext2D
    var previousTime = System.nanoTime()

    val translate: Point2D
    val scale: Double
    var fps = 0.0
    var ups = 0.0

    init {
        objects += Tank(0.0, 0.0, Color.SADDLEBROWN)
//        objects += Tank(100.0, 100.0, Color.PURPLE)
        
        val labWidth = 8
        val labHeight = 8
        objects += Labyrinth.generate(labWidth, labHeight, Random(1))

        val screenScale = Math.min(gc.canvas.width / 1920.0, gc.canvas.height / 1080.0)
        gc.scale(screenScale, screenScale)

        val (labW, labH) = Labyrinth.getRealSize(labWidth, labHeight)

        scale = Math.min(1920.0 / labW, 1080.0 / labH) * 0.8
        translate = Point2D((1920 - labW * scale) / 2, (1080 - labH * scale) / 2)
    }

    override fun handle(now: Long) {
        update(now)
        render()
    }

    private fun update(now: Long) {
        Mouse.poll()
        Keyboard.poll()

        val deltaTime = (now - previousTime) / 1000000000.0
        fps = (fps * 20 + 1 / deltaTime) / 21
        previousTime = now
        
        val updateDelta = Math.min(deltaTime, 1.0 / 40.0)
        ups = (ups * 20 + 1 / updateDelta) / 21

        objects.forEach { it.update(updateDelta) }
    }

    private fun render() = gc.transformContext {
        clearRect(0.0, 0.0, 1920.0, 1080.0)
        
        fillText("fps: ${fps.toInt()}", 10.0, 20.0)
        fillText("ups: ${ups.toInt()}", 10.0, 40.0)

        gc.transformContext {
            translate(translate.x, translate.y)
            scale(scale, scale)

            objects.forEach { it.render(gc) }
        }
    }

    companion object {
        val objects = mutableListOf<Entity>()
    }
}