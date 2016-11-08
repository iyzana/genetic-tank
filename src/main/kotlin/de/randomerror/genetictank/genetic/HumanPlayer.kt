package de.randomerror.genetictank.genetic

import de.randomerror.genetictank.entities.Tank
import de.randomerror.genetictank.input.Keyboard.keyDown

/**
 * Created by henri on 01.11.16.
 */
class HumanPlayer : Player {
    override fun update(deltaTime: Double, body: Tank) {}

    override fun forward() = keyDown("w")

    override fun backward() = keyDown("s")

    override fun turnRight() = keyDown("d")

    override fun turnLeft() = keyDown("a")

    override fun shoot() = keyDown("m", once = true)

}