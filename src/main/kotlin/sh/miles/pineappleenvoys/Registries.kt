package sh.miles.pineappleenvoys

import org.bukkit.plugin.Plugin
import sh.miles.pineapple.json.JsonHelper
import sh.miles.pineappleenvoys.configuration.io.LootConfigurationIOHandler
import sh.miles.pineappleenvoys.configuration.registry.LootConfigurationRegistry
import sh.miles.pineappleenvoys.configuration.registry.LootProviderRegistry

object Registries {

    val LOOT = LootConfigurationRegistry
    val LOOT_PROVIDER = LootProviderRegistry

    fun load(plugin: Plugin, jsonHelper: JsonHelper) {
        LootConfigurationIOHandler(plugin, jsonHelper).load()
    }

    fun save(plugin: Plugin, jsonHelper: JsonHelper) {
        LootConfigurationIOHandler(plugin, jsonHelper).save()
    }

}
