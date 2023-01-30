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
    @ConfigNode(value = "fuel_added", bind = "message.yml")
    lateinit var fuel_added:String
    @ConfigNode(value="fuel_not_enough",bind="message.yml")
    lateinit var fuel_not_enough:String
    @ConfigNode(value="cooking",bind="message.yml")
    lateinit var cooking:String
    @ConfigNode(value = "level_not_enough", bind = "message.yml")
    lateinit var level_not_enough:String
    @ConfigNode(value = "failed", bind = "message.yml")
    lateinit var failed:String
    @ConfigNode(value = "success", bind = "message.yml")
    lateinit var success:String

    @ConfigNode(value = "cooking_mode", bind = "message.yml")
    lateinit var cooking_mode:String
    @ConfigNode(value = "fuel", bind = "message.yml")
    lateinit var  fuel:String
    @ConfigNode(value = "cooking_hd", bind = "message.yml")
    lateinit var  cooking_hd:String
    @ConfigNode(value = "remain_time_hd", bind = "message.yml")
    lateinit var remain_time_hd:String
    @ConfigNode(value = "seconds", bind = "message.yml")
    lateinit var seconds:String
    @ConfigNode(value = "success_chance", bind = "message.yml")
    lateinit var  success_chance:String
    @ConfigNode(value = "recipe", bind = "message.yml")
    lateinit var  recipe:String
    @ConfigNode(value = "next_page", bind = "message.yml")
    lateinit var next_page:String
    @ConfigNode(value = "previous_page", bind = "message.yml")
    lateinit var previous_page:String
    @ConfigNode(value = "info", bind = "message.yml")
    lateinit var info:String
    @ConfigNode(value = "exp", bind = "message.yml")
    lateinit var exp:String
    @ConfigNode(value = "level", bind = "message.yml")
    lateinit var level:String
    @ConfigNode(value = "unlocked", bind = "message.yml")
    lateinit var   unlocked:String
    @ConfigNode(value = "level_required", bind = "message.yml")
    lateinit var   level_required:String
    @ConfigNode(value = "cookery_system", bind = "message.yml")
    lateinit var cookery_system:String
    @ConfigNode(value = "command_menu", bind = "message.yml")
    lateinit var  command_menu:String
    @ConfigNode(value = "command_info", bind = "message.yml")
    lateinit var command_info:String
    @ConfigNode(value = "command_gradients", bind = "message.yml")
    lateinit var command_gradients:String




    fun  save(){
        submitAsync {
            message.saveToFile(File(path))
        }
    }

}