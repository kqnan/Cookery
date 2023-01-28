package me.kqn.cook

import me.kqn.cook.files.Configs
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.info
import taboolib.module.chat.colored
import taboolib.platform.util.isAir
import taboolib.platform.util.onlinePlayers


fun debug(str:String){
    if(Configs.config.getBoolean("debug")){
        info(str)
        onlinePlayers.forEach {
            if(it.isOp)it.sendMessage(str)
        }
    }
}
fun infoOP(str:String){
    onlinePlayers.forEach {
        if(it.isOp) info(str.colored())
    }
}
fun ItemStack.isGradient(gradient:ItemStack):Boolean{
    if(this.type.isAir()||gradient.isAir)return false
    var r1=this.itemMeta.displayName==gradient.itemMeta.displayName
    var r2=this.type==gradient.type
    var r3=true
    for (s in gradient.itemMeta.lore) {
        if(!this.itemMeta.lore.contains(s)){
            r3=false
        }
    }
    return r1&&r2&&r3
}

fun ItemStack.isFood():Boolean{
    return arrayListOf(Material.APPLE,Material.MUSHROOM_SOUP,Material.BREAD,Material.PORK,
    Material.GOLDEN_APPLE,Material.RAW_FISH,Material.COOKED_FISH,Material.CAKE,Material.COOKIE
    ,Material.MELON,Material.RAW_BEEF,Material.COOKED_BEEF,Material.COOKED_CHICKEN,Material.RAW_CHICKEN,
    Material.ROTTEN_FLESH,Material.SPIDER_EYE,Material.CARROT,Material.POTATO,Material.BAKED_POTATO,
    Material.POISONOUS_POTATO,Material.PUMPKIN_PIE,Material.RABBIT,Material.COOKED_RABBIT,
    Material.RABBIT_STEW,Material.MUTTON,Material.COOKED_MUTTON,Material.BEETROOT,Material.BEETROOT_SOUP).contains(this.type)
}