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

    val pokemons = LinkedList<ASI>()
    val numPokemons = 1000

    val deltaTime = 0.016

    init {
        (0 until numPokemons).forEach {
            pokemons += ASI(listOf(47, 15, 5))
        }
        GameLoop.entities += LabyrinthGenerator.generate(10,10).asWalls()
    }

    fun evolve() {
        pokemons.forEach {
            train(it)
        }
    }



    fun train(pokemon: ASI): Double {
        val enemy = Tank(0.0, 0.0, Color.BLACK, HumanPlayer())
        val body = Tank(0.0, 0.0, Color.ALICEBLUE, pokemon)
        var time = 0.0;

        GameLoop.entities += enemy
        GameLoop.entities += body

        do {
            GameLoop.entities.forEach { it.update(deltaTime) }
            time += deltaTime
        } while (enemy.alive && body.alive && time < 60)

        return when {
            body.alive && !enemy.alive -> 60.0-time
            body.alive && enemy.alive -> 0.0
            else -> time-60.0
        }
    }
}