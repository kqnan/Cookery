package me.kqn.cook

import me.kqn.cook.files.Recipes
import me.kqn.cook.holo.HoloDisplay
import me.kqn.cook.holo.HoloGramDsiplay
import me.kqn.cook.holo.HolographDisplay
import me.kqn.cook.integrate.Protection
import me.kqn.cook.integrate.Residence
import me.kqn.cook.integrate.WorldGuard
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.io.newFile
import taboolib.common.platform.Plugin
import taboolib.common.platform.command.command
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.expansion.getDataContainer
import taboolib.expansion.releaseDataContainer
import taboolib.expansion.setupDataContainer
import taboolib.expansion.setupPlayerDatabase
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.giveItem

object Cookery : Plugin(),Protection {
    lateinit var holoDisplay:HoloGramDsiplay
    private var protections=ArrayList<Protection>()
    override fun onLoad() {

    }
    override fun onEnable() {
        Recipes.read()
        setupPlayerDatabase(newFile(getDataFolder(), "data.db"))
        holoDisplay= HolographDisplay(BukkitPlugin.getInstance())
        regcmd()
        integration()
    }

    private fun integration(){
        if (Bukkit.getPluginManager().isPluginEnabled("Residence")){
            protections.add(Residence())
        }
        if(Bukkit.getPluginManager().isPluginEnabled("WorldGuard")){
            protections.add(WorldGuard)
        }
    }

    fun regcmd(){

        command("Cookery", aliases = listOf("cook")){
            literal("testholo"){
                dynamic { execute<Player>{sender, context, argument -> holoDisplay.addholo(sender.location, listOf(argument))  } }
            }
            literal("clearholo"){
                execute<Player>{sender, context, argument -> holoDisplay.clear() }
            }
            literal("info"){
                dynamic {
                    execute<CommandSender>{sender, context, argument ->
                        var player= Bukkit.getPlayerExact(argument)
                        sender.sendMessage("经验：${player.getDataContainer()["exp"]}")
                        sender.sendMessage("等级:${player.getDataContainer()["level"]}")
                    }
                }
            }
            literal("gradients"){
                execute<Player>{sender, context, argument ->
                    for (recipe in Recipes.rcp) {
                        sender.giveItem(recipe.gradients)
                    }
                }
            }
        }
    }
    @SubscribeEvent
    fun setupData(e: PlayerJoinEvent) {
        // 初始化玩家容器

        e.player.setupDataContainer()

    }
    @SubscribeEvent
    fun releaseDAta(e: PlayerQuitEvent) {
        // 释放玩家容器缓存
        e.player.releaseDataContainer()
    }

    override fun allowOpen(loc: Location, player: Player): Boolean {
        if(player.isOp)return true
       this.protections.forEach { if(!it.allowOpen(loc,player))return  false }
        return  true
    }

    override fun allowAddFuel(loc: Location, player: Player): Boolean {
        if(player.isOp)return true
        this.protections.forEach { if(!it.allowAddFuel(loc,player)) return false }
        return true
    }

}