package de.randomerror.genetictank.labyrinth

import de.randomerror.genetictank.entities.Wall
import de.randomerror.genetictank.labyrinth.TileType.PATH
import de.randomerror.genetictank.labyrinth.TileType.WALL

class Labyrinth(val w: Int, val h: Int) {
    private val data = Array(w) { x -> Array(h) { y -> WALL } }
    
    fun asBooleanArray() = data.map { a -> a.map { it == WALL }.toTypedArray() }.toTypedArray()

    fun asWalls(): List<Wall> {
        return (0..w - 1).flatMap { x -> (0..h - 1).map { y -> Point(x, y) } }
                .filter { isWall(it) }
                // filter all walls that are dot-like and would be contained in a larger wall to their right or bottom
                .filter { point ->
                    if (point.x % 2 != 0 || point.y % 2 != 0) return@filter true

                    val rightWall = point + Point(1, 0)
                    val bottomWall = point + Point(0, 1)

                    return@filter !(isWall(rightWall) || isWall(bottomWall))
                }
                .map { point ->
                    val x = point.x / 2 * tileSize
                    val y = point.y / 2 * tileSize
                    val w = if (point.x % 2 == 0) wallSize else tileSize
                    val h = if (point.y % 2 == 0) wallSize else tileSize

                    Wall(x, y, w, h)
                }
    }

    fun getRealSize(): Pair<Double, Double> {
        return w * tileSize + wallSize to h * tileSize + wallSize
    }
    
    internal fun isWall(p: Point) = if (p in this) data[p.x][p.y] == WALL else false

    internal fun setPathAt(p: Point) {
        data[p.x][p.y] = PATH
    }

    internal operator fun contains(point: Point) = point.x >= 0 && point.x < w && point.y >= 0 && point.y < h

    companion object {
        private val tileSize = 100.0
        private val wallSize = tileSize * 0.075
    }
}