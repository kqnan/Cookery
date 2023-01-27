package me.kqn.cook

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info

object Cookery : Plugin() {

    override fun onEnable() {
        info("Successfully running ExamplePlugin!")
    }
}