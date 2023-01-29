package me.kqn.cook.integrate

import com.bekvon.bukkit.residence.api.ResidenceApi
import org.bukkit.Location
import org.bukkit.entity.Player

class Residence :Protection{

    override fun allowOpen(loc: Location, player: Player):Boolean {
        var res=ResidenceApi.getResidenceManager().getByLoc(loc)
        return (res?:return true).ownerUUID==player.uniqueId||res.isTrusted(player)
    }
    override fun allowAddFuel(loc: Location, player: Player):Boolean {
        var res=ResidenceApi.getResidenceManager().getByLoc(loc)
        return (res?:return true).ownerUUID==player.uniqueId||res.isTrusted(player)
    }
}