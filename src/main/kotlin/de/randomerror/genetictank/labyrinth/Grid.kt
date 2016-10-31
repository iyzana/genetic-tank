package de.randomerror.genetictank.labyrinth

import java.util.*

internal class Grid(val w: Int, val h: Int, val random: Random) {
    enum class Type {
        WALL,
        PATH
    }
    
    val data = Array(w) { x -> Array(h) { y -> Type.WALL } }

    fun isWall(p: Point) = if(p in this) data[p.x][p.y] == Type.WALL else false

    fun isPath(p: Point) = if(p in this) data[p.x][p.y] == Type.PATH else false

    fun setPathAt(p: Point) {
        data[p.x][p.y] = Type.PATH
    }

    fun randomPoint() = Point(random.nextInt((w - 1) / 2) * 2 + 1, random.nextInt((h - 1) / 2) * 2 + 1)

    operator fun contains(point: Point) = point.x >= 0 && point.x < w && point.y >= 0 && point.y < h
    
    fun asBooleanArray() = data.map { a -> a.map { it == Grid.Type.WALL }.toTypedArray() }.toTypedArray()
}