package me.kqn.cook.cook

import de.tr7zw.changeme.nbtapi.NBTItem
import me.kqn.cook.isFood

import org.bukkit.entity.Player
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


object RewardItem {
    @SubscribeEvent
    fun eat(e:PlayerItemConsumeEvent){
        parseItem(e.item?:return,e.player)
    }
    @SubscribeEvent
    fun rightclick(e:PlayerInteractEvent){
        if(e.item==null)return
        if(!e.item.isFood())parseItem(e.item?:return,e.player)
    }

    fun parseItem(itemStack: ItemStack,player: Player){
        var nbtItem=NBTItem(itemStack)
        if(!nbtItem.hasKey("cookery_buff")||!nbtItem.hasKey("cookery_triggerAll"))return
        submitAsync {
            var buff=nbtItem.getStringList("cookery_buff")
            var triggAll=nbtItem.getBoolean("cookery_triggerAll")
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

    fun getRewardItem(itemStack: ItemStack,buff:List<String>,triggAll:Boolean):ItemStack{
        var nbtItem=NBTItem(itemStack)
        var buffs= nbtItem.getStringList("cookery_buff")
        buffs.clear()
        buff.forEach { buffs.add(it) }

        nbtItem.setBoolean("cookery_triggerAll",triggAll)
        return nbtItem.item
    }
}