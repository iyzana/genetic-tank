package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.GameLoop
import de.randomerror.genetictank.entities.Tank

/**
 * Created by henri on 01.11.16.
 */
class TrainingAI : Player {
    var shoot = false

    override fun update(deltaTime: Double, body: Tank) {
        val enemy = GameLoop.entities.filter { it is Tank && it != body }.first()

        if (Math.abs(Math.atan2(body.y - enemy.y, body.x - enemy.x) - body.heading) < 1.0)
            shoot = true
        else
            shoot = false
    }

    override fun forward() = false

    override fun backward() = false

    override fun turnRight() = true

    override fun turnLeft() = false

    override fun shoot() = false

}