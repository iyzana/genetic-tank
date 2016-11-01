package de.randomerror.genetictank.labyrinth

import de.randomerror.genetictank.helper.shuffled
import java.util.*

/**
 * Created by Jannis on 01.11.16.
 */
object LabyrinthGenerator {
    private val up = Point(0, -2)
    private val down = Point(0, 2)
    private val left = Point(-2, 0)
    private val right = Point(2, 0)

    fun generate(width: Int, height: Int, random: Random = Random()): Labyrinth {
        val labyrinth = Labyrinth(width * 2 + 1, height * 2 + 1)

        val start = randomPoint(width, height, random)
        generate(start, labyrinth, random)

        return labyrinth
    }

    private fun randomPoint(w: Int, h: Int, random: Random) = Point(random.nextInt((w - 1) / 2) * 2 + 1, random.nextInt((h - 1) / 2) * 2 + 1)

    private fun generate(current: Point, labyrinth: Labyrinth, random: Random) {
        labyrinth.setPathAt(current)

        listOf(up, down, left, right)
                .map { current + it }
                .filter { it in labyrinth }
                .shuffled(random)
                .forEach { next ->
                    if (labyrinth.isWall(next)) {
                        labyrinth.setPathAt((current + next) / 2)

                        generate(next, labyrinth, random)
                    } else if (random.nextDouble() < 0.2) { // allow break through walls occasionally
                        // ensure the breakthrough does not introduce a mini circle or a single dot wall
                        val move = next - current
                        val sideR = current + move.rotatedRight() + move / 2
                        val sideL = current + move.rotatedLeft() + move / 2
                        val valid = (sideR !in labyrinth || labyrinth.isWall(sideR))
                                && (sideL !in labyrinth || labyrinth.isWall(sideL))

                        if (valid) {
                            labyrinth.setPathAt((current + next) / 2)
                            labyrinth.setPathAt(next)
                        }
                    }
                }
    }

    fun print(labyrinth: Array<Array<Boolean>>) {
        (0..labyrinth.lastIndex).forEach { x ->
            (0..labyrinth[x].lastIndex).forEach { y ->
                print(if (labyrinth[x][y]) "# " else ". ")
            }
            println()
        }
    }
}