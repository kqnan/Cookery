package me.kqn.cook.integrate



import com.sk89q.worldguard.bukkit.RegionContainer
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.BooleanFlag
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

object WorldGuard :Protection {
    private val flag = BooleanFlag("Cookery-Area")
    private var worldGuardPlugin:WorldGuardPlugin?=null
    private var regionContainer:RegionContainer?=null
    @Awake(LifeCycle.LOAD)
    fun register (){
        this.worldGuardPlugin = Bukkit.getPluginManager().getPlugin("WorldGuard") as WorldGuardPlugin
        val booleanFlag: BooleanFlag = flag
        this.regionContainer = worldGuardPlugin!!.regionContainer
        try {
            worldGuardPlugin!!.flagRegistry.register(booleanFlag)
            Bukkit.getLogger().warning("[Cookery]¿ªÆôWorldGuard")
        } catch (e: Exception) {
            e.printStackTrace()
            Bukkit.getLogger().warning("[Cookery]Î´¿ªÆôWorldGuard")
        }
    }
    override fun allowOpen(loc: Location, player: Player) :Boolean{
        val localPlayer = worldGuardPlugin?.wrapPlayer(player)?:return  false
        val regions = regionContainer?.createQuery()?.getApplicableRegions(loc)?:return  false
        val allow = regions.queryValue(localPlayer, flag)
        return allow?:false
    }

    override fun allowAddFuel(loc: Location, player: Player) :Boolean{
        val localPlayer = worldGuardPlugin?.wrapPlayer(player)?:return  false
        val regions = regionContainer?.createQuery()?.getApplicableRegions(loc)?:return  false
        val allow = regions.queryValue(localPlayer, flag)
        return allow?:false
    }

}