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

class RotatedRectangle(val x: Double, val y: Double, val width: Double, val height: Double, var rotation: Double) {
    val area: Area = Area(Rectangle2D.Double(x, y, width, height)).run {
        createTransformedArea(AffineTransform.getRotateInstance(rotation, bounds.centerX, bounds.centerY))
    }
    
    val outline: Rectangle
        get() {
            val bounds = area.bounds2D
            return Rectangle(bounds.x, bounds.y, bounds.width, bounds.height)
        }

    fun collidesWith(x: Double, y: Double) = area.contains(x, y)

    fun collidesWithRect(other: Rectangle): Boolean {
        return Area(area).run {
            intersect(other.area)
            !isEmpty
        }
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