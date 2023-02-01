package me.kqn.cook.cook

import de.tr7zw.changeme.nbtapi.NBTItem
import me.kqn.cook.eval
import me.kqn.cook.isFood
import net.minecraft.server.v1_12_R1.FoodMetaData

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync
import taboolib.common.util.random
import taboolib.common.util.sync
import taboolib.platform.util.isRightClick


object RewardItem {

    @SubscribeEvent
    fun eat(e:PlayerItemConsumeEvent){
        parseItem(e.item?:return,e.player,true,e)
    }
    @SubscribeEvent
    fun rightclick(e:PlayerInteractEvent){
        if(e.item==null)return
        if(!e.isRightClick())return
        if(!e.item.isFood())parseItem(e.item?:return,e.player,false,e)
    }

    fun parseItem(itemStack: ItemStack,player: Player,eat:Boolean,cancellable: Cancellable){
        var nbtItem=NBTItem(itemStack.clone())
        if(!nbtItem.hasKey("cookery_buff")||!nbtItem.hasKey("cookery_triggerAll")||!nbtItem.hasKey("cookery_action"))return
        cancellable.isCancelled=true

        player.inventory.itemInMainHand.amount-=1
        submitAsync {

            var buff=nbtItem.getStringList("cookery_buff")
            var triggAll=nbtItem.getBoolean("cookery_triggerAll")
            var action=nbtItem.getString("cookery_action")
            action.eval(player)
            for (bf in buff) {
                var tmp=bf.split(":")
                if(tmp.size<4)continue
                var p=PotionEffectType.getByName(tmp[0])
                var lvl=tmp[1].toIntOrNull()?:continue
                var t=tmp[2].toIntOrNull()?:continue
                var chance=tmp[3].toIntOrNull()?:continue
                if(random(1,100)>chance)continue
               sync {  player.addPotionEffect(PotionEffect(p,t*20,lvl-1)) }
                if(!triggAll)break
            }

        }
    }

    fun getRewardItem(itemStack: ItemStack,buff:List<String>,triggAll:Boolean,action:String):ItemStack{
        var nbtItem=NBTItem(itemStack)
        var buffs= nbtItem.getStringList("cookery_buff")
        buffs.clear()
        buff.forEach { buffs.add(it) }
        nbtItem.setString("cookery_action",action)
        nbtItem.setBoolean("cookery_triggerAll",triggAll)
        return nbtItem.item
    }
}