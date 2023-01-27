package me.kqn.cook.cook


import me.kqn.cook.Cookery
import me.kqn.cook.files.Configs
import me.kqn.cook.files.Recipes
import me.kqn.cook.isGradient
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync
import taboolib.common5.Baffle
import taboolib.expansion.getDataContainer
import java.util.concurrent.TimeUnit


data class Pot(val loc: Location,val player: Player){
    var mode:String?=null
    var state:State=State.WAITING
    var gradient:ArrayList<ItemStack>?=null

    private val cookTime=30

    enum class State{
        WAITING,
        COOKING
    }
    fun cook(){
        if((gradient?.size ?: return) > 0){
            state=State.COOKING
            CookPot.currentPots.put(loc.clone(),this)
            var remain=cookTime
            submitAsync(period = 20){
                if(remain<=0){
                    finish()
                    this.cancel()
                    return@submitAsync
                }
                remain--
                Cookery.holoDisplay.addholo(loc.clone().add(0.0,1.0,0.0), listOf("&a正在烹饪中，剩余时间:${remain}秒"))
            }
        }
    }
    private fun reward(recipe:Recipes.Recipe){
        var exp=(player.getDataContainer()["exp"]?.toIntOrNull()?:0)+recipe.reward_exp
       Configs.config.getIntegerList("upgrade_require_exp").forEachIndexed { index, i ->

       }
    }
    private fun finish(){
        var res=false
        recipe@ for (recipe in Recipes.rcp) {

            val cfg_group=recipe.gradients.groupBy {it.itemMeta.hashCode()}
            var pot_group=this.gradient?.groupBy { it.itemMeta.hashCode() }?:return
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
                if(!res)continue@recipe
                //判断锅中物品数量是否足够
                en!!
                var need=0
                entry.value.forEach { need+=it.amount }
                var pot_has=0
                en.value.forEach { pot_has+=it.amount }
                if(pot_has<need)continue@recipe
            }
            res=true
            reward(recipe)
            break@recipe
        }
        state=State.WAITING
        gradient=null

    }

}
