package de.randomerror.genetictank

import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.entities.Projectile
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.genetic.ASI
import de.randomerror.genetictank.genetic.StillPlayer
import de.randomerror.genetictank.genetic.Trainer
import de.randomerror.genetictank.helper.*
import de.randomerror.genetictank.input.Keyboard
import de.randomerror.genetictank.input.Keyboard.keyDown
import de.randomerror.genetictank.input.Mouse
import de.randomerror.genetictank.labyrinth.LabyrinthGenerator
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import java.util.*
import kotlin.concurrent.thread

class GameLoop(val canvas: Canvas? = null) {
    val gc: GraphicsContext? = canvas?.graphicsContext2D
    var previousTime = System.nanoTime()

    var translate: Vector2D = Vector2D(0.0, 0.0)
    var scale: Double = 1.0
    var fps = 0.0
    var showTime = 0.0
    var showCaseLab = 0
    var showCaseIndex = 0

    var KI = Tank(150.0, 10.0, Trainer.pokémon.first())
    var fitnesses = listOf<Trainer.PokemonFitness>()
    val averages = mutableListOf<Double>()
    var evolvePercentage = 0.0

    var evolveThread: Thread? = null

    var fitnessGraph: Graph? = null
    var averagesGraph: Graph? = null
    
    init {
        log.info("running with ${Runtime.getRuntime().availableProcessors().coerceAtMost(32)} threads")

        if(Trainer.load()) {
            labyrinth = Trainer.trainingLabyrinths[showCaseLab++]

            entities += Tank(400.0, 400.0, StillPlayer())
            entities += Tank(150.0, 10.0, Trainer.pokémon[showCaseIndex].copy())
            
            showTime = 30.0
        }
        canvas?.widthProperty()?.addListener { observable, oldValue, newValue -> calculateScale() }
        canvas?.heightProperty()?.addListener { observable, oldValue, newValue -> calculateScale() }

//        entities += Tank(900.0, 900.0, StillPlayer())
//        entities += KI
//
//        entities += Tank(150.0, 150.0, Color.PURPLE)

        entities += labyrinth.asWalls()
        calculateScale()
    }

    private fun calculateScale() = gc?.let { gc ->
        val (labW, labH) = labyrinth.getRealSize()

        scale = Math.min(gc.canvas.width / labW, gc.canvas.height / labH) * 0.8
        translate = Vector2D((gc.canvas.width - labW * scale) / 2, (gc.canvas.height - labH * scale) / 2)
    }

    fun handle(now: Long) {
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

        if (keyDown("c", once = true))
            entities.removeAll { it is Projectile }

        if (keyDown("f5", once = true) || (Trainer.generation % saveInterval == 0 && Trainer.generation != 0))
            Trainer.save()

        if (evolveThread?.isAlive ?: false)
            return

        if (!entities.filter { it is Tank }.all { (it as Tank).alive } || keyDown("g", once = true) || showTime <= 40) {
            entities.clear()
            
            if(showCaseLab < Trainer.trainingLabyrinths.size && Trainer.generation != 0) {
                labyrinth = Trainer.trainingLabyrinths[showCaseLab++]
                entities += labyrinth.asWalls()

                entities += Tank(400.0, 400.0, StillPlayer())
                entities += Tank(150.0, 10.0, Trainer.pokémon[showCaseIndex].copy())

                showTime = 30.0
            } else {
                showCaseLab = 0
                
                evolveThread = thread(isDaemon = true, name = "evolver") {
                    evolvePercentage = 0.0

                    fitnesses = Trainer.evolve { percentage -> evolvePercentage = percentage }
                    averages += fitnesses.sumByDouble { it.fitness } / fitnesses.size

                    val bestFitness = fitnesses.first().fitness
                    val bestFitness5 = fitnesses[4].fitness
                    val worstFitness = fitnesses.last().fitness
                    val averageFitness = averages.last()
                    val medianFitness = fitnesses[fitnesses.size / 2].fitness

                    val fitnessesReversed = fitnesses.reversed()

                    fitnessGraph = canvas?.let {
                        Graph(
                                0.5, 10, "fitness",
                                2.0, 5, "sorted tanks",
                                mapOf(Color.BLUE to (0 until fitnessesReversed.size).associateBy(Int::toDouble, { fitnessesReversed[it].fitness }),
                                        Color.ORANGE to mapOf(0.0 to averageFitness, fitnessesReversed.size.toDouble() to averageFitness))
                        )
                    }

                    if (averages.size > 1) {
                        val xScale = (fitnessGraph?.width ?: 0.0) / (averages.size - 1)
                        averagesGraph = canvas?.let {
                            Graph(
                                    xScale, 5, "average fitness",
                                    2.0, 10, "generation",
                                    mapOf(Color.ORANGE to (0 until averages.size).associateBy(Int::toDouble, { averages[it] }))
                            )
                        }
                    }

                    log.info("generation: ${Trainer.generation}, best: $bestFitness, 5th: $bestFitness5, median: $medianFitness, average: $averageFitness, worst: $worstFitness")

                    labyrinth = Trainer.trainingLabyrinths[showCaseLab++]
                    entities += labyrinth.asWalls()

                    entities += Tank(400.0, 400.0, StillPlayer())
                    entities += Tank(150.0, 10.0, Trainer.pokémon[showCaseIndex].copy())

                    showTime = 30.0
                }
            }

            return
        }

        entities.toList().forEach { it.update(0.016) }

        showTime -= 0.016
    }

    private fun render() = gc?.transformContext {
        try {
            clearRect(0.0, 0.0, canvas.width, canvas.height)

            var pos = 1

            fillText("fps: ${fps.toInt()}", 10.0, pos++ * 20.0)
            pos++

            fillText("generation: ${Trainer.generation}", 10.0, pos++ * 20.0)
            fillText("evolving: ${(evolvePercentage * 100).toInt()}%", 10.0, pos++ * 20.0)
            fillText("time remaining: ${showTime.toInt()}", 10.0, pos++ * 20.0)
            pos++

            if (!fitnesses.isEmpty()) {
                val bestFitness = fitnesses.first().fitness
                val bestFitness5 = fitnesses[4].fitness
                val worstFitness = fitnesses.last().fitness
                val averageFitness = averages.last()
                val medianFitness = fitnesses[fitnesses.size / 2].fitness

                fillText("fitness best: $bestFitness", 10.0, pos++ * 20.0)
                fillText("fitness worst: $worstFitness", 10.0, pos++ * 20.0)

                transformContext {
                    fill = Color.PURPLE
                    fillText("fitness 5th best: $bestFitness5", 10.0, pos++ * 20.0)

                    fill = Color.GREEN
                    fillText("fitness median: $medianFitness", 10.0, pos++ * 20.0)

                    fill = Color.ORANGE
                    fillText("fitness average: $averageFitness", 10.0, pos++ * 20.0)
                }

                transformContext {
                    translate(50.0, pos++ * 20.0)

                    fitnessGraph?.render(this)
                    fitnessGraph?.renderPoint(this, fitnesses.size.toDouble() / 2.0, medianFitness, 4.0, Color.GREEN)
                    fitnessGraph?.renderPoint(this, fitnesses.size.toDouble() - 5, bestFitness5, 4.0, Color.PURPLE)

                    val height = fitnessGraph?.height ?: 0.0

                    translate(0.0, height + 30.0)

                    averagesGraph?.render(this)
                }
            }

            transformContext {
                translate(canvas.width - 250.0, 50.0)

                entities.filter { it is Tank }
                        .map { it as Tank }
                        .map(Tank::player)
                        .filter { it is ASI }
                        .map { it as ASI }
                        .filter { it.time > 0.0 }
                        .take(1)
                        .map { it.brain.getThinkData(it.idea) }
                        .singleOrNull()
                        ?.let { thinkData ->
                            val input = thinkData[0]
                            (0 until input.h).map { input[it] }.forEachIndexed { index, value ->
                                fillText("$value", 0.0, index * 15.0)
                            }

                            thinkData.drop(1).forEachIndexed { layer, vector ->
                                (0 until vector.h).map { vector[it] }.forEachIndexed { index, value ->
                                    fill = Color.color(1 - value, 1 - value, 1 - value)
                                    fillRect(layer * 20.0 + 140.0, index * 15.0 - 10.0, 20.0, 15.0)

                                    fill = Color.RED
                                    fillText("${(value * 10).toInt()}", layer * 20.0 + 140.0, index * 15.0)
                                }
                            }
                        }
            }

            transformContext {
                translate(translate.x, translate.y)
                scale(scale, scale)

                entities.asReversed().forEach { render(it) }
            }
        } catch(e: Exception) {
            log.error("exception while rendering scene", e)
        }
    }

    companion object {
        var saveInterval = 100
        val entities = mutableListOf<Entity>()

        var labyrinth = LabyrinthGenerator.generate(10, 10, Random(1))
    }
}