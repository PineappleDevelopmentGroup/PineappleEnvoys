package sh.miles.pineappleenvoys.configuration.io

import com.google.gson.JsonElement
import org.bukkit.plugin.Plugin
import sh.miles.pineapple.collection.registry.WriteableRegistry
import sh.miles.pineapple.json.JsonHelper
import sh.miles.pineappleenvoys.GlobalConfig
import sh.miles.pineappleenvoys.Registries
import sh.miles.pineappleenvoys.configuration.EnvoyConfiguration
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.io.path.reader

class EnvoyConfigurationIOHandler(private val plugin: Plugin, private val jsonHelper: JsonHelper) :
    AbstractIOHandler<EnvoyConfiguration>(plugin, jsonHelper) {
    override val activationFile: String = "envoys.json"
    override val activationDir: String = "envoys"
    override val examples: List<String> = listOf(
        "default-envoy.json"
    )
    override val createExamples: Boolean = GlobalConfig.CREATE_EXAMPLES
    override val registry: WriteableRegistry<EnvoyConfiguration, String> = Registries.ENVOYS

    override fun loadFile(path: Path) {
        val configuration = jsonHelper.gson.fromJson(path.reader(Charsets.UTF_8), EnvoyConfiguration::class.java)
        plugin.logger.info("Loaded \"${path.pathString}\" as Envoy Configuration")
        registry.register(configuration)
    }

    override fun saveContent(key: String): JsonElement {
        throw UnsupportedOperationException("Envoy Configurations can not be saved")
    }

}
