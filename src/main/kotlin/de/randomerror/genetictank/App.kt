package de.randomerror.genetictank

import de.randomerror.genetictank.input.Keyboard
import de.randomerror.genetictank.input.Mouse
import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.stage.Screen
import javafx.stage.Stage

/**
 * Created by Jannis on 19.10.16.
 */
class App : Application() {
    override fun start(stage: Stage) {
        val screenBounds = Screen.getPrimary().bounds

        val canvas = Canvas(screenBounds.width, screenBounds.height)

        stage.apply {
            title = "Genetic Tank"

            fullScreenExitHint = ""
            isFullScreen = true

            val group = Group()
            group.children += canvas
            scene = Scene(group)
        }

        addInputListeners(canvas)

        GameLoop(canvas).start()

        stage.show()
    }

    private fun addInputListeners(canvas: Canvas) {
        canvas.onMouseMoved = Mouse
        canvas.onMouseDragged = Mouse
        canvas.onMousePressed = Mouse
        canvas.onMouseReleased = Mouse
        canvas.onKeyPressed = Keyboard
        canvas.onKeyReleased = Keyboard
    }
}

fun main(args: Array<String>) {
    launch(App::class.java)
}