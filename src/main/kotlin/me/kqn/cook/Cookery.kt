package me.kqn.cook

import me.kqn.cook.files.Messages
import me.kqn.cook.files.Recipes
import me.kqn.cook.holo.HoloDisplay
import me.kqn.cook.holo.HoloGramDsiplay
import me.kqn.cook.holo.HolographDisplay
import me.kqn.cook.integrate.Protection
import me.kqn.cook.integrate.Residence
import me.kqn.cook.integrate.WorldGuard
import me.kqn.cook.menu.RecipeMenu
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.io.newFile
import taboolib.common.platform.Plugin
import taboolib.common.platform.command.CommandContext
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.expansion.getDataContainer
import taboolib.expansion.releaseDataContainer
import taboolib.expansion.setupDataContainer
import taboolib.expansion.setupPlayerDatabase
import taboolib.module.chat.colored
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.giveItem

object Cookery : Plugin(),Protection {
    lateinit var holoDisplay:HoloGramDsiplay
    private var protections=ArrayList<Protection>()

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

        command("Cookery", aliases = listOf("cook"), permissionDefault = PermissionDefault.TRUE){
            literal("info",permission="Cookery.command.info"){
                dynamic {
                    execute<CommandSender>{sender, context, argument ->
                        var player= Bukkit.getPlayerExact(argument)
                        sender.sendMessage("${Messages.exp}£º${player?.getDataContainer()?.get("exp") ?:0}")
                        sender.sendMessage("${Messages.level}:${player?.getDataContainer()?.get("level") ?:0}")
                    }
                }
            }
            literal("gradients",permission="Cookery.command.gradients"){
                execute<Player>{sender, context, argument ->
                    for (recipe in Recipes.rcp) {
                        sender.giveItem(recipe.gradients)
                    }
                }
            }
            literal("menu"){
                execute<Player>{
                    sender, context, argument ->
                    RecipeMenu.open(sender)
                }
            }
            execute <Player>{ sender, context, argument ->
                sender.sendMessage(Messages.cookery_system.colored())
                sender.sendMessage(" &f- /cook menu ${Messages.command_menu}".colored())
                if(sender.hasPermission("Cookery.command.info")){
                    sender.sendMessage(" &f- /cook info <Íæ¼ÒÃû> ${Messages.command_info}".colored())
                }
                if(sender.hasPermission("Cookery.command.gradients")){
                    sender.sendMessage(" &f- /cook gradients ${Messages.command_gradients}".colored())
                }



            }
        }
    }
    @SubscribeEvent
    fun setupData(e: PlayerJoinEvent) {
        // ³õÊ¼»¯Íæ¼ÒÈÝÆ÷

        e.player.setupDataContainer()

    }
    @SubscribeEvent
    fun releaseDAta(e: PlayerQuitEvent) {
        // ÊÍ·ÅÍæ¼ÒÈÝÆ÷»º´æ
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