package de.randomerror.genetictank.input

import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.KeyEvent.KEY_PRESSED
import javafx.scene.input.KeyEvent.KEY_TYPED
import java.util.*

/**
 * Created by Jannis on 19.10.16.
 */
object Keyboard : EventHandler<KeyEvent> {
    private enum class KeyState {
        RELEASED,
        PRESSED,
        ONCE
    }

    private val actualState = HashMap<KeyCode, Boolean>()

    private val currentState = HashMap<KeyCode, KeyState>()
    private val typedString = StringBuilder()

    fun poll() {
        typedString.setLength(0)

        actualState.forEach { key, pressed ->
            if (pressed) {
                if (currentState[key] == KeyState.RELEASED)
                    currentState[key] = KeyState.ONCE
                else
                    currentState[key] = KeyState.PRESSED
            } else
                currentState[key] = KeyState.RELEASED
        }
    }

    fun keyDown(code: KeyCode, once: Boolean = false): Boolean {
        val keyState = currentState[code] ?: KeyState.RELEASED

        return if (once) keyState == KeyState.ONCE
        else keyState != KeyState.RELEASED;
    }

    fun ctrl() = keyDown(KeyCode.CONTROL)

    fun alt() = keyDown(KeyCode.ALT)

    fun shift() = keyDown(KeyCode.SHIFT)

    fun typedString() = typedString.toString()

    override fun handle(event: KeyEvent) {
        if (event.eventType == KEY_TYPED) {
            typedString += event.character
        } else {
            actualState[event.code] = event.eventType == KEY_PRESSED
        }
    }

    operator fun StringBuilder.plusAssign(string: String) {
        this.append(string)
    }
}