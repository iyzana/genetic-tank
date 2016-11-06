package de.randomerror.genetictank

import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.entities.Projectile
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.entities.Wall
import de.randomerror.genetictank.genetic.ASI
import de.randomerror.genetictank.genetic.StillPlayer
import de.randomerror.genetictank.genetic.Trainer
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

class GameLoop(canvas: Canvas, default: Boolean = false) : AnimationTimer() {
    var gc = canvas.graphicsContext2D
    var previousTime = System.nanoTime()


    var translate: Point2D = Point2D(0.0, 0.0)
    var scale: Double = 1.0
    var fps = 0.0
    var ups = 0.0
    var showTime = 30.0

    var KI = Tank(150.0, 10.0, Color.ALICEBLUE, Trainer.pokémon.first())

    init {
        canvas.widthProperty().addListener { observable, oldValue, newValue -> calculateScale() }
        canvas.heightProperty().addListener { observable, oldValue, newValue -> calculateScale() }

        entities += Tank(400.0, 400.0, Color.SADDLEBROWN, StillPlayer())
        entities += KI

//        entities += Tank(150.0, 150.0, Color.PURPLE)

        entities += labyrinth.asWalls()
        calculateScale()
    }

    private fun loadLabyrinth() {
        entities.removeAll { it is Wall }

        labyrinth = LabyrinthGenerator.generate(5, 5, Random(1))
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

        val deltaTime = (now - previousTime) / 1000000000.0
        val updateDelta = Math.min(deltaTime, 1.0 / 40.0)

        fps = (fps * 20 + 1 / deltaTime) / 21
        previousTime = now

        showTime -= updateDelta

        if (keyDown(KeyCode.L, once = true))
            loadLabyrinth()
        if (keyDown(KeyCode.C, once = true))
            GameLoop.entities.removeAll { it is Projectile }
        if (keyDown(KeyCode.T, once = true)) {
            GameLoop.entities.removeAll { it is Projectile }
            entities -= KI
            KI = Tank(150.0, 10.0, Color.color(Math.random(), Math.random(), Math.random()), ASI(listOf(81, 81, 5)))
            entities += KI
        }

        if (!entities.filter { it is Tank }.all { (it as Tank).alive } || keyDown(KeyCode.G, once = true) || showTime <= 0) {
            GameLoop.entities.clear()
            Trainer.evolve()

            entities += Trainer.walls

            entities += Tank(400.0, 400.0, Color.SADDLEBROWN, StillPlayer())
            entities += Tank(150.0, 10.0, Color.color(Math.random(), Math.random(), Math.random()), Trainer.pokémon.first().copy())

            showTime = 30.0
        }

        entities.toList().forEach { it.update(0.016) }
    }

    private fun render() = gc.transformContext {
        clearRect(0.0, 0.0, canvas.width, canvas.height)

        fillText("fps: ${fps.toInt()}", 10.0, 20.0)
        fillText("generation: ${Trainer.generation}", 10.0, 36.0)
        fillText("fitness: ${Trainer.bestFitness}", 10.0, 55.0)

        transformContext {
            translate(translate.x, translate.y)
            scale(scale, scale)

            entities.asReversed().forEach { render(it) }
        }
    }

    companion object {
        val entities = mutableListOf<Entity>()

        var level = 1L
        var labyrinth: Labyrinth = LabyrinthGenerator.generate(5, 5, Random(1))
    }
}