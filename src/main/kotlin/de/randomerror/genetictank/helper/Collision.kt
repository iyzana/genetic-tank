package de.randomerror.genetictank.helper

import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.entities.Wall
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Rectangle2D

/**
 * Created by Jannis on 29.10.16.
 */
class Rectangle(val x: Double, val y: Double, val width: Double, val height: Double) {
    val area = Area(Rectangle2D.Double(x, y, width, height))

    fun collidesWith(x: Double, y: Double) = x > this.x && x < this.x + width && y > this.y && y < this.y + height
}

class RotatedRectangle(var x: Double, var y: Double, var width: Double, var height: Double, var rotation: Double) {
    val area: Area
        get() = Area(Rectangle2D.Double(x, y, width, height)).run {
            createTransformedArea(AffineTransform.getRotateInstance(rotation, bounds.centerX, bounds.centerY))
        }
    val outline: Rectangle
        get() = Rectangle(area.bounds2D.x, area.bounds2D.y, area.bounds2D.width, area.bounds2D.height)

    fun collidesWith(x: Double, y: Double) = area.contains(x, y)

    fun collidesWith(other: Rectangle): Boolean {
        return Area(area).run {
            intersect(other.area)
            !isEmpty
        }
    }
}

fun Tank.getBounds() = RotatedRectangle(x, y, width, height, heading)
fun Wall.getBounds() = Rectangle(x, y, width, height)