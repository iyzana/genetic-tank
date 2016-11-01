package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.GameLoop
import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.helper.render
import de.randomerror.genetictank.helper.transformContext
import de.randomerror.genetictank.labyrinth.LabyrinthGenerator
import javafx.scene.paint.Color
import java.util.*

/**
 * Created by henri on 01.11.16.
 */

object Trainer {

    var pokemons: MutableList<ASI> = LinkedList<ASI>()
    val numPokemons = 1000

    val deltaTime = 0.016

    init {
        (0 until numPokemons).forEach {
            pokemons.add(ASI(listOf(47, 15, 5)))
        }
        GameLoop.entities += LabyrinthGenerator.generate(10,10, Random(1)).asWalls()
    }

    fun evolve(): List<ASI> {
        val fitness = pokemons.map { pokemon ->
            val fit = train(pokemon)
            println(fit)
            pokemon to fit
        }.sortedByDescending { it.second }

        println("best: " + fitness.first().second)

        val survivors = fitness.take((numPokemons*0.2).toInt()).map { it.first }.toMutableList()
        (0..(numPokemons-survivors.size)).forEach {
            survivors += ASI(listOf(47, 15, 5))
        }

        pokemons = survivors

        GameLoop.entities.clear()

        return survivors
    }



    fun train(pokemon: ASI): Double {
        GameLoop.entities.clear()
        GameLoop.entities += LabyrinthGenerator.generate(10,10, Random(1)).asWalls()
        val enemy = Tank(500.0, 500.0, Color.BLACK, TrainingAI())
        val body = Tank(50.0, 10.0, Color.ALICEBLUE, pokemon)
        var time = 0.0;

        GameLoop.entities += enemy
        GameLoop.entities += body

        do {
            GameLoop.entities.toList().forEach { it.update(deltaTime) }
            time += deltaTime
        } while (enemy.alive && body.alive && time < 60)

        return when {
            body.alive && !enemy.alive -> 60.0-time
            body.alive && enemy.alive -> 0.0
            else -> time-60.0
        }
    }
}