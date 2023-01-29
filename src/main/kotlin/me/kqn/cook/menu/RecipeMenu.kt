package me.kqn.cook.menu

import me.kqn.cook.files.Configs
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

        player.openMenu<Linked<Recipes.Recipe>>(title="食谱") {
            rows(6)
            elements {
                return@elements Recipes.rcp
            }
            // 下一页位置以及物品
            setNextPage(53) { page, hasNextPage ->

                return@setNextPage  ItemBuilder(Material.ARROW).also { it.skullTexture= ItemBuilder.SkullTexture(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2NzJiODJmMGQxZjhjNDBjNTZiNDJkMzY5YWMyOTk0Yzk0ZGE0NzQ5MTAxMGMyY2U0MzAzZTM0NjViOTJhNyJ9fX0="
                )

                    it.name="&f下一页".colored()
                }.build()
            }
            // 上一页位置以及物品
            setPreviousPage(45) { page, hasPreviousPage ->

                return@setPreviousPage ItemBuilder(Material.ARROW).also {
                    it.skullTexture= ItemBuilder.SkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTVlZmQ5Njk3NGMwNDAzZjIyOWNmOTQxODVjZGQwZjcxOTczNjJhY2JkMDMxY2RmNTFmY2M4ZGFmYWM2Yjg1YSJ9fX0=")

                    it.name="&f上一页".colored()
                }.build()
            }
            slots(generateSequence(0) { if((it + 1) < 45)it+1 else null }.toList())
            onGenerate(async = true){player, element, index, slot ->
                ItemBuilder(element.reward_item?:return@onGenerate ItemStack(Material.BEDROCK))
                    .also { it.lore.clear()
                        if(unlocklist.contains(element.key)){
                            it.lore.add("&7配方：".colored())
                            element.gradients.forEach { its->it.lore.add(" &7- ${its.itemMeta.displayName}".colored()) }
                            it.lore.add("&a已解锁".colored())
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