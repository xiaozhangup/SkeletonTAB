package me.xiaozhangup.skeletontab.module

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.player.TabListEntry
import net.kyori.adventure.text.Component

class GlobalTabList(private val proxyServer: ProxyServer?) {
    @Subscribe
    fun connect(event: ServerConnectedEvent?) {
        update()
    }

    @Subscribe
    fun disconnect(event: DisconnectEvent?) {
        update()
    }

    private fun update() {
        for (player in proxyServer!!.allPlayers) {
            for (player1 in proxyServer.allPlayers) {
                if (!player.tabList.containsEntry(player1.uniqueId)) {
                    player.tabList.addEntry(
                        TabListEntry.builder()
                            .displayName(Component.text(player1.username))
                            .profile(player1.gameProfile)
                            .gameMode(0) // Impossible to get player game mode from proxy, always assume survival
                            .tabList(player.tabList)
                            .build()
                    )
                }
            }
            for (entry in player.tabList.entries) {
                val uuid = entry.profile.id
                val playerOptional = proxyServer.getPlayer(uuid)
                if (playerOptional.isPresent) {
                    // Update ping
                    entry.latency = (player.ping * 1000).toInt()
                } else {
                    player.tabList.removeEntry(uuid)
                }
            }
        }
    }
}