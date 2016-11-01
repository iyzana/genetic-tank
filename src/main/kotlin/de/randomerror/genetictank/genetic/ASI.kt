package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.entities.Entity
import de.randomerror.genetictank.entities.Projectile
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.helper.Matrix


/**
 * Created by henri on 01.11.16.
 */
class ASI(val layers: List<Int>) : Player {

    val brain = Brain(layers)
    var stateOfMind = Matrix(1, 5, { i, j -> 0.0 })

    fun update(time: Double, entities: List<Entity>, self: Tank) {
        val enemy = entities.filter { it != self && it is Tank }.first() as Tank

        val idea = Matrix(1, 47, { i, j -> 0.0 })

        idea.data[0][0] = time
        idea.data[0][1] = enemy.x - self.x
        idea.data[0][2] = enemy.y - self.y
        idea.data[0][3] = enemy.heading
        idea.data[0][4] = self.heading

        val bullets = entities.filter { it is Projectile }.take(10).forEachIndexed { i, entity ->
            idea.data[0][4 * i + 5] = entity.x - self.x
            idea.data[0][4 * i + 6] = entity.y - self.y
            idea.data[0][4 * i + 7] = entity.velX
            idea.data[0][4 * i + 8] = entity.velY
        }

        val walls = 0

        stateOfMind = brain.thinkAbout(idea);
    }

    override fun forward() = stateOfMind.data[0][0] > 0

    override fun backward() = stateOfMind.data[0][1] > 0

    override fun turnRight() = stateOfMind.data[0][2] > 0

    override fun turnLeft() = stateOfMind.data[0][3] > 0

    override fun shoot() = stateOfMind.data[0][4] > 0

}