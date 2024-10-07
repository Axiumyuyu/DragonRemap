package me.axiumyu

import org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH
import org.bukkit.entity.EnderCrystal
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.EnderDragon.Phase.DYING
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

class DragonRemap : JavaPlugin(), Listener {
    companion object {
        var uuid: UUID? = null
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun onDamageEvent(ev: EntityDamageByEntityEvent) {
        if (ev.entity is EnderDragon) {
            if (ev.damager is EnderCrystal) ev.damage = 0.0
            val dragon = ev.entity as EnderDragon
            val maxHealth = dragon.getAttribute(GENERIC_MAX_HEALTH)!!.value
            if (ev.damage >= dragon.health * 0.08 && dragon.health >= maxHealth * 0.5) {
                ev.damage = dragon.health * 0.08
            } else if (ev.damage >= dragon.health * 0.08 && dragon.health < maxHealth * 0.5) {
                ev.damage = dragon.health * 0.08 + 2
            }
            if (uuid == null) {
                uuid = dragon.uniqueId
                if (dragon.bossBar?.players?.size != server.onlinePlayers.size) {
                    server.onlinePlayers.forEach {
                        dragon.bossBar?.addPlayer(it)
                    }
                }
                return
            } else if (uuid == ev.entity.uniqueId) {
                if ((ev.entity as EnderDragon).health < 2) {
                    val damager = ev.damager
                    /*val timer: Timer = Timer(true)
                    timer.schedule(object : TimerTask(){
                        var time = 0
                        override fun run() {
                            if (dragon.health <= 0 || dragon.isDead) {
//                                dragon.damage(2.0,damager)
//                                dragon.phase = DYING
                                when (time){
                                    0 -> server.serverTickManager.tickRate = 16.0F
                                    2 -> server.serverTickManager.tickRate = 12.0F
                                    4 -> server.serverTickManager.tickRate = 8.0F
                                    6 -> server.serverTickManager.tickRate = 4.0F
                                    8 -> server.serverTickManager.tickRate = 2.0F
                                    10 -> {
                                        server.serverTickManager.tickRate = 20.0F
                                        dragon.bossBar?.removeAll()
                                        uuid = null
                                        this.cancel()
                                    }
                                }
                                time++
                            }
                        }
                    },0,500)*/
                    /*timer.schedule(object : TimerTask(){
                        var time = 0
                        override fun run() {
                            if (dragon.health <= 1 || dragon.isDead) {
                                dragon.damage(2.0,damager)
                                dragon.phase = DYING
                                server.serverTickManager.tickRate = 2.0F
                                if (time >= 1) {
                                    server.serverTickManager.tickRate = 20.0F
                                    dragon.bossBar?.removeAll()
                                    uuid = null
                                    this.cancel()
                                }
                                time++
                            }
                        }
                    },0,50)*/
                    object : BukkitRunnable() {
                        var time = 0
                        override fun run() {
                            damager.sendMessage("tick rate: ${server.serverTickManager.tickRate}")
                            if (dragon.health <= 1 || dragon.isDead || dragon.isEmpty) {
                                dragon.damage(2.0, damager)
                                dragon.health = 0.0
                                dragon.phase = DYING
                                when (time) {
                                    0 -> server.serverTickManager.tickRate = 16.0F
                                    2 -> server.serverTickManager.tickRate = 12.0F
                                    4 -> server.serverTickManager.tickRate = 8.0F
                                    6 -> server.serverTickManager.tickRate = 4.0F
                                    8 -> server.serverTickManager.tickRate = 2.0F
                                    10 -> server.serverTickManager.tickRate = 4.0F
                                    12 -> server.serverTickManager.tickRate = 8.0F
                                    14 -> server.serverTickManager.tickRate = 12.0F
                                    16 -> server.serverTickManager.tickRate = 16.0F
                                    18 -> {
                                        server.serverTickManager.tickRate = 20.0F
                                        dragon.bossBar?.removeAll()
                                        uuid = null
                                        time = 0
                                        this.cancel()
                                    }
                                    else -> {
                                        server.serverTickManager.tickRate = 20.0F
                                        dragon.bossBar?.removeAll()
                                        uuid = null
                                        time = 0
                                        this.cancel()
                                    }
                                }
                                /*if (time >= 1) {
                                    server.serverTickManager.tickRate = 20.0F
                                    dragon.bossBar?.removeAll()
                                    uuid = null
                                    this.cancel()
                                }*/
                                time++
                            }
                        }
                    }.runTaskTimer(this, 0L, 6L)
                }
            }
        }
    }
}
