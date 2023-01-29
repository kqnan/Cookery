package me.kqn.cook.cook


import me.kqn.cook.Cookery
import me.kqn.cook.files.Configs
import me.kqn.cook.files.Messages
import me.kqn.cook.files.Recipes
import me.kqn.cook.isGradient
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.service.PlatformExecutor
import taboolib.common.util.random
import taboolib.common.util.sync
import taboolib.common5.Baffle
import taboolib.expansion.getDataContainer
import taboolib.module.chat.colored
import java.util.concurrent.TimeUnit


data class Pot(val loc: Location){
    private lateinit var player:Player
    private lateinit var recipe: Recipes.Recipe
    private var fuel:Double=0.0
    var mode:String?=null
    var state:State=State.WAITING
    var gradient:ArrayList<ItemStack>?=null
    private var isSuccess= false
    private var cookTime:Int=1
    private var task:PlatformExecutor.PlatformTask?=null
    enum class State{
        WAITING,
        COOKING
    }
    fun getFuel():Double{
        return fuel
    }
    fun addFuel(value:Double){
        fuel+=value
    }
    fun cook( player: Player,recipe:Recipes.Recipe){
        this.player=player
        this.recipe=recipe
        this.isSuccess= random(1,100)<=recipe.chance
        this.cookTime=recipe.time
        if((gradient?.size ?: return) > 0){
            state=State.COOKING
        //    CookPot.currentPots.put(loc.clone(),this)
            var remain=cookTime
            fuel-=1.0
            task=submitAsync(period = 20){
                if(remain<=0){
                    try {
                        finish()
                    } catch (e: Exception) {
                    }
                    this.cancel()
                    return@submitAsync
                }
                remain--
                sync { Cookery.holoDisplay.addholo(loc.clone().add(0.5,2.0,0.5), listOf("&a���������","&aʣ��ʱ��:${remain}��")) }
            }
        }
    }
    private fun reward(recipe:Recipes.Recipe){
        if(!isSuccess){
            player.sendMessage("${Messages.prefix}${Messages.failed}".colored())
            return
        }
        else {
            player.sendMessage("${Messages.prefix}${Messages.success}".colored())
        }

        var exp=(player.getDataContainer()["exp"]?.toIntOrNull()?:0)+recipe.reward_exp
        player.getDataContainer()["exp"]=exp
        var newlevel=1
        for (i in Configs.config.getIntegerList("upgrade_require_exp")) {
            if(exp>=i){
                newlevel++
            }
            else break
        }
        player.getDataContainer()["level"]=newlevel
        var ritem=RewardItem.getRewardItem(recipe.reward_item?:return,recipe.reward_buff?:return,recipe.reward_buff_trigg_all?:return)
        sync { loc.world.dropItemNaturally(loc.clone().add(0.0,1.0,0.0),ritem) }
    }
    private fun finish(){
        task?.cancel()
        task=null
        sync { Cookery.holoDisplay.removeholo(loc.clone().add(0.5,2.0,0.5)) }
        var res=false
        reward(this.recipe)
        state=State.WAITING
        gradient=null

    }

}