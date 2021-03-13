package me.infinity.features.module.player

import me.infinity.features.Module
import me.infinity.features.ModuleInfo
import me.infinity.utils.Helper

@ModuleInfo(name = "Scaffold", desc = "Auto building on walk", key = -2, visible = true, category = Module.Category.PLAYER)
class Scaffold : Module() {

    override fun onPlayerTick() {
        if (!Helper.getUpdateUtil().canUpdate())
            return
        if (Helper.getPlayer().y < 0) {

        }
        super.onPlayerTick()
    }
}