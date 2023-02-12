package me.xiaozhangup.skeletontab

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import me.xiaozhangup.skeletontab.configuration.TabSettings
import me.xiaozhangup.skeletontab.module.GlobalTabList
import me.xiaozhangup.skeletontab.module.TabListHeaderFooter
import org.slf4j.Logger
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import java.nio.file.Path

@RuntimeDependencies(
    RuntimeDependency(value = "net.kyori:adventure-text-minimessage:4.12.0")
)
object TabList : Plugin() {
    @Inject
    private val logger: Logger? = null

    @Inject
    private val proxyServer: ProxyServer? = null

    @Inject
    @DataDirectory
    private val dataDirectory: Path? = null
    private var tabSettings: TabSettings? = null
    private var globalTabList: GlobalTabList? = null
    private var tabListHeaderFooter: TabListHeaderFooter? = null
    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent?) {
        tabSettings = TabSettings(dataDirectory!!.toFile())
        if (tabSettings!!.isEnabled) {
            if (tabSettings?.toml?.getBoolean("global-tablist.enabled") == true) {
                globalTabList = GlobalTabList(proxyServer)
                proxyServer!!.eventManager.register(this, globalTabList)
                logger!!.info("Loaded Global Tablist")
            }
            if (tabSettings?.toml?.getBoolean("tablist-header-footer.enabled") == true) {
                tabListHeaderFooter = TabListHeaderFooter(this, proxyServer, tabSettings)
                proxyServer!!.eventManager.register(this, tabListHeaderFooter)
                logger!!.info("Loaded Header & Footer")
            }
        }
    }
}