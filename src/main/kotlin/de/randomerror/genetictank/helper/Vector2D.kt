package de.randomerror.genetictank.helper

import java.awt.geom.Point2D

/**
 * Created by henri on 05.11.16.
 */
data class Vector2D(val x: Double, val y: Double) {

    constructor(point: Point2D.Double) : this(point.x, point.y)

    operator fun times(other: Vector2D) = x * other.x + y * other.y
    operator fun times(other: Point2D.Double) = x * other.x + y * other.y
    operator fun times(other: Double) = Vector2D(x * other, y * other)

    operator fun minus(other: Vector2D) = Vector2D(x - other.x, y - other.y)

    operator fun plus(other: Vector2D) = Vector2D(x + other.x, y + other.y)

    fun toPoint2D() = Point2D.Double(x, y)

    fun unitVector(): Vector2D {
        val length = Math.sqrt(x * x + y * y)
        return Vector2D(x / length, y / length)
    }
}