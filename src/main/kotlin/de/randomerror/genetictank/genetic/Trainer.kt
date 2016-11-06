package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.helper.log
import de.randomerror.genetictank.helper.shuffled
import de.randomerror.genetictank.labyrinth.LabyrinthGenerator
import de.randomerror.genetictank.labyrinth.Point
import javafx.scene.paint.Color
import java.io.*
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.concurrent.thread

/**
 * Created by henri on 01.11.16.
 */

object Trainer{

    val numPokémon = 1000
    private val roundTime = 30.0

    var pokémon = (0 until numPokémon).map {
        ASI()
    }

    val deltaTime = 0.016

    private var labIndex = 1L
    var labyrinth = LabyrinthGenerator.generate(5, 5, Random(0))
    var walls = labyrinth.asWalls()
    var generation = 0

    data class PokemonFitness(val pokemon: ASI, val fitness: Double)

    fun evolve(callback: (Double) -> Unit): List<PokemonFitness> {
        var evolved = 0

        fun ASI.toFitnessChecker() = Callable<PokemonFitness> {
            PokemonFitness(this, train(this)).apply {
                callback((++evolved).toDouble() / pokémon.size)
            }
        }

        labyrinth = LabyrinthGenerator.generate(5, 5, Random(labIndex++))
        walls = labyrinth.asWalls()

        val threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()) { runnable ->
            thread(isDaemon = true, start = false) { runnable.run() }
        }

        val fitness = threadPool
                .invokeAll(pokémon.map(ASI::toFitnessChecker))
                .map(Future<PokemonFitness>::get)
                .shuffled()
                .sortedByDescending { it.fitness }

        println("best: " + fitness[0].fitness)

        val surviveCount = (numPokémon * 0.5).toInt().coerceAtLeast(1)
        val mutateCount = (numPokémon * 0.5).toInt()

        pokémon = fitness.take(surviveCount).map { it.pokemon.copy() }

        pokémon += (0 until mutateCount).map {
            pokémon[(Math.random() * pokémon.size).toInt()].copy().apply { mutate() }
        }

        generation++

        return fitness
    }

    fun train(pokemon: ASI): Double {
        return train(pokemon, StillPlayer())
    }

    fun save() {
        val savedata = pokémon.map(ASI::copy)
        thread {
            log.info("saving Generation")
            val stream = ObjectOutputStream(FileOutputStream("savefile.sav"))
            stream.writeObject(savedata)
            stream.flush()
            stream.close()
            log.info("saving complete")
        }
    }

    fun load() {
        val file = File("savefile.sav")
        if(file.exists()) {
            val stream = ObjectInputStream(FileInputStream(file))
            pokémon = stream.readObject() as List<ASI>
            stream.close()
        }
    }

    fun train(pokemon: ASI, against: Player): Double {
        val entities: MutableList<Entity> = walls.toMutableList()

        val enemy = Tank(400.0, 400.0, Color.SADDLEBROWN, StillPlayer())
        val body = Tank(150.0, 10.0, Color.ALICEBLUE, pokemon)
        var time = 0.0

        enemy.entities = entities
        enemy.labyrinth = labyrinth
        body.entities = entities
        body.labyrinth = labyrinth

        entities += enemy
        entities += body

        var distancesCount = 0
        var enemyDistance = 0.0

        do {
            entities.toList().forEach { it.update(deltaTime) }

            val currentDistance = Math.abs(enemy.x - body.x) + Math.abs(enemy.y - body.y)
            enemyDistance = (currentDistance + enemyDistance * distancesCount) / ++distancesCount

            time += deltaTime
        } while (enemy.alive && body.alive && time < roundTime)

        val timeScore = when {
            body.alive && !enemy.alive -> roundTime - time
            body.alive && enemy.alive -> 0.0
            else -> time - roundTime
        }
        return timeScore * 5 + body.visitedTiles.size * 10 - enemyDistance / 50 + body.movedDistance / 1000 + body.shotBullets
    }

    fun ASI.mutate() {
        val brainMass = brain.allAxons
                .flatMap { axon -> Array(axon.x) { i -> Array(axon.y) { j -> axon to Point(i, j) }.toList() }.flatMap { it }.toList() }
                .shuffled()
        brainMass.take(brainMass.size * 10 / 100)
                .forEach { pair ->
                    val (axon, position) = pair

                    val ohm = axon[position.x, position.y]
                    axon[position.x, position.y] = ohm + (Math.random() * 20 - 10) + (ohm * (Math.random() * 20 - 10) / 100)
                }
    }
}