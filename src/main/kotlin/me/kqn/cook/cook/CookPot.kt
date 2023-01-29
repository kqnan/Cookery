package me.kqn.cook.cook

import me.kqn.cook.debug
import me.kqn.cook.files.Configs
import me.kqn.cook.files.Messages
import me.kqn.cook.files.Recipes
import me.kqn.cook.isGradient
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptLocation
import taboolib.common.util.asList
import taboolib.expansion.getDataContainer
import taboolib.module.chat.colored


import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.isNotAir
import taboolib.platform.util.isRightClickBlock
import java.util.Collections
import java.util.stream.Collectors

object CookPot {
    val currentPots=HashMap<Location,Pot>()
    @SubscribeEvent
    fun clickPot(e:PlayerInteractEvent){
        if(e.isRightClickBlock()){
            if(e.clickedBlock.type== Material.CAULDRON){
                if(!currentPots.containsKey(e.clickedBlock.location)|| currentPots.get(e.clickedBlock.location)!!.state==Pot.State.WAITING){
                    e.player.openMenu<Basic>(title = "&a烹饪模式".colored()){
                        var loc=e.clickedBlock.location.clone()
                        rows(2)
                        var idx=2

                        for (mode in Configs.getModes()) {
                            set(idx++,ItemBuilder(Material.WOOL).also { it.name=mode.second.colored() }.build()){
                                this.clicker.closeInventory()
                                //获取锅中物品实体
                                val items=loc.world.getNearbyEntities(loc.clone(),1.0,1.0,1.0).filter { return@filter it.type==EntityType.DROPPED_ITEM } as List<org.bukkit.entity.Item>
                                //看看这一堆实体中，匹配了哪个配方
                                val playerlevel=this.clicker.getDataContainer()["level"]?.toIntOrNull()?:1

                                var recipe:Recipes.Recipe?=null
                                for (rcp in Recipes.rcp) {
                                    if(matchRecipe(mode.first,items.map { it.itemStack },rcp)&&playerlevel>=rcp.require_level){
                                        recipe=rcp
                                        break
                                    }
                                }
                                //如果一个配方都没匹配到，就退出
                                recipe?:return@set
                                //如果匹配到配方，按照配方来把地上的物品移除
                                    //把剩余的物品分类
                                val remainitems=items.map { it.itemStack }.filter { it.isNotAir() }.groupBy { it.itemMeta.hashCode() }
                                    //把每一类压缩成单个物品,压缩到[0]位置的物品中
                                for (remainitem in remainitems) {
                                    var cnt=0
                                    remainitem.value.forEach { cnt+=it.amount }
                                    remainitem.value[0].amount=cnt
                                }
                                val rcpitems=recipe.gradients.groupBy { it.itemMeta.hashCode() }
                                    //对于配方中每一类的物品
                                for (rcpitem in rcpitems) {
                                    //计算这一类物品的总数量
                                    var cnt=0
                                    rcpitem.value.forEach { cnt+=it.amount }
                                    //从remainitems中找到这一类的物品，并减去数量
                                    for (remainitem in remainitems) {
                                        if(remainitem.value[0].isGradient(rcpitem.value[0])){
                                            if(remainitem.value[0].amount<=cnt){
                                                remainitem.value[0].type=Material.AIR
                                            }
                                            else {
                                                remainitem.value[0].amount-=cnt
                                            }
                                        }
                                    }
                                }
                                //移除全部物品
                                items.forEach { it.remove() }
                                //把剩余物品重新生成出来
                                for (remainitem in remainitems) {
                                    if(remainitem.value[0].isNotAir()){
                                        loc.world.dropItem(loc,remainitem.value[0])
                                    }
                                }


                                var pot=Pot(loc,this.clicker,recipe)
                                pot.gradient=recipe.gradients
                                pot.mode=mode.first
                                pot.cook()
                            }
                        }
                    }
                }
                else {
                    e.player.sendMessage("${Messages.prefix}${Messages.cooking}".colored())
                }
            }
        }
    }

    //判断这一堆原料中是否由符合一个配方的
    fun matchRecipe(cook_type:String,gradient:List<ItemStack>,recipe:Recipes.Recipe):Boolean{
            if(recipe.require_type!=cook_type)return  false
            val cfg_group=recipe.gradients.groupBy {it.itemMeta.hashCode()}
            var pot_group=gradient.groupBy { it.itemMeta.hashCode() }
            for (entry in cfg_group) {
                //判断这个物品是否在锅中
                var res=false
                var en:Map.Entry<Int,List<ItemStack>>?=null
                for (entry2 in pot_group) {
                    if(entry2.value[0].isGradient(entry.value[0])){
                        res=true
                        en=entry2
                        break
                    }
                }
                if(!res)return false
                //判断锅中物品数量是否足够
                en!!
                var need=0
                entry.value.forEach { need+=it.amount }
                var pot_has=0
                en.value.forEach { pot_has+=it.amount }
                if(pot_has<need)return  false
            }
            return  true
    }
   private fun isRecipeItem(item:ItemStack):Boolean{

       for (recipe in Recipes.rcp) {
           for (gradient in recipe.gradients) {
               if(item.isGradient(gradient)){
                   return true
               }
           }
       }
       return false
   }
}