package me.xiaozhangup.skeletontab.module

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import me.xiaozhangup.skeletontab.TabList
import me.xiaozhangup.skeletontab.configuration.TabSettings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import java.util.concurrent.TimeUnit

class TabListHeaderFooter(private val proxyServer: ProxyServer?, tabSettings: TabSettings?) {
    private val header: String
    private val footer: String


    @Subscribe
    fun connect(event: ServerConnectedEvent) {
        update()
    }

    // listener of player disconnect
    @Subscribe
    fun disconnect(event: DisconnectEvent) {
        update()
    }

    init {
        header = tabSettings?.toml!!.getString("tablist-header-footer.header")
        footer = tabSettings.toml.getString("tablist-header-footer.footer")
        proxyServer!!.scheduler.buildTask(TabList.plugin) { update() }.repeat(1, TimeUnit.SECONDS).schedule()
    }

    private fun update() {
        for (player in proxyServer!!.allPlayers) {
            val head = MiniMessage.miniMessage().deserialize(
                header
                    .replace("{online}", proxyServer.allPlayers.count().toString())
                    .replace("{server}", serverName(player))
            )
            val foot = MiniMessage.miniMessage().deserialize(
                footer
                    .replace("{online}", proxyServer.allPlayers.count().toString())
                    .replace("{server}", serverName(player))
            )
            player.sendPlayerListHeaderAndFooter(head, foot)
        }
    }

    private fun serverName(player: Player): String {
        if (player.currentServer.isPresent) {
            val name = player.currentServer.get().serverInfo.name
            if (name.contains("worker")) {
                return "子岛屿"
            } else return "主岛屿"
        }
        return "位置地域"
    }
}