package me.kqn.cook

import me.kqn.cook.files.Recipes
import me.kqn.cook.holo.HoloDisplay
import me.kqn.cook.holo.HoloGramDsiplay
import me.kqn.cook.holo.HolographDisplay
import org.bukkit.Bukkit
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
import taboolib.platform.BukkitPlugin

object Cookery : Plugin() {
    lateinit var holoDisplay:HoloGramDsiplay
    override fun onEnable() {
        Recipes.read()
        setupPlayerDatabase(newFile(getDataFolder(), "data.db"))
        holoDisplay= HolographDisplay(BukkitPlugin.getInstance())
        regcmd()

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
                        sender.sendMessage("���飺${player.getDataContainer()["exp"]}")
                        sender.sendMessage("�ȼ�:${player.getDataContainer()["level"]}")
                    }
                }
            }
        }
    }
    @SubscribeEvent
    fun setupData(e: PlayerJoinEvent) {
        // ��ʼ���������

        e.player.setupDataContainer()

    }
    @SubscribeEvent
    fun releaseDAta(e: PlayerQuitEvent) {
        // �ͷ������������
        e.player.releaseDataContainer()
    }

}