package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.entities.Projectile
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.helper.Matrix
import de.randomerror.genetictank.labyrinth.Point


/**
 * Created by henri on 01.11.16.
 */
class ASI(val layers: List<Int>) : Player {
    var time = 0.0
    
    val brain = Brain(layers)
    var stateOfMind = Matrix(1, layers.last(), { i, j -> 0.0 })

    override fun update(deltaTime: Double, body: Tank) {
        val enemy = body.entities.filter { it != body && it is Tank }.first() as Tank

        val idea = Matrix(1, layers.first(), { i, j -> 0.0 })

        idea[0] = time
        idea[1] = enemy.x - body.x
        idea[2] = enemy.y - body.y
        idea[3] = enemy.heading
        idea[4] = body.heading
        
        val bullets = body.entities.filter { it is Projectile }.take(10).forEachIndexed { i, entity ->
            idea[4 * i + 5] = entity.x - body.x
            idea[4 * i + 6] = entity.y - body.y
            idea[4 * i + 7] = entity.velX
            idea[4 * i + 8] = entity.velY
        }
        
        val (tileW, tileH) = body.labyrinth.getTileSize()
        
        val tileX = ((body.x + body.width / 2) / tileW).toInt() * 2 + 1
        val tileY = ((body.y + body.height / 2) / tileH).toInt() * 2 + 1

        val xPositions = (tileX - 2 until tileX + 2 step 2).toList()
        val yPositions = (tileY - 2 until tileY + 2 step 2).toList()
        xPositions.forEachIndexed { ix, dx ->
            yPositions.forEachIndexed { iy, dy ->
                directions.forEachIndexed { id, d ->
                    val pos = Point(dx, dy) + d
                    idea[(ix * yPositions.size + iy) * 4 + id + 9] = if(!body.labyrinth.isPath(pos)) 1.0 else 0.0
                }
            }
        }
        
        stateOfMind = brain.thinkAbout(idea)
        time += deltaTime
    }

    override fun forward() = stateOfMind[0] > 0.5

    override fun backward() = stateOfMind[1] > 0.5

    override fun turnRight() = stateOfMind[2] > 0.5

    override fun turnLeft() = stateOfMind[3] > 0.5

    override fun shoot() = stateOfMind[4] > 0.5

    companion object {
        private val directions = listOf(Point(0, -1), Point(1, 0), Point(0, 1), Point(-1, 0))
    }
}
