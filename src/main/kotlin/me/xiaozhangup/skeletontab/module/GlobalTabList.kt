package me.xiaozhangup.skeletontab.module

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.player.TabListEntry
import me.xiaozhangup.skeletontab.TabList
import net.kyori.adventure.text.Component

class GlobalTabList( // class server
    private val proxyServer: ProxyServer
) {
    // listener of player login
    @Subscribe
    fun connect(event: ServerConnectedEvent) {
        connectUpdate(event)
    }

    // listener of player disconnect
    @Subscribe
    fun disconnect(event: DisconnectEvent) {
        disconnectUpdate(event.player)
    }

    // pingUpdate tab list (!! not work when changing the server)
    private fun connectUpdate(event: ServerConnectedEvent) {
        val playerOfEvent = event.player
        // Update list of current players
        for (toPlayer in proxyServer.allPlayers) {
            val serverName = event.server.serverInfo.name
            addTabListEntry(toPlayer, playerOfEvent, serverName)
        }
        // Update list of new connecting player
        for (fromPlayer in proxyServer.allPlayers) {
            if (fromPlayer.currentServer.isPresent) {
                val serverName = fromPlayer.currentServer.get().serverInfo.name
                addTabListEntry(playerOfEvent, fromPlayer, serverName)
            }
        }
    }

    // remove disconnected player from list
    private fun disconnectUpdate(fromPlayer: Player) {
        for (toPlayer in proxyServer.allPlayers) {
            if (toPlayer.tabList.containsEntry(fromPlayer.uniqueId)) {
                toPlayer.tabList.removeEntry(fromPlayer.uniqueId)
            }
        }
    }

    // add TabList entry
    private fun addTabListEntry(toPlayer: Player, fromPlayer: Player, serverName: String) {
        if (toPlayer == fromPlayer) return
        if (toPlayer.currentServer.isEmpty) return
        if (toPlayer.currentServer.get().serverInfo.name == serverName) return
        if (toPlayer.tabList.containsEntry(fromPlayer.uniqueId)) toPlayer.tabList.removeEntry(fromPlayer.uniqueId)
        toPlayer.tabList.addEntry(
            TabListEntry.builder()
                .displayName(Component.text(fromPlayer.username))
                .latency(fromPlayer.ping.toInt())
                .profile(fromPlayer.gameProfile)
                .gameMode(0)
                .tabList(toPlayer.tabList)
                .build()
        )
    }

    // normal pingUpdate, public method used for registering the scheduler in plugin. Need to improve!
    fun pingUpdate() {
        for (toPlayer in proxyServer.allPlayers) for (fromPlayer in proxyServer.allPlayers) {
            if (fromPlayer.currentServer.isPresent) {
                if (toPlayer.tabList.containsEntry(fromPlayer.uniqueId)) {
                    if (toPlayer != fromPlayer &&
                        fromPlayer.currentServer.isPresent &&
                        toPlayer.currentServer.isPresent
                    ) {
                        if (toPlayer.currentServer.get() != fromPlayer.currentServer.get()) // ! setLatency seems not work !
                            findTabListEntry(toPlayer, fromPlayer).latency = fromPlayer.ping.toInt()
                    }
                } else addTabListEntry(toPlayer, fromPlayer, fromPlayer.currentServer.get().serverInfo.name)
            }
        }
    }

    fun updateTask() {
        proxyServer.scheduler.buildTask(TabList.plugin) {
            pingUpdate()
        }.repeat(500L, java.util.concurrent.TimeUnit.MILLISECONDS).schedule()
    }

    companion object {
        fun findTabListEntry(toPlayer: Player, fromPlayer: Player): TabListEntry {
            return toPlayer.tabList.entries
                .stream()
                .filter { t: TabListEntry -> t.profile.id == fromPlayer.gameProfile.id }
                .findFirst()
                .orElse(null)
        }
    }
}