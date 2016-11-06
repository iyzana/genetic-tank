package de.randomerror.genetictank.helper

import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.entities.Wall
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D

/**
 * Created by Jannis on 29.10.16.
 */
class Rectangle(val x: Double, val y: Double, val width: Double, val height: Double) {
    val area = Area(Rectangle2D.Double(x, y, width, height))

    fun collidesWith(x: Double, y: Double) = x > this.x && x < this.x + width && y > this.y && y < this.y + height

    fun toRotatedRectangle() = RotatedRectangle(x, y, width, height, 0.0)
}

class RotatedRectangle(val x: Double, val y: Double, val width: Double, val height: Double, val rotation: Double) {

    val centerX = x + width / 2.0
    val centerY = y + height / 2.0
    val transform = AffineTransform.getRotateInstance(rotation, centerX, centerY)
    val vertices = arrayOf(Vector2D(x, y).toPoint2D(), Vector2D(x, y + height).toPoint2D(), Vector2D(x + width, y + height).toPoint2D(), Vector2D(x + width, y).toPoint2D())
    val transformedVertices: List<Vector2D>

    init {
        val dst = Array(4) { Point2D.Double() }
        transform.transform(vertices, 0, dst, 0, 4)
        transformedVertices = dst.map(::Vector2D)
    }

    val area: Area
        get() = Area(Rectangle2D.Double(x, y, width, height)).run {
            createTransformedArea(AffineTransform.getRotateInstance(rotation, bounds.centerX, bounds.centerY))
        }


    val outline: Rectangle
        get() {
            val bounds = area.bounds2D
            return Rectangle(bounds.x, bounds.y, bounds.width, bounds.height)
        }

    fun collidesWith(x: Double, y: Double): Boolean {
        val rotated = transform.inverseTransform(Point2D.Double(x, y), Point2D.Double())

        return (rotated.x > this.x) && (rotated.x < (this.x + width)) && (rotated.y > this.y) && (rotated.y < (this.y + height))
    }

    fun collidesWith(other: RotatedRectangle): Boolean {
        return listOf(transformedVertices[1] - transformedVertices[0],
                transformedVertices[2] - transformedVertices[1],
                other.transformedVertices[1] - other.transformedVertices[0],
                other.transformedVertices[2] - other.transformedVertices[1])
                .all {
                    val axis = it.unitVector()
                    val (min0, max0) = projectToAxis(axis, transformedVertices)
                    val (min1, max1) = projectToAxis(axis, other.transformedVertices)

                    (max0 > min1 && min1 >= min0) || (max1 > min0 && min0 >= min1)
                }
    }

    fun projectToAxis(axis: Vector2D, vertices: List<Vector2D>): Pair<Double, Double> {
        val initProduct = axis * vertices[0]
        return vertices.drop(1).fold(initProduct to initProduct, { last, vertex ->
            val (min, max) = last

            val product = axis * vertex

            Math.min(min, product) to Math.max(max, product)
        })
    }

    fun collisionArea(other: Rectangle): Rectangle {
        return Area(area).run {
            intersect(other.area)
            Rectangle(bounds2D.x, bounds2D.y, bounds2D.width, bounds2D.height)
        }
    }
}

fun Tank.getBounds() = RotatedRectangle(x, y, width, height, heading)
fun Wall.getBounds() = Rectangle(x, y, width, height)