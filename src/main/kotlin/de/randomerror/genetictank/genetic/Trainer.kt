package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.labyrinth.LabyrinthGenerator
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
        ASI(listOf(81, 81, 5))
    }

    val deltaTime = 0.016

    private val labyrinth = LabyrinthGenerator.generate(10, 10, Random(1))
    private val walls = labyrinth.asWalls()

    private data class PokemonFitness(val pokemon: ASI, val fitness: Double) {
        init {
            println(fitness)
        }
    }

    fun evolve(): List<ASI> {
        fun ASI.toFitnessChecker() = Callable<PokemonFitness> { PokemonFitness(this, train(this)) }

        val fitness = Executors.newFixedThreadPool(8)
                .invokeAll(pokémon.map(ASI::toFitnessChecker))
                .map(Future<PokemonFitness>::get)
                .sortedByDescending { it.fitness }

        println("best: " + fitness[0].fitness)

        val surviveCount = (numPokémon * 0.2).toInt()
        val mutateCount = (numPokémon * 0.8).toInt()

        pokémon = fitness.take(surviveCount).map { it.pokemon }
        
        pokémon += (0..mutateCount).map {
            ASI(listOf(81, 81, 5))
        }

        return pokémon
    }


    fun train(pokemon: ASI): Double {
        val entities: MutableList<Entity> = walls.toMutableList()

        val enemy = Tank(500.0, 500.0, Color.BLACK, HumanPlayer())
        val body = Tank(50.0, 10.0, Color.ALICEBLUE, pokemon)
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
        }
    }
}