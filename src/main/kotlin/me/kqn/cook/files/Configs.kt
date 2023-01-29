package me.kqn.cook.files

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submitAsync
import taboolib.common5.FileWatcher
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.platform.util.onlinePlayers
import java.io.File

object Configs {
    @Config(autoReload = true)
    lateinit var config: Configuration
    private val path="plugins/Cookery/config.yml"
    private val fuel =HashMap<Material,Double>()
    init {
        read()
        FileWatcher.INSTANCE.addSimpleListener(File(path)){
            submitAsync {
                read()
                onlinePlayers.forEach { if(it.isOp)it.sendMessage("${Messages.prefix}自动重载完成".colored()) }
            }
        }
    }
    fun read(){
        config= Configuration.loadFromFile(File(path))
        for (s in config.getStringList("fuel")) {
            var m=try {
                Material.valueOf(s.split(":")[0].uppercase())
            }catch (e:Exception){continue}
            var v=s.split(":")[1].toDoubleOrNull()?:continue
            fuel.put(m,v)
        }
    }
    fun  save(){
        submitAsync {
            config.saveToFile(File(path))
        }
    }

    fun getFuel(material: Material):Double{
        return fuel.getOrDefault(material,0.0)
    }
    fun Material.isPotFuel():Boolean{
        return fuel.keys.contains(this)
    }
    fun getModes():List<Pair<String,String>>{
        var res=ArrayList<Pair<String,String>>()
        var t1=config.getStringList("mode.keys")
        var t2= config.getStringList("mode.display")
        t1.forEachIndexed { index, s ->
            t2.getOrNull(index)?:return res
            res.add(Pair(s, t2[index]))
        }
        return res
    }
}