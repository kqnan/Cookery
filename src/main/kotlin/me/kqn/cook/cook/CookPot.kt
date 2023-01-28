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
import taboolib.module.chat.colored


import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.ItemBuilder
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
                    e.player.openMenu<Basic>(title = "&a≈Î‚øƒ£ Ω".colored()){
                        var loc=e.clickedBlock.location.clone()
                        rows(2)
                        var idx=2

                        for (mode in Configs.getModes()) {
                            set(idx++,ItemBuilder(Material.WOOL).also { it.name=mode.second.colored() }.build()){
                                this.clicker.closeInventory()
                                var gradients= getGradients(loc)
                                var pot=Pot(loc,this.clicker)
                                pot.gradient=gradients
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
    private fun getGradients(loc:Location):ArrayList<ItemStack>{
        debug(loc.toString())
        var items=loc.world.getNearbyEntities(loc.clone(),1.0,1.0,1.0).filter {
            debug(it.type.toString()+"  "+(it.type==EntityType.DROPPED_ITEM).toString())
            debug(isRecipeItem((it as org.bukkit.entity.Item).itemStack).toString())

            if(it.type==EntityType.DROPPED_ITEM&& isRecipeItem((it as org.bukkit.entity.Item).itemStack)){
                it.remove()
                return@filter  true
            }
            return@filter false}.map{
            (it as org.bukkit.entity.Item).itemStack
        }
        var res=ArrayList<ItemStack>()
        items.forEach { res.add(it) }
        debug(res.toString())
        return  res
    }
   private fun isRecipeItem(item:ItemStack):Boolean{
       debug(Recipes.rcp.toString())
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