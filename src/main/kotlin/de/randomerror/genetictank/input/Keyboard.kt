package de.randomerror.genetictank.input

import javafx.scene.input.KeyEvent
import javafx.scene.input.KeyEvent.KEY_PRESSED
import javafx.scene.input.KeyEvent.KEY_TYPED
import java.util.*

/**
 * Created by Jannis on 19.10.16.
 */
object Keyboard {
    private enum class KeyState {
        RELEASED,
        PRESSED,
        ONCE
    }

    private val actualState = HashMap<String, Boolean>()

    private val currentState = HashMap<String, KeyState>()
    private val typedString = StringBuilder()

    fun poll() {
        typedString.setLength(0)

        actualState.forEach { key, pressed ->
            if (pressed) {
                if (!currentState.containsKey(key) || currentState[key] == KeyState.RELEASED)
                    currentState[key] = KeyState.ONCE
                else
                    currentState[key] = KeyState.PRESSED
            } else
                currentState[key] = KeyState.RELEASED
        }
    }

    fun keyDown(code: String, once: Boolean = false): Boolean {
        val keyState = currentState[code.toLowerCase()] ?: KeyState.RELEASED

        return if (once) keyState == KeyState.ONCE
        else keyState != KeyState.RELEASED
    }

    fun ctrl() = keyDown("ctrl")

    fun alt() = keyDown("alt")

    fun shift() = keyDown("shift")

    fun typedString() = typedString.toString()

    fun handle(event: KeyEvent) {
        if (event.eventType == KEY_TYPED) {
            typedString += event.character
        } else {
            actualState[event.code.getName().toLowerCase()] = event.eventType == KEY_PRESSED
        }
    }

    operator fun StringBuilder.plusAssign(string: String) {
        this.append(string)
    }
}