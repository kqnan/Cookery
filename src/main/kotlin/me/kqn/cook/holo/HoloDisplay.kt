package me.kqn.cook.holo

import com.github.unldenis.hologram.Hologram
import com.github.unldenis.hologram.HologramPool
import org.bukkit.Location
import taboolib.platform.BukkitPlugin

class HoloDisplay {
    private val pool:HologramPool
    private val plugin:BukkitPlugin
    private val holo=HashMap<Location,Hologram>()
    constructor(plugin: BukkitPlugin){
        this.plugin=plugin
        pool=HologramPool(plugin,70.0,0.5f,0.5f)
    }

    fun display(pot_loc:Location,content:List<String>){
        holo[pot_loc]?.let { pool.remove(it) }
        val builder: Hologram.Builder = Hologram.builder()
            .location(pot_loc.clone().add(0.0,1.0,0.0))
        //content.forEach { builder.addLine(it.colored(),false) }
        builder.addLine("asdasd",false)

        holo.put(pot_loc,builder.build(pool))
    }
    fun remove(pot_loc: Location){
        holo[pot_loc]?.let { pool.remove(it) }
        holo.remove(pot_loc)
    }
}