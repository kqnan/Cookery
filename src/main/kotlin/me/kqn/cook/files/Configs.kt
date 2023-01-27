package me.kqn.cook.files

import taboolib.common.platform.function.submitAsync
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import java.io.File

object Configs {
    @Config(autoReload = true)
    lateinit var config: Configuration
    private val path="plugins/Cookery/config.yml"
    fun  save(){
        submitAsync {
            config.saveToFile(File(path))
        }
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