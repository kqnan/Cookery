package me.kqn.cook.holo

import com.gmail.filoghost.holographicdisplays.api.Hologram
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI
import me.filoghost.holographicdisplays.api.Position
import me.kqn.cook.debug
import org.bukkit.Bukkit
import org.bukkit.Location
import taboolib.common.util.sync
import taboolib.module.chat.colored
import taboolib.platform.BukkitPlugin

class HolographDisplay :HoloGramDsiplay {
    constructor(plugin: BukkitPlugin){
        holographicDisplaysAPI=HolographicDisplaysAPI.get(plugin)
        this.plugin=plugin
    }
    private val holographicDisplaysAPI:HolographicDisplaysAPI
    private val plugin: BukkitPlugin
    private var holos=HashMap<Position,me.filoghost.holographicdisplays.api.hologram.Hologram>()
    override fun addholo(potLoc: Location,content:List<String>) {
        var pos=Position.of(potLoc)
        removeholo(potLoc)

         holos.put(pos,holographicDisplaysAPI.createHologram(potLoc).also { content.forEach { its->it.lines.appendText(its.colored()) } })

    }

    override fun removeholo(potLoc: Location) {
     var pos=Position.of(potLoc)
          holos.get(pos)?.let {

              it.delete() }
          holos.remove(pos)
    }

    override fun clear() {
        holos.forEach { t, u ->u.delete()

        }
    }
}