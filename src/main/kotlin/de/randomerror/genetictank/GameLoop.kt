package de.randomerror.genetictank

import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.entities.Projectile
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.entities.Wall
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
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import java.util.*
import kotlin.concurrent.thread

class GameLoop(canvas: Canvas, default: Boolean = false) : AnimationTimer() {
    var gc: GraphicsContext = canvas.graphicsContext2D
    var previousTime = System.nanoTime()


    var translate: Point2D = Point2D(0.0, 0.0)
    var scale: Double = 1.0
    var fps = 0.0
    var showTime = 30.0

    var KI = Tank(150.0, 10.0, Color.ALICEBLUE, Trainer.pokémon.first())
    var fitnesses = listOf<Trainer.PokemonFitness>()
    var evolvePercentage = 0.0

    var evolveThread: Thread? = null

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

        if (evolveThread?.isAlive ?: false)
            return

        if (keyDown(KeyCode.L, once = true))
            loadLabyrinth()
        if (keyDown(KeyCode.C, once = true))
            GameLoop.entities.removeAll { it is Projectile }

        if (!entities.filter { it is Tank }.all { (it as Tank).alive } || keyDown(KeyCode.G, once = true) || showTime <= 0) {
            GameLoop.entities.clear()

            evolveThread = thread(isDaemon = true, name = "evolver") {
                evolvePercentage = 0.0

                fitnesses = Trainer.evolve { percentage -> evolvePercentage = percentage }

                labyrinth = Trainer.labyrinth
                entities += Trainer.walls

                entities += Tank(400.0, 400.0, Color.SADDLEBROWN, StillPlayer())
                entities += Tank(150.0, 10.0, Color.color(Math.random(), Math.random(), Math.random()), Trainer.pokémon[4].copy())

                showTime = 30.0
            }

            return
        }

        entities.toList().forEach { it.update(0.016) }

        showTime -= 0.016
    }

    private fun render() = gc.transformContext {
        clearRect(0.0, 0.0, canvas.width, canvas.height)

        var pos = 1

        fillText("fps: ${fps.toInt()}", 10.0, pos++ * 20.0)
        fillText("generation: ${Trainer.generation}", 10.0, pos++ * 20.0)
        fillText("evolving: ${(evolvePercentage * 100).toInt()}%", 10.0, pos++ * 20.0)

        if (!fitnesses.isEmpty()) {
            val bestFitness = fitnesses.first().fitness
            val bestFitness5 = fitnesses[4].fitness
            val averageFitness = fitnesses.sumByDouble { it.fitness } / fitnesses.size
            val medianFitness = fitnesses[fitnesses.size / 2].fitness

            fillText("fitness best: $bestFitness", 10.0, pos++ * 20.0)
            fillText("fitness 5th best: $bestFitness5", 10.0, pos++ * 20.0)

            transformContext {
                fill = Color.GREEN
                fillText("fitness median: $medianFitness", 10.0, pos++ * 20.0)

                fill = Color.ORANGE
                fillText("fitness average: $averageFitness", 10.0, pos++ * 20.0)
            }

            transformContext {
                gc.translate(25.0, 250.0)
                renderGraph(gc)
            }
        }

        transformContext {
            translate(translate.x, translate.y)
            scale(scale, scale)

            entities.asReversed().forEach { render(it) }
        }
    }

    fun renderGraph(gc: GraphicsContext) = gc.transformContext {
        val yAxisScale = 0.05
        val xAxisScale = 0.5

        transform(1.0, 0.0,
                  0.0, -1.0,
                  0.0, 0.0)
        lineWidth = 3.0
        beginPath()
        fitnesses.reversed().forEachIndexed { i, fitness ->
            lineTo(i.toDouble()*xAxisScale, fitness.fitness*yAxisScale)
        }
        stroke()

        val minYVal = (fitnesses.last().fitness-10)*yAxisScale
        val maxYVal = (fitnesses.first().fitness+10)*yAxisScale

        val minXVal = 0.0
        val maxXVal = fitnesses.size.toDouble()*xAxisScale

        stroke = Color.RED
        transformContext {
            lineWidth = 1.0
            //render x-Axis
            strokeLine(minXVal, 0.0, maxXVal, 0.0)

            transformContext {
                //render median
                fill = Color.GREEN
                fillOval((fitnesses.size / 2) * xAxisScale - 4.0, fitnesses[fitnesses.size / 2].fitness*yAxisScale - 4.0, 8.0, 8.0)

                //render average
                stroke = Color.ORANGE
                val average = fitnesses.sumByDouble { it.fitness } / fitnesses.size
                strokeLine(minXVal, average * yAxisScale, maxXVal, average * yAxisScale)
            }

            fillText("0", minXVal-12.0, 5.0)
        }
        //render y-Axis
        strokeLine(minXVal, minYVal, minXVal, maxYVal)
    }

    companion object {
        val entities = mutableListOf<Entity>()

        var level = 1L
        var labyrinth: Labyrinth = LabyrinthGenerator.generate(5, 5, Random(1))
    }
}