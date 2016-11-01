package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.entities.Tank

/**
 * Created by henri on 01.11.16.
 */
interface Player {
    fun update(deltaTime: Double, body: Tank)

    fun forward(): Boolean
    fun backward(): Boolean
    fun turnRight(): Boolean
    fun turnLeft(): Boolean
    fun shoot(): Boolean
}