package de.randomerror.genetictank

import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.entities.Projectile
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.helper.transformContext
import de.randomerror.genetictank.input.Keyboard
import de.randomerror.genetictank.input.Mouse
import de.randomerror.genetictank.labyrinth.Labyrinth
import javafx.animation.AnimationTimer
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import java.util.*

/**
 * Created by Jannis on 19.10.16.
 */
class GameLoop(canvas: Canvas) : AnimationTimer() {
    var gc = canvas.graphicsContext2D
    var previousTime = System.nanoTime()
    
    val translate: Pair<Double, Double>
    val scale: Double

    init {
        val labWidth = 10
        val labHeight = 10
        objects += Labyrinth.generate(labWidth, labHeight, Random(0)).apply { println(size) }
        
        val screenScale = Math.min(gc.canvas.width / 1920.0, gc.canvas.height / 1080.0)
        gc.scale(screenScale, screenScale)
        
        val labScale = Math.min(10.0 / labWidth, 19.0 / labHeight)
        val labW = labWidth * 100.0 * labScale
        val labH = labHeight * 100.0 * labScale

        translate = (1920 - labW) / 2 to (1080 - labH) / 2
        scale = labScale
    }

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
        
        objects.forEach { it.update(deltaTime) }
    }

    private fun render() = gc.transformContext {
        clearRect(0.0, 0.0, 1920.0, 1080.0)
        
        scale(scale, scale)
        val (x, y) = translate
        translate(x, y)

        objects.forEach { it.render(gc) }
    }
    
    companion object {
        val objects = mutableListOf<Entity>()
        
        init {
            objects += Tank(0.0, 0.0, Color.SADDLEBROWN)
            objects += Tank(100.0, 100.0, Color.PURPLE)
        }
        
        fun checkCollisions(projectile: Projectile) {
            objects.filter { it.collides(projectile.x, projectile.y) }.forEach {
                //do semthing
            }
        }
    }
}