package me.xiaozhangup.skeletontab.configuration

import com.moandjiezana.toml.Toml
import java.io.File
import java.io.IOException
import java.nio.file.Files

class TabSettings(private val dataFolder: File) {
    private val file: File = File(dataFolder, "config.toml")
    var isEnabled: Boolean
    val toml: Toml

    init {
        saveDefaultConfig()
        val toml = loadConfig()
        isEnabled = toml.getBoolean("plugin.enabled")

        // Load Toml
        this.toml = toml
    }

    private fun saveDefaultConfig() {
        if (!dataFolder.exists()) dataFolder.mkdir()
        if (file.exists()) return
        try {
            TabSettings::class.java.getResourceAsStream("/config.toml")
                .use { `in` -> `in`?.let { Files.copy(it, file.toPath()) } }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private val configFile: File
        get() = File(dataFolder, "config.toml")

    private fun loadConfig(): Toml {
        return Toml().read(configFile)
    }
}