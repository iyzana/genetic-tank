package de.randomerror.genetictank.labyrinth

internal class Point(val x: Int = 0, val y: Int = 0) {
    operator fun plus(o: Point) = Point(x + o.x, y + o.y)

    operator fun minus(o: Point) = Point(x - o.x, y - o.y)

    operator fun div(v: Int) = Point(x / v, y / v)

    operator fun times(v: Int) = Point(x * v, y * v)

    fun rotatedRight() = Point(y, -x)
    fun rotatedLeft() = Point(-y, x)
}
