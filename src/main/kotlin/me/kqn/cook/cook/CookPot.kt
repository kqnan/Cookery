package me.kqn.cook.cook

import me.kqn.cook.debug
import me.kqn.cook.files.Configs
import me.kqn.cook.files.Configs.isPotFuel
import me.kqn.cook.files.Messages
import me.kqn.cook.files.Recipes
import me.kqn.cook.isGradient
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptLocation
import taboolib.common.util.asList
import taboolib.common5.Baffle
import taboolib.expansion.getDataContainer
import taboolib.module.chat.colored


import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.isNotAir
import taboolib.platform.util.isRightClickBlock
import java.util.Collections
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

object CookPot {
    val currentPots=HashMap<Location,Pot>()
    private val baffle=Baffle.of(500,TimeUnit.MILLISECONDS)
    @Awake(LifeCycle.DISABLE)
    fun disable(){
        baffle.resetAll()
    }
    @SubscribeEvent
    fun addFuel(e:PlayerInteractEvent){
        if(baffle.hasNext()) baffle.next()
        if(e.isRightClickBlock()&&e.clickedBlock.type==Material.CAULDRON
            &&e.player.inventory.itemInMainHand.type.isPotFuel()
        ){
            var fuelMaterial=e.player.inventory.itemInMainHand
            var pot=currentPots[e.clickedBlock.location]?: Pot(e.clickedBlock.location)
            pot.addFuel(Configs.getFuel(fuelMaterial.type))
            currentPots[e.clickedBlock.location]=pot
            e.player.inventory.itemInMainHand.amount-=1
            e.player.sendMessage("${Messages.prefix}${Messages.fuel_added}".colored())
        }
    }
    @SubscribeEvent
    fun openPot(e:PlayerInteractEvent){
        if(e.isRightClickBlock()&&!e.player.inventory.itemInMainHand.type.isPotFuel()){
            if(e.clickedBlock.type== Material.CAULDRON){
                if(!currentPots.containsKey(e.clickedBlock.location)|| currentPots.get(e.clickedBlock.location)!!.state==Pot.State.WAITING){
                    e.player.openMenu<Basic>(title = "&a���ģʽ".colored()){
                        var loc=e.clickedBlock.location.clone()
                        rows(2)
                        var idx=2

                        //�������û��¼���¼�������ñ��еĶ���
                        var pot=Pot(loc)
                        if(currentPots.containsKey(loc)){
                            currentPots[loc]?.let { pot=it  }
                        }else {
                            currentPots.put(loc,pot)
                        }
                        set(13,ItemBuilder(Material.COAL).also { it.name="&aȼ��ֵ��&f${pot.getFuel()}".colored() }.build()){isCancelled=true}
                        for (mode in Configs.getModes()) {
                            set(idx++,ItemBuilder(Material.WOOL).also { it.name=mode.second.colored() }.build()){
                                this.clicker.closeInventory()
                                //���ȼ���Ƿ��㹻
                                if ((pot.getFuel())<1.0){
                                    this.clicker.sendMessage("${Messages.prefix}${Messages.fuel_not_enough}".colored())
                                    return@set}
                                //��ȡ������Ʒʵ��
                                val items=loc.world.getNearbyEntities(loc.clone(),1.0,1.0,1.0).filter { return@filter it.type==EntityType.DROPPED_ITEM } as List<org.bukkit.entity.Item>
                                //������һ��ʵ���У�ƥ�����ĸ��䷽
                                val playerlevel=this.clicker.getDataContainer()["level"]?.toIntOrNull()?:1

                                var recipe:Recipes.Recipe?=null
                                for (rcp in Recipes.rcp) {
                                    if(matchRecipe(mode.first,items.map { it.itemStack },rcp)&&playerlevel>=rcp.require_level){
                                        recipe=rcp
                                        break
                                    }
                                }
                                //���һ���䷽��ûƥ�䵽�����˳�
                                recipe?:return@set
                                //���ƥ�䵽�䷽�������䷽���ѵ��ϵ���Ʒ�Ƴ�
                                    //��ʣ�����Ʒ����
                                val remainitems=items.map { it.itemStack }.filter { it.isNotAir() }.groupBy { it.itemMeta.hashCode() }
                                    //��ÿһ��ѹ���ɵ�����Ʒ,ѹ����[0]λ�õ���Ʒ��
                                for (remainitem in remainitems) {
                                    var cnt=0
                                    remainitem.value.forEach { cnt+=it.amount }
                                    remainitem.value[0].amount=cnt
                                }
                                val rcpitems=recipe.gradients.groupBy { it.itemMeta.hashCode() }
                                    //�����䷽��ÿһ�����Ʒ
                                for (rcpitem in rcpitems) {
                                    //������һ����Ʒ��������
                                    var cnt=0
                                    rcpitem.value.forEach { cnt+=it.amount }
                                    //��remainitems���ҵ���һ�����Ʒ������ȥ����
                                    for (remainitem in remainitems) {
                                        if(remainitem.value[0].isGradient(rcpitem.value[0])){
                                            if(remainitem.value[0].amount<=cnt){
                                                remainitem.value[0].type=Material.AIR
                                            }
                                            else {
                                                remainitem.value[0].amount-=cnt
                                            }
                                        }
                                    }
                                }
                                //�Ƴ�ȫ����Ʒ
                                items.forEach { it.remove() }
                                //��ʣ����Ʒ�������ɳ���
                                for (remainitem in remainitems) {
                                    if(remainitem.value[0].isNotAir()){
                                        loc.world.dropItem(loc,remainitem.value[0])
                                    }
                                }
                                pot.gradient=recipe.gradients
                                pot.mode=mode.first
                                pot.cook(this.clicker,recipe)
                            }
                        }
                    }
                }
                else {
                    e.player.sendMessage("${Messages.prefix}${Messages.cooking}".colored())
                }
            }
        }
    }

    //�ж���һ��ԭ�����Ƿ��ɷ���һ���䷽��
    fun matchRecipe(cook_type:String,gradient:List<ItemStack>,recipe:Recipes.Recipe):Boolean{
            if(recipe.require_type!=cook_type)return  false
            val cfg_group=recipe.gradients.groupBy {it.itemMeta.hashCode()}
            var pot_group=gradient.groupBy { it.itemMeta.hashCode() }
            for (entry in cfg_group) {
                //�ж������Ʒ�Ƿ��ڹ���
                var res=false
                var en:Map.Entry<Int,List<ItemStack>>?=null
                for (entry2 in pot_group) {
                    if(entry2.value[0].isGradient(entry.value[0])){
                        res=true
                        en=entry2
                        break
                    }
                }
                if(!res)return false
                //�жϹ�����Ʒ�����Ƿ��㹻
                en!!
                var need=0
                entry.value.forEach { need+=it.amount }
                var pot_has=0
                en.value.forEach { pot_has+=it.amount }
                if(pot_has<need)return  false
            }
            return  true
    }
   private fun isRecipeItem(item:ItemStack):Boolean{

       for (recipe in Recipes.rcp) {
           for (gradient in recipe.gradients) {
               if(item.isGradient(gradient)){
                   return true
               }
           }
       }
       return false
   }
}