package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.entities.Tank

/**
 * Created by Jannis on 05.11.16.
 */
class StillPlayer : Player {
    override fun update(deltaTime: Double, body: Tank) {
    }

    override fun forward() = false

    override fun backward() = false

    override fun turnRight() = false

    override fun turnLeft() = false

    override fun shoot() = false
}