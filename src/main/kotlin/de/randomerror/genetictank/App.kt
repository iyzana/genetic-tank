package de.randomerror.genetictank

import de.randomerror.genetictank.helper.Matrix
import de.randomerror.genetictank.helper.log
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
            
            scene.widthProperty().addListener { observable, oldValue, newValue -> canvas.width = newValue.toDouble() }
            scene.heightProperty().addListener { observable, oldValue, newValue -> canvas.height = newValue.toDouble() }
            
            addInputListeners(scene)
        }
        
        GameLoop(canvas).start()

        stage.show()
    }

    private fun addInputListeners(scene: Scene) {
        scene.onMouseMoved = Mouse
        scene.onMouseDragged = Mouse
        scene.onMousePressed = Mouse
        scene.onMouseReleased = Mouse
        scene.onKeyPressed = Keyboard
        scene.onKeyReleased = Keyboard
    }
}

fun main(args: Array<String>) {
    log.info("Application start")

    launch(App::class.java)
}