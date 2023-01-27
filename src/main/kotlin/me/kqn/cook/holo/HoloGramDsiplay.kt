package me.kqn.cook.holo

import org.bukkit.Location

interface HoloGramDsiplay {
    fun addholo(potLoc:Location,content:List<String>)
    fun removeholo(potLoc: Location)
    fun clear()
}