package me.kqn.cook.menu

import me.kqn.cook.files.Configs
import me.kqn.cook.files.Messages
import me.kqn.cook.files.Recipes
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.asList
import taboolib.expansion.getDataContainer
import taboolib.library.xseries.getItemStack
import taboolib.module.chat.colored
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.ItemBuilder

object RecipeMenu {
    private fun parseUnlockList(player: Player):LinkedHashSet<String>{
        val res=LinkedHashSet<String>()
        res.addAll(Configs.getUnlockList(player.getDataContainer()["level"]?.toIntOrNull()?:1))
        val tmp=player.getDataContainer()["unlock"]?.split(":")?:return res
        res.addAll(tmp)
        return res
    }
    fun open(player:Player){

        val unlocklist= parseUnlockList(player)

        player.openMenu<Linked<Recipes.Recipe>>(title=Messages.recipe.colored()) {
            rows(6)
            elements {
                return@elements Recipes.rcp
            }
            // 下一页位置以及物品
            setNextPage(53) { page, hasNextPage ->

                return@setNextPage  ItemBuilder(Material.ARROW).also {


                    it.name=Messages.next_page.colored()
                }.build()
            }
            // 上一页位置以及物品
            setPreviousPage(45) { page, hasPreviousPage ->

                return@setPreviousPage ItemBuilder(Material.ARROW).also {
                    it.name=Messages.previous_page.colored()
                }.build()
            }
            set(49,ItemBuilder(Material.SKULL_ITEM).also {
                it.name=Messages.info.colored()
                it.lore.addAll(listOf("${Messages.exp}：&7${player.getDataContainer()["exp"]?.toIntOrNull()?:0}".colored(),"" +
                        "${Messages.level}：&7${player.getDataContainer()["level"]?.toIntOrNull()?:1}".colored()))
            }.build()){
                this.isCancelled=true
            }
            slots(generateSequence(0) { if((it + 1) < 45)it+1 else null }.toList())
            onGenerate(async = true){player, element, index, slot ->
                ItemBuilder(element.reward_item?:return@onGenerate ItemStack(Material.BEDROCK))
                    .also { it.lore.clear()
                        if(unlocklist.contains(element.key)){
                            it.lore.add("${Messages.recipe}：".colored())
                            element.gradients.forEach { its->it.lore.add(" &7- ${its.itemMeta.displayName}".colored()) }
                            it.lore.add(Messages.unlocked.colored())
                            it.lore.add(Messages.level_required.replace("%required_level%",element.require_level.toString()).colored())
                        }
                        else {
                            val unlockitem=Configs.config.getItemStack("menu.unlock")
                            it.name=unlockitem?.itemMeta?.displayName?:"&7???"
                            it.material=unlockitem?.type?:Material.PAPER
                            it.lore.clear()
                            it.lore.addAll(unlockitem?.itemMeta?.lore?:"&f你未做过这个菜".asList())
                        }
                    }
                    .build()
            }
        }
    }
}