package me.kqn.cook.cook


import me.kqn.cook.Cookery
import me.kqn.cook.files.Configs
import me.kqn.cook.files.Messages
import me.kqn.cook.files.Recipes
import me.kqn.cook.isGradient
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.service.PlatformExecutor
import taboolib.common.util.random
import taboolib.common.util.sync
import taboolib.common5.Baffle
import taboolib.expansion.getDataContainer
import taboolib.library.xseries.getItemStack
import taboolib.module.chat.colored
import java.util.concurrent.TimeUnit


data class Pot(val loc: Location){
    private lateinit var player:Player
    private var recipe: Recipes.Recipe?=null
    private var fuel:Double=0.0
    var mode:String?=null
    var modeDisplay:String?=null
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
    fun cook( player: Player,recipe:Recipes.Recipe?){
        this.player=player
        this.recipe=recipe
        var level=player.getDataContainer()["level"]?.toIntOrNull()?:1
        var chance=(recipe?.chance ?: 100)
        //当食谱需要的等级低于当前等级，则触发额外成功率
        if(recipe!=null&&(recipe.require_level)<level){
            chance+=Configs.config.getIntegerList("upgrade_success_chance").getOrElse(level-2){0}
        }
        //如果等级未达到当前食谱需要的等级则必定失败
        else if(recipe!=null&&recipe.require_level>level){
            chance=0
        }
        this.isSuccess= random(1, 100) <= chance
        this.cookTime=recipe?.time?:30

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
                loc.world.spawnParticle(Particle.SMOKE_LARGE,loc.clone().add(0.5,2.0,0.5),2,0.0,0.0,0.0,0.0)
                sync { Cookery.holoDisplay.addholo(loc.clone().add(0.5,2.0,0.5), listOf("${modeDisplay}",
                    Messages.cooking_hd,"${Messages.remain_time_hd}:${remain}${Messages.seconds}","${Messages.success_chance}：${chance}%")) }
            }

    }
    private fun reward(recipe:Recipes.Recipe?){
        if(recipe==null){
            sync { loc.world.dropItemNaturally(loc.clone().add(0.0,1.0,0.0),Configs.getUnkonwItem()?:return@sync) }
            return
        }
        recipe!!
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
        updateUnlockRecipe()
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
    private fun updateUnlockRecipe(){
        val res=LinkedHashSet<String>()
        player.getDataContainer()["unlock"]?.let { res.addAll(it.split(":")) }
        this.recipe?.let { res.add(it.key) }
        val builder=StringBuilder()
        res.forEachIndexed { index, s ->
            builder.append(s)
            if(index<res.size-1)builder.append(":")
        }
        player.getDataContainer()["unlock"]=builder.toString()
    }

}
