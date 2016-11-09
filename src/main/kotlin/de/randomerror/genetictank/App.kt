package de.randomerror.genetictank

import de.randomerror.genetictank.helper.log
import de.randomerror.genetictank.input.Keyboard
import de.randomerror.genetictank.input.Mouse
import javafx.animation.AnimationTimer
import javafx.application.Application
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

        val gameLoop = GameLoop(canvas)

        object : AnimationTimer() {
            override fun handle(now: Long) = gameLoop.handle(now)
        }.start()

        stage.show()
    }

    private fun addInputListeners(scene: Scene) {
        scene.setOnMouseMoved { Mouse.handle(it) }
        scene.setOnMouseDragged { Mouse.handle(it) }
        scene.setOnMousePressed { Mouse.handle(it) }
        scene.setOnMouseReleased { Mouse.handle(it) }
        scene.setOnKeyPressed { Keyboard.handle(it) }
        scene.setOnKeyReleased { Keyboard.handle(it) }
    }
}

fun main(args: Array<String>) {
    if (args.isNotEmpty())
        GameLoop.saveInterval = args[0].toInt().coerceAtLeast(1)

    log.info("Application start")

//    Application.launch(App::class.java)

    val gameLoop = GameLoop()
    while (true) {
        gameLoop.handle(System.nanoTime())
    }
}