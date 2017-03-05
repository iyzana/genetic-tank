package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.entities.Projectile
import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.helper.Matrix
import de.randomerror.genetictank.labyrinth.Point
import java.io.Serializable
import java.lang.Math.atan2
import java.lang.Math.sqrt


/**
 * Created by henri on 01.11.16.
 */
class ASI(val layers: List<Int> = listOf(83, 83, 40, 5)) : Player, Serializable {
    var time = 0.0
    var fitness = 0.0
    var copyCount = 0

    lateinit var idea: Matrix
    var brain = Brain(layers)
    var stateOfMind = Matrix(1, layers.last(), { i, j -> 0.0 })

    override fun update(deltaTime: Double, body: Tank) {
        sense(body)

        stateOfMind = brain.thinkAbout(idea)
        time += deltaTime
    }

    private fun sense(body: Tank) {
        idea = Matrix(1, layers.first()) { i, j -> 0.0 }

        var index = 0
        val enemy = body.entities.filter { it != body && it is Tank }.first() as Tank

        idea[index++] = time
        val dx = enemy.x - body.x
        val dy = body.y - enemy.y
        idea[index++] = normalizeHeading(atan2(dx, dy) - body.heading)
        idea[index++] = sqrt(dx * dx + dy * dy)
        idea[index++] = normalizeHeading(enemy.heading)
        idea[index++] = .0

        val projectiles = body.entities.asSequence().filter { it is Projectile }.take(10).toList()
        projectiles.forEach { entity ->
            idea[index++] = entity.x - body.x
            idea[index++] = entity.y - body.y
            idea[index++] = entity.velX
            idea[index++] = entity.velY
        }
        index += (10 - projectiles.size) * 4

        val (tileW, tileH) = body.labyrinth.getTileSize()

        val tileX = ((body.x + body.width / 2) / tileW).toInt() * 2 + 1
        val tileY = ((body.y + body.height / 2) / tileH).toInt() * 2 + 1

        val xPositions = ((tileX - 2)..(tileX + 2) step 2).toList()
        val yPositions = ((tileY - 2)..(tileY + 2) step 2).toList()
        xPositions.forEachIndexed { ix, dx ->
            yPositions.forEachIndexed { iy, dy ->
                directions.forEachIndexed { id, d ->
                    val pos = Point(dx, dy) + d
                    idea[index++] = if (!body.labyrinth.isPath(pos)) 1.0 else 0.0
                }
            }
        }

        idea[index++] = if (body.collidesX) 1.0 else 0.0
        idea[index++] = if (body.collidesY) 1.0 else 0.0
    }

    private fun normalizeHeading(heading: Double): Double {
        val mod = heading % (2 * Math.PI)
        return if (mod < 0) mod + (2 * Math.PI) else mod
    }

    override fun forward() = stateOfMind[0] > 0.5

    override fun backward() = stateOfMind[1] > 0.5

    override fun turnRight() = stateOfMind[2] > 0.5

    override fun turnLeft() = stateOfMind[3] > 0.5

    override fun shoot() = stateOfMind[4] > 0.5

    fun copy(): ASI {
        val asi = ASI(layers)
        asi.fitness = fitness
        asi.brain = brain.copy()
        asi.copyCount = copyCount + 1
        return asi
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ASI) return false

        if (brain != other.brain) return false

        return true
    }

    override fun hashCode(): Int {
        return brain.hashCode()
    }

    companion object {
        private val directions = listOf(Point(0, -1), Point(1, 0), Point(0, 1), Point(-1, 0))
    }
}
