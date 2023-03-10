package me.kqn.cook.files

import com.comphenix.protocol.wrappers.WrappedDataWatcher

import me.kqn.cook.infoOP
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync
import taboolib.common5.FileWatcher
import taboolib.library.xseries.getItemStack
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import java.io.File

object Recipes {
    private val path="plugins/Cookery/recipes.yml"
    @Config(value="recipes.yml", autoReload = true)
    lateinit var recipes :Configuration
    val rcp=ArrayList<Recipe>()
    private val rci=ArrayList<ItemStack>()
    init {
        FileWatcher.INSTANCE.addSimpleListener(File(path)){
            submitAsync {
                recipes=Configuration.loadFromFile(File(path))
                read()
                infoOP("自动重载已完成")
            }
        }

    }

    override fun toString(): String {
        return rcp.toString()
    }

    fun read(){
        rcp.clear()
        rci.clear()
        for (key in recipes.getKeys(false)) {
            Recipe.getRecipe(recipes.getConfigurationSection(key)?:continue,key)?.let { rcp.add(it) }
        }
        for (recipe in rcp) {
            rci.addAll(recipe.gradients)
        }

    }
    fun getGradientsItem():ArrayList<ItemStack>{
        return rci
    }
    fun isGradient(itemStack: ItemStack):Boolean{
        return rci.map { it.itemMeta }.contains(itemStack.itemMeta ?:return false)
    }
    fun  save(){
        submitAsync {
            recipes.saveToFile(File(path))
        }
    }
    class  Recipe{
        var reward_action:String=""
        var key:String=""
        val gradients=ArrayList<ItemStack>()
        var time:Int =30
        var chance:Int=60
        var require_level=1
        var require_type=""
        var reward_exp=1
        var reward_item: ItemStack? =null
        var reward_buff_trigg_all:Boolean?=null
        var reward_buff:List<String>?=null
        private constructor()
        companion object{
            fun getRecipe(cfg:taboolib.library.configuration.ConfigurationSection,key:String):Recipe?{
                var recipe=Recipe()
                recipe.key=key
                cfg.getConfigurationSection("gradients")?.getKeys(false)?.forEach {
                    cfg.getItemStack("gradients.${it}")?.let { it1 -> recipe.gradients.add(it1) }
                }?:return null

                recipe.time= cfg.getInt("time")
                recipe.chance=cfg.getInt("chance")
                recipe.require_level=cfg.getInt("require_level")
                recipe.require_type=cfg.getString("require_type")?:return null
                recipe.reward_exp=cfg.getInt("rewards.exp")
                recipe.reward_item=cfg.getItemStack("rewards.item")
                recipe.reward_buff_trigg_all=cfg.getBoolean("rewards.item.buff_trigger_all")
                recipe.reward_buff=cfg.getStringList("rewards.item.buff")
                recipe.reward_action=cfg.getString("rewards.item.action","")!!
                return recipe
            }
        }

        override fun toString(): String {
            return "Recipe(gradients=$gradients, time=$time, chance=$chance, require_level=$require_level, require_type='$require_type', reward_exp=$reward_exp, reward_item=$reward_item, reward_buff_trigg_all=$reward_buff_trigg_all, reward_buff=$reward_buff)"
        }
    }
}