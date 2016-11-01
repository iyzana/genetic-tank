package de.randomerror.genetictank.genetic

/**
 * Created by henri on 01.11.16.
 */
interface Player {
    fun forward(): Boolean
    fun backward(): Boolean
    fun turnRight(): Boolean
    fun turnLeft(): Boolean
    fun shoot(): Boolean
}