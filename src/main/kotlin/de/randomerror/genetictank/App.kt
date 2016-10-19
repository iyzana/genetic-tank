package de.randomerror.genetictank

import javafx.application.Application
import javafx.application.Application.launch
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
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
        
        stage.show()
    }
}

fun main(args: Array<String>) {
    launch(App::class.java)
}