package me.kqn.cook

import me.kqn.cook.files.Configs
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.info
import taboolib.module.chat.colored
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