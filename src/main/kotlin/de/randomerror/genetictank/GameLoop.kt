package de.randomerror.genetictank

import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.entities.Projectile
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.entities.Wall
import de.randomerror.genetictank.genetic.ASI
import de.randomerror.genetictank.genetic.HumanPlayer
import de.randomerror.genetictank.helper.render
import de.randomerror.genetictank.helper.transformContext
import de.randomerror.genetictank.input.Keyboard
import de.randomerror.genetictank.input.Keyboard.keyDown
import de.randomerror.genetictank.input.Mouse
import de.randomerror.genetictank.labyrinth.Labyrinth
import de.randomerror.genetictank.labyrinth.LabyrinthGenerator
import javafx.animation.AnimationTimer
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import java.util.*

class GameLoop(canvas: Canvas) : AnimationTimer() {
    var gc = canvas.graphicsContext2D
    var previousTime = System.nanoTime()
    
    var labyrinth: Labyrinth

    var translate: Point2D = Point2D(0.0, 0.0)
    var scale: Double = 1.0
    var level = 1L
    var fps = 0.0
    var ups = 0.0

    var KI = Tank(150.0, 50.0, Color.ALICEBLUE, ASI(listOf(45, 15, 5)))

    init {
        canvas.widthProperty().addListener { observable, oldValue, newValue -> calculateScale() }
        canvas.heightProperty().addListener { observable, oldValue, newValue -> calculateScale() }

        entities += Tank(50.0, 50.0, Color.SADDLEBROWN, HumanPlayer())
        entities += KI

//        entities += Tank(150.0, 150.0, Color.PURPLE)

        labyrinth = LabyrinthGenerator.generate(10, 10, Random(level++))
        entities += labyrinth.asWalls()
        calculateScale()
    }

    private fun loadLabyrinth() {
        entities.removeAll { it is Wall }

        labyrinth = LabyrinthGenerator.generate(10, 10, Random(level++))
        entities += labyrinth.asWalls()
        calculateScale()
    }

    private fun calculateScale() {
        val (labW, labH) = labyrinth.getRealSize()

        scale = Math.min(gc.canvas.width / labW, gc.canvas.height / labH) * 0.8
        translate = Point2D((gc.canvas.width - labW * scale) / 2, (gc.canvas.height - labH * scale) / 2)
    }

    override fun handle(now: Long) {
        update(now)
        render()
    }

    private fun update(now: Long) {
        Mouse.poll()
        Keyboard.poll()

        if (keyDown(KeyCode.L, once = true))
            loadLabyrinth()
        if (keyDown(KeyCode.C, once = true))
            GameLoop.entities.removeAll { it is Projectile }
        if (keyDown(KeyCode.T, once = true)) {
            entities -= KI
            KI = Tank(150.0, 50.0, Color.ALICEBLUE, ASI(listOf(45, 15, 5)))
            entities += KI
        }

        val deltaTime = (now - previousTime) / 1000000000.0
        val updateDelta = Math.min(deltaTime, 1.0 / 40.0)

        fps = (fps * 20 + 1 / deltaTime) / 21
        previousTime = now

        entities.toList().forEach { it.update(updateDelta) }
    }

    private fun render() = gc.transformContext {
        clearRect(0.0, 0.0, canvas.width, canvas.height)
        
        fillText("fps: ${fps.toInt()}", 10.0, 20.0)

        transformContext {
            translate(translate.x, translate.y)
            scale(scale, scale)
            
            entities.asReversed().forEach { render(it) }
        }
    }

    companion object {
        val entities = mutableListOf<Entity>()
    }
}