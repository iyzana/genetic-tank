package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.labyrinth.Labyrinth
import java.util.*

/**
 * Created by henri on 01.11.16.
 */

object Trainer {

    val entities: List<Entity>

    val pokemons = LinkedList<ASI>()
    val numPokemons = 1000

    init {
        (0 until numPokemons).forEach {
            pokemons += ASI(listOf(47, 15, 5))
        }
        entities =  Labyrinth.generate(10,10)
    }

    fun update() {
        entities.forEach { it.update(0.016) }
    }

    fun evolve() {
        fight()
        fitness()
    }

    fun fight() {

    }

    fun fitness() {

    }

}