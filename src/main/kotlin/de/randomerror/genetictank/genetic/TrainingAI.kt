package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.entities.Tank

/**
 * Created by henri on 01.11.16.
 */
class TrainingAI : Player {
    var shoot = false

    override fun update(deltaTime: Double, body: Tank) {
        val enemy = body.entities.filter { it != body && it is Tank }.first()

        calcIfShoot(body, enemy)
    }

    private fun calcIfShoot(body: Tank, enemy: Entity) {
        shoot = Math.abs(Math.atan2(body.y - enemy.y, body.x - enemy.x) - body.heading) < 1.0
    }

    override fun forward() = Math.random() < 0.1

    override fun backward() = false

    override fun turnRight() = false

    override fun turnLeft() = Math.random() < 0.1

    override fun shoot() = Math.random() < 0.03

}