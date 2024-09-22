package sh.miles.pineappleenvoys.configuration.io

import com.google.gson.JsonElement
import org.bukkit.plugin.Plugin
import sh.miles.pineapple.collection.registry.WriteableRegistry
import sh.miles.pineapple.json.JsonHelper
import sh.miles.pineappleenvoys.Registries
import sh.miles.pineappleenvoys.configuration.LootConfiguration
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.io.path.reader

class LootConfigurationIOHandler(
    private val plugin: Plugin, private val jsonHelper: JsonHelper
) : AbstractIOHandler<LootConfiguration>(plugin, jsonHelper) {
    override val activationFile: String = "loot-tables.json"
    override val activationDir: String = "loot-tables"
    override val examples: List<String> = listOf(
        "default-loot.json"
    )
    override val createExamples: Boolean = true
    override val registry: WriteableRegistry<LootConfiguration, String> = Registries.LOOT

    override fun loadFile(path: Path) {
        val configuration = jsonHelper.gson.fromJson(path.reader(Charsets.UTF_8), LootConfiguration::class.java)
        plugin.logger.info("Loaded \"${path.pathString}\" as Loot Configuration")
        registry.register(configuration)
    }

    override fun saveContent(key: String): JsonElement {
        val serializedConfiguration =
            jsonHelper.gson.toJsonTree(registry.get(key).orThrow(), LootConfiguration::class.java)
        plugin.logger.info("Saved \"${key}\" as Loot Configuration to \"${activationDir}/$key.json\"")
        return serializedConfiguration
    }

}
