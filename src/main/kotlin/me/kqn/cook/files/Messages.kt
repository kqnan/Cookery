package me.kqn.cook.files

import org.bukkit.entity.Player
import taboolib.common.platform.function.submitAsync
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration
import java.io.File

object Messages {
    private val path="plugins/Cookery/message.yml"
    @Config(value = "message.yml", autoReload = true)
    lateinit var message:Configuration

    @ConfigNode(value="prefix",bind="message.yml")
    lateinit var prefix:String
    @ConfigNode(value="fuel_not_enough",bind="message.yml")
    lateinit var fuel_not_enough:String
    @ConfigNode(value="cooking",bind="message.yml")
    lateinit var cooking:String
    fun  save(){
        submitAsync {
            message.saveToFile(File(path))
        }
    }

}