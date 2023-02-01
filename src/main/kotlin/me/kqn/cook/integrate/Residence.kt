package me.kqn.cook.integrate

import com.bekvon.bukkit.residence.api.ResidenceApi
import com.bekvon.bukkit.residence.containers.Flags
import com.bekvon.bukkit.residence.protection.FlagPermissions.FlagCombo
import org.bukkit.Location
import org.bukkit.entity.Player

class Residence :Protection{

    override fun allowOpen(loc: Location, player: Player):Boolean {
        var res=ResidenceApi.getResidenceManager().getByLoc(loc)
        return (res?:return true).ownerUUID==player.uniqueId||res.permissions.playerHas(player, Flags.use,FlagCombo.OnlyTrue)
    }
    override fun allowAddFuel(loc: Location, player: Player):Boolean {
        var res=ResidenceApi.getResidenceManager().getByLoc(loc)
        return (res?:return true).ownerUUID==player.uniqueId||res.permissions.playerHas(player, Flags.use,FlagCombo.OnlyTrue)
    }
}