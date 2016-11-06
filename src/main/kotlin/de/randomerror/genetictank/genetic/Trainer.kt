package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.helper.shuffled
import de.randomerror.genetictank.labyrinth.LabyrinthGenerator
import de.randomerror.genetictank.labyrinth.Point
import javafx.scene.paint.Color
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Created by henri on 01.11.16.
 */

object Trainer {

    val numPokémon = 1000
    private val roundTime = 30.0

    var pokémon = (0 until numPokémon).map {
        ASI(listOf(82, 82, 81, 40, 5))
    }

    val deltaTime = 0.016

    private var labIndex = 1L
    var labyrinth = LabyrinthGenerator.generate(5, 5, Random(0))
    var walls = labyrinth.asWalls()
    var generation = 0

    data class PokemonFitness(val pokemon: ASI, val fitness: Double) {
        init {
            println(fitness)
        }
    }

    fun evolve(): List<PokemonFitness> {
        fun ASI.toFitnessChecker() = Callable<PokemonFitness> { PokemonFitness(this, train(this)) }

        labyrinth = LabyrinthGenerator.generate(5, 5, Random(labIndex++))
        walls = labyrinth.asWalls()

        val fitness = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
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

        do {
            entities.toList().forEach { it.update(deltaTime) }
            time += deltaTime
        } while (enemy.alive && body.alive && time < roundTime)

        return when {
            body.alive && !enemy.alive -> roundTime - time
            body.alive && enemy.alive -> 0.0
            else -> time - roundTime
        } * 100 + body.visitedTiles.size * 10 + body.movedDistance / 1000
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