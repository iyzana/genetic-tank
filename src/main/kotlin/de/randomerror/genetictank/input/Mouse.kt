package de.randomerror.genetictank.input

import javafx.event.EventHandler
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import java.awt.geom.Point2D
import java.util.*


/**
 * Created by Jannis on 19.10.16.
 */
object Mouse : EventHandler<MouseEvent> {
    private enum class ButtonState {
        UP,
        RELEASED,
        DOWN,
        PRESSED
    }

    private val actualState = HashMap<MouseButton, Boolean>()
    private var actualPosition = Point2D.Double()

    private val currentState = HashMap<MouseButton, ButtonState>()
    private val currentPosition = Point2D.Double()

    var scale = 1.0

    fun poll() {
        actualState.forEach { button, pressed ->
            if (pressed) {
                currentState[button] = if (isUp(button)) ButtonState.PRESSED else ButtonState.DOWN
            } else {
                currentState[button] = if (isDown(button)) ButtonState.RELEASED else ButtonState.UP
            }
        }

        currentPosition.x = actualPosition.x / scale
        currentPosition.y = actualPosition.y / scale
    }

    fun isDown(button: MouseButton = MouseButton.PRIMARY, once: Boolean = false): Boolean {
        return currentState[button] == ButtonState.PRESSED || !once && currentState[button] == ButtonState.DOWN
    }

    fun isUp(button: MouseButton = MouseButton.PRIMARY, once: Boolean = false): Boolean {
        return !currentState.containsKey(button) || currentState[button] == ButtonState.RELEASED || !once && currentState[button] == ButtonState.UP
    }

    fun getPos() = currentPosition

    fun getX() = currentPosition.x

    fun getY() = currentPosition.y

    override fun handle(event: MouseEvent) {
        when (event.eventType) {
            MouseEvent.MOUSE_PRESSED -> actualState[event.button] = true
            MouseEvent.MOUSE_RELEASED -> actualState[event.button] = false
            MouseEvent.MOUSE_MOVED -> actualPosition = Point2D.Double(event.x, event.y)
            MouseEvent.MOUSE_DRAGGED -> actualPosition = Point2D.Double(event.x, event.y)
        }
    }
}