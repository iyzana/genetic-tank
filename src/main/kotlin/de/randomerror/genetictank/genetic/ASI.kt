package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.GameLoop
import de.randomerror.genetictank.entities.Projectile
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.helper.Matrix


/**
 * Created by henri on 01.11.16.
 */
class ASI(val layers: List<Int>) : Player {
    var time = 0.0


    val brain = Brain(layers)
    var stateOfMind = Matrix(1, layers.last(), { i, j -> 0.0 })

    override fun update(deltaTime: Double, body: Tank) {
        val enemy = GameLoop.entities.filter { it != body && it is Tank }.first() as Tank

        val idea = Matrix(1, layers.first(), { i, j -> 0.0 })

        idea.data[0][0] = time
        idea.data[0][1] = enemy.x - body.x
        idea.data[0][2] = enemy.y - body.y
        idea.data[0][3] = enemy.heading
        idea.data[0][4] = body.heading

        val bullets = GameLoop.entities.filter { it is Projectile }.take(10).forEachIndexed { i, entity ->
            idea.data[0][4 * i + 5] = entity.x - body.x
            idea.data[0][4 * i + 6] = entity.y - body.y
            idea.data[0][4 * i + 7] = entity.velX
            idea.data[0][4 * i + 8] = entity.velY
        }

        val walls = 0

        stateOfMind = brain.thinkAbout(idea)
        time += deltaTime
    }

    override fun forward() = stateOfMind.data[0][0] > 0.5

    override fun backward() = stateOfMind.data[0][1] > 0.5

    override fun turnRight() = stateOfMind.data[0][2] > 0.5

    override fun turnLeft() = stateOfMind.data[0][3] > 0.5

    override fun shoot() = stateOfMind.data[0][4] > 0.5

}