package de.randomerror.genetictank.labyrinth

import de.randomerror.genetictank.entities.Wall
import java.util.*

object Labyrinth {
    fun generate(width: Int, height: Int, random: Random = Random()): List<Wall> {
        val grid = Grid(width * 2 + 1, height * 2 + 1, random)

        val start = grid.randomPoint()
        generate(start, grid, random)

        return convertToWalls(grid)
    }

    fun print(labyrinth: Array<Array<Boolean>>) {
        (0..labyrinth.lastIndex).forEach { x ->
            (0..labyrinth[x].lastIndex).forEach { y ->
                print(if (labyrinth[x][y]) "# " else ". ")
            }
            println()
        }
    }

    private fun generate(current: Point, grid: Grid, random: Random) {
        grid.setPathAt(current)

        listOf(up, down, left, right)
                .map { current + it }
                .filter { it in grid }
                .shuffled(random)
                .asSequence()
                .forEach { next ->
                    if (grid.isWall(next)) {
                        grid.setPathAt((current + next) / 2)

                        generate(next, grid, random)
                    } else if (random.nextDouble() < 0.2) { // allow break through walls occasionally
                        // ensure the breakthrough does not introduce a mini circle or a single dot wall
                        val move = next - current
                        val sideR = current + move.rotatedRight() + move / 2
                        val sideL = current + move.rotatedLeft() + move / 2
                        val valid = (sideR !in grid || grid.isWall(sideR))
                                && (sideL !in grid || grid.isWall(sideL))

                        if (valid) {
                            grid.setPathAt((current + next) / 2)
                            grid.setPathAt(next)
                        }
                    }
                }
    }

    private fun convertToWalls(grid: Grid): List<Wall> {

        return (0..grid.w - 1).flatMap { x -> (0..grid.h - 1).map { y -> Point(x, y) } }
                .filter { grid.isWall(it) }
                .filter { it.x % 2 == 0 || it.y % 2 == 0 }
                .map { point ->
                    val x = point.x / 2 * tileSize
                    val y = point.y / 2 * tileSize
                    val w = if (point.x % 2 == 0) wallSize else tileSize
                    val h = if (point.y % 2 == 0) wallSize else tileSize

                    Wall(x, y, w, h)
                }
    }

    fun getRealSize(width: Int, height: Int): Pair<Double, Double> {
        return width * tileSize + wallSize to height * tileSize + wallSize
    }

    private val tileSize = 100.0
    private val wallSize = tileSize * 0.075

    private val up = Point(0, -2)
    private val down = Point(0, 2)
    private val left = Point(-2, 0)
    private val right = Point(2, 0)

    private fun <T> List<T>.shuffled(random: Random = Random()) = apply { Collections.shuffle(this, random) }
}