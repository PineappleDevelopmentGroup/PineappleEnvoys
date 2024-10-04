package sh.miles.pineappleenvoys

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import sh.miles.collector.util.spec.adapter.HologramSpecAdapter
import sh.miles.collector.util.spec.adapter.SoundSpecAdapter
import sh.miles.collector.util.spec.adapter.VectorSpecAdapter
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.json.JsonHelper
import sh.miles.pineapple.task.work.ServerThreadTicker
import sh.miles.pineapple.tiles.api.Tiles
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapterRegistry
import sh.miles.pineapple.util.serialization.bridges.gson.GsonSerializedBridge
import sh.miles.pineappleenvoys.command.EnvoysCommand
import sh.miles.pineappleenvoys.configuration.adapter.EnvoyConfigurationAdapter
import sh.miles.pineappleenvoys.configuration.adapter.LootConfigurationAdapter
import sh.miles.pineappleenvoys.configuration.registry.EnvoyConfigurationRegistry
import sh.miles.pineappleenvoys.envoy.EnvoyDrop
import sh.miles.pineappleenvoys.envoy.tile.EnvoyTileType
import sh.miles.pineappleenvoys.listener.ChatInputListener
import sh.miles.pineappleenvoys.util.adapter.BoundingBoxAdapter
import sh.miles.pineappleenvoys.util.adapter.EnvoyDropAdapter
import sh.miles.pineappleenvoys.util.adapter.LootEntryAdapter
import sh.miles.pineappleenvoys.util.spec.HologramSpec
import sh.miles.pineappleenvoys.util.spec.SoundSpec

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
        ticker = ServerThreadTicker(this)
        Tiles.setup(this)
        Tiles.getInstance().registerTileType(EnvoyTileType)
        setupSerializers()
        Registries.load(this, jsonHelper)

        Tiles.getInstance().loadSpawnChunks()

        PineappleLib.getCommandRegistry().register(EnvoysCommand)
        server.pluginManager.registerEvents(ChatInputListener, this)
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
        registry.register(LootEntryAdapter)
        registry.register(BoundingBoxAdapter)
        registry.register(EnvoyDropAdapter)
        registry.register(HologramSpecAdapter)
        registry.register(SoundSpecAdapter)
        registry.register(VectorSpecAdapter)

        jsonHelper = JsonHelper {
            it.setPrettyPrinting()
            it.disableHtmlEscaping()
            registry.registerBridge(GsonSerializedBridge(it))
        }
    }
}
