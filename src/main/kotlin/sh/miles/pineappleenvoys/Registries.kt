package sh.miles.pineappleenvoys

import org.bukkit.plugin.Plugin
import sh.miles.pineapple.json.JsonHelper
import sh.miles.pineappleenvoys.configuration.io.EnvoyConfigurationIOHandler
import sh.miles.pineappleenvoys.configuration.io.LootConfigurationIOHandler
import sh.miles.pineappleenvoys.configuration.registry.EnvoyClickEffectRegistry
import sh.miles.pineappleenvoys.configuration.registry.EnvoyConfigurationRegistry
import sh.miles.pineappleenvoys.configuration.registry.LootConfigurationRegistry
import sh.miles.pineappleenvoys.configuration.registry.LootProviderRegistry

object Registries {

    val ENVOYS = EnvoyConfigurationRegistry
    val LOOT = LootConfigurationRegistry
    val LOOT_PROVIDER = LootProviderRegistry
    val CLICK_EFFECT = EnvoyClickEffectRegistry

    fun load(plugin: Plugin, jsonHelper: JsonHelper) {
        LootConfigurationIOHandler(plugin, jsonHelper).load()
        EnvoyConfigurationIOHandler(plugin, jsonHelper).load()
    }

    fun save(plugin: Plugin, jsonHelper: JsonHelper) {
        LootConfigurationIOHandler(plugin, jsonHelper).save()
        // EnvoyConfigurationIOHandler(plugin, jsonHelper).save() Don't save for now? In game editor??
    }

}
