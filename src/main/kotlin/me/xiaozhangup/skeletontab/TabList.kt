package me.xiaozhangup.skeletontab

import com.google.inject.Inject
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import me.xiaozhangup.skeletontab.configuration.TabSettings
import me.xiaozhangup.skeletontab.module.GlobalTabList
import me.xiaozhangup.skeletontab.module.TabListHeaderFooter
import org.slf4j.Logger
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import taboolib.platform.VelocityPlugin
import java.nio.file.Path
import java.util.Objects

@RuntimeDependencies(
    RuntimeDependency(value = "net.kyori:adventure-text-minimessage:4.12.0")
)
object TabList : Plugin() {
    @JvmStatic
    lateinit var plugin: VelocityPlugin
        private set

    @Inject
    private var logger: Logger? = null

    @Inject
    private var proxyServer: ProxyServer? = null

    @Inject
    private var tabSettings: TabSettings? = null
    private var globalTabList: GlobalTabList? = null
    private var tabListHeaderFooter: TabListHeaderFooter? = null

    override fun onEnable() {
        plugin = VelocityPlugin.getInstance()
        logger = plugin.logger;
        proxyServer = plugin.server

        tabSettings = TabSettings(plugin.configDirectory.toFile())
        if (tabSettings!!.isEnabled) {
            if (tabSettings?.toml?.getBoolean("global-tablist.enabled") == true) {
                globalTabList = GlobalTabList(proxyServer)
                proxyServer!!.eventManager.register(plugin, globalTabList)
                logger!!.info("Loaded Global Tablist")
            }
            if (tabSettings?.toml?.getBoolean("tablist-header-footer.enabled") == true) {
                tabListHeaderFooter = TabListHeaderFooter(proxyServer, tabSettings)
                proxyServer!!.eventManager.register(plugin, tabListHeaderFooter)
                logger!!.info("Loaded Header & Footer")
            }
        }
    }
}