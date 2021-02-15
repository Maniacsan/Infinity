package me.infinity.features.movement

import me.infinity.features.Module
import me.infinity.features.ModuleInfo
import me.infinity.utils.Helper
import org.lwjgl.glfw.GLFW

@ModuleInfo(category = Module.Category.COMBAT, desc = "Auto sneaking", key = GLFW.GLFW_KEY_B, name = "Sneak", visible = true)
class Sneak : Module() {

    override fun onDisable() {
        Helper.minecraftClient.options.keySneak.isPressed = false
        super.onDisable()
    }
    
    override fun onPlayerTick() {
        Helper.minecraftClient.options.keySneak.isPressed = true
    }


}