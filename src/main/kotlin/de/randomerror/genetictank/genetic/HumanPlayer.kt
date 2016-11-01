package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.input.Keyboard
import de.randomerror.genetictank.input.Keyboard.keyDown
import javafx.scene.input.KeyCode

/**
 * Created by henri on 01.11.16.
 */
class HumanPlayer : Player {
    override fun update(deltaTime: Double, body: Tank) {}

    override fun forward() = keyDown(KeyCode.W)

    override fun backward() = keyDown(KeyCode.S)

    override fun turnRight() = keyDown(KeyCode.D)

    override fun turnLeft() = keyDown(KeyCode.A)

    override fun shoot() = keyDown(KeyCode.M, once = true)

}