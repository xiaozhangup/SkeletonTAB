package me.xiaozhangup.skeletontab.module

import com.velocitypowered.api.proxy.ProxyServer
import me.xiaozhangup.skeletontab.TabList
import me.xiaozhangup.skeletontab.configuration.TabSettings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import java.util.concurrent.TimeUnit

class TabListHeaderFooter(private val proxyServer: ProxyServer?, tabSettings: TabSettings?) {
    private val header: Component
    private val footer: Component

    init {
        header = MiniMessage.miniMessage().deserialize(tabSettings?.toml!!.getString("tablist-header-footer.header"))
        footer = MiniMessage.miniMessage().deserialize(tabSettings.toml.getString("tablist-header-footer.footer"))
        proxyServer!!.scheduler.buildTask(TabList.plugin) { update() }.repeat(50, TimeUnit.MILLISECONDS).schedule()
    }

    private fun update() {
        for (player in proxyServer!!.allPlayers) {
            player.sendPlayerListHeaderAndFooter(header, footer)
        }
    }
}