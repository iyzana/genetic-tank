package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.helper.log
import de.randomerror.genetictank.helper.shuffled
import de.randomerror.genetictank.labyrinth.Labyrinth
import de.randomerror.genetictank.labyrinth.LabyrinthGenerator
import de.randomerror.genetictank.labyrinth.Point
import java.io.*
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.concurrent.thread

/**
 * Created by henri on 01.11.16.
 */

object Trainer {
    val numPokémon = 1000
    private val roundTime = 30.0

    var pokémon = (0 until numPokémon).map {
        ASI()
    }

    val deltaTime = 0.016

    var labSeed = 0L
    var trainingLabyrinths = Array(3) { LabyrinthGenerator.generate(5, 5, Random(labSeed++)) }

    val threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()) { runnable ->
        thread(isDaemon = true, start = false) { runnable.run() }
    }

    var generation = 0

    data class PokemonFitness(val pokemon: ASI, val fitness: Double)

    fun evolve(callback: (Double) -> Unit): List<PokemonFitness> {
        var evolved = 0

        val newLabyrinths = generation % 40 == 0 && generation != 0
        if (newLabyrinths)
            trainingLabyrinths = Array(3) { LabyrinthGenerator.generate(5, 5, Random(labSeed++)) }

        fun ASI.toFitnessChecker() = Callable<PokemonFitness> {
            fitness = when {
                !newLabyrinths && copyCount != 0 -> fitness
                else -> trainingLabyrinths.map { train(pokemon = this, on = it) }.min() ?: -1000.0
            }

            callback((++evolved).toDouble() / pokémon.size)

            PokemonFitness(this, fitness)
        }

//        labyrinth = LabyrinthGenerator.generate(10, 10, Random(1))
//        walls = labyrinth.asWalls()

        val fitness = threadPool
                .invokeAll(pokémon.map(ASI::toFitnessChecker))
                .map(Future<PokemonFitness>::get)
                .shuffled()
                .sortedByDescending { it.fitness }

        println(fitness.last().pokemon.brain.toString())

        val surviveCount = (numPokémon * 0.05).toInt().coerceAtLeast(1)
        val mutateCount = (numPokémon * 0.95).toInt()

        pokémon = fitness.take(surviveCount).map { it.pokemon.copy() }

        pokémon += (0 until mutateCount).map {
            pokémon[(Math.random() * pokémon.size).toInt()].copy().apply { mutate() }
        }

        generation++

        return fitness
    }

    fun save() {
        val saveFile = File("savefile-$generation.sav")
        if (saveFile.exists()) return
        if (!saveFile.createNewFile())
            throw IOException("can't create file " + saveFile.canonicalPath)

        log.info("preparing save")
        val saveData = pokémon.map(ASI::copy)

        thread(name = "saver", isDaemon = true) {
            log.info("saving generation $generation")
            ObjectOutputStream(FileOutputStream(saveFile)).use { stream ->
                stream.writeObject(saveData)
                stream.flush()
            }
            log.info("saving complete")
        }
    }

    fun load(): Boolean {
        val saveFile = File("savefile.sav")
        if (saveFile.exists()) {
            log.info("loading generation")
            ObjectInputStream(FileInputStream(saveFile)).use { stream ->
                pokémon = stream.readObject() as List<ASI>
            }
            log.info("loading complete")
            return true
        }

        return false
    }

    fun train(pokemon: ASI, on: Labyrinth): Double {
        return train(pokemon, StillPlayer(), on)
    }

    fun train(pokemon: ASI, against: Player, on: Labyrinth): Double {
        val entities: MutableList<Entity> = on.asWalls().toMutableList()

        val enemy = Tank(400.0, 400.0, against)
        val body = Tank(150.0, 10.0, pokemon)
        var time = 0.0

        enemy.entities = entities
        enemy.labyrinth = on
        body.entities = entities
        body.labyrinth = on

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
        return timeScore + body.visitedTiles.size * 10 - enemyDistance / 50 + body.movedDistance / 1000 + body.shotBullets * 5 - body.blockedTicks / 30
    }

    fun ASI.mutate() {
        copyCount = 0

        val allAxons = brain.allAxons

        allAxons.map { axon -> Array(axon.w) { x -> Array(axon.h) { y -> axon to Point(x, y) } } }
                .flatMap { it.toList() }
                .flatMap { it.toList() }
                .shuffled()
                .take(allAxons.size * 80 / 100)
                .forEach { pair ->
                    val (axon, position) = pair

                    val ohm = axon[position.x, position.y]
                    axon[position.x, position.y] = ohm + (Math.random() * 0.1 - 0.5)
                }
    }
}