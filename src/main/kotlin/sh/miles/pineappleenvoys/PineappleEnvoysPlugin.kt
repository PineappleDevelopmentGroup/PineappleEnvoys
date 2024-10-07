package sh.miles.pineappleenvoys

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.json.JsonHelper
import sh.miles.pineapple.task.work.ServerThreadTicker
import sh.miles.pineapple.tiles.api.Tiles
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapterRegistry
import sh.miles.pineapple.util.serialization.bridges.gson.GsonSerializedBridge
import sh.miles.pineappleenvoys.command.EnvoysCommand
import sh.miles.pineappleenvoys.configuration.adapter.EnvoyConfigurationAdapter
import sh.miles.pineappleenvoys.configuration.adapter.LootConfigurationAdapter
import sh.miles.pineappleenvoys.envoy.EnvoyEventSpec
import sh.miles.pineappleenvoys.envoy.click.EnvoyClickEffectEntry
import sh.miles.pineappleenvoys.envoy.tile.EnvoyTileType
import sh.miles.pineappleenvoys.listener.ChatInputListener
import sh.miles.pineappleenvoys.util.adapter.EnvoyClickEffectEntryAdapter
import sh.miles.pineappleenvoys.util.adapter.EnvoyDropAdapter
import sh.miles.pineappleenvoys.util.adapter.EnvoyEventSpecAdapter
import sh.miles.pineappleenvoys.util.adapter.LootEntryAdapter

class PineappleEnvoysPlugin : JavaPlugin() {

    companion object {
        lateinit var plugin: Plugin
            private set
        lateinit var ticker: ServerThreadTicker
            private set
    }

    lateinit var jsonHelper: JsonHelper
        private set

    override fun onEnable() {
        plugin = this
        PineappleLib.initialize(this)
        ticker = ServerThreadTicker()
        Tiles.setup(this)
        Tiles.getInstance().registerTileType(EnvoyTileType)
        setupSerializers()
        Registries.load(this, jsonHelper)

        Tiles.getInstance().loadSpawnChunks()

        PineappleLib.getCommandRegistry().register(EnvoysCommand)
        server.pluginManager.registerEvents(ChatInputListener, this)

        EnvoysTickLoop(ticker).start(this)
    }

    override fun onDisable() {
        Registries.save(this, jsonHelper)

        PineappleLib.cleanup()
        Tiles.cleanup()
    }

    private fun setupSerializers() {
        val registry = SerializedAdapterRegistry.INSTANCE

        // registry
        registry.register(LootConfigurationAdapter)
        registry.register(EnvoyConfigurationAdapter)

        // spec
        registry.register(EnvoyClickEffectEntryAdapter)
        registry.register(LootEntryAdapter)
        registry.register(EnvoyDropAdapter)
        registry.register(EnvoyEventSpecAdapter)

        jsonHelper = JsonHelper {
            it.setPrettyPrinting()
            it.disableHtmlEscaping()
            registry.registerBridge(GsonSerializedBridge(it))
        }
    }
}
