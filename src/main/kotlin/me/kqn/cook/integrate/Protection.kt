package me.kqn.cook.integrate

import org.bukkit.Location
import org.bukkit.entity.Player

interface Protection {
    fun allowOpen(loc:Location,player: Player):Boolean
    fun allowAddFuel(loc:Location,player: Player):Boolean
}