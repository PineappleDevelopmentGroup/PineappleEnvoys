package sh.miles.pineappleenvoys.configuration.io

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import org.bukkit.plugin.Plugin
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.collection.registry.Registry
import sh.miles.pineapple.json.JsonHelper
import sh.miles.pineappleenvoys.util.MarkedKey
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.notExists
import kotlin.io.path.pathString
import kotlin.io.path.reader
import kotlin.io.path.relativeTo
import kotlin.io.path.writeLines
import kotlin.io.path.writeText
import kotlin.io.path.writer

abstract class AbstractIOHandler<R : MarkedKey<String>>(
    private val plugin: Plugin,
    private val jsonHelper: JsonHelper
) {
    protected abstract val activationFile: String
    protected abstract val activationDir: String
    protected abstract val examples: List<String>
    protected abstract val createExamples: Boolean
    protected abstract val registry: Registry<R, String>

    fun load() {
        saveDefaults()
        val activationJson =
            JsonParser.parseReader(Path(plugin.dataFolder.path, activationFile).reader(Charsets.UTF_8)).asJsonObject
        val activateAll = activationJson.getAsJsonPrimitive("activate-all").asBoolean
        val activationList = activationJson.getAsJsonArray("active").map {
            // Shouldn't ever be used in prod | Welp its in prod now
            if (
                System.getProperty("os.name").lowercase().contains("windows")) {
                PineappleLib.getLogger()
                    .warning("System detected as windows, converting to windows file paths. IF THIS IS SEEN IN PROD REPORT TO EBIC/MILES")
                it.asString.replace("/", "\\")
            } else {
                it.asString
            }
        }.toSet()

        val activationPath = Path(plugin.dataFolder.path, activationDir)
        for (child in Files.list(activationPath)) {
            val relativeChild = child.relativeTo(Path(plugin.dataFolder.path))
            if (activateAll) {
                plugin.logger.info("Activating file ${relativeChild.pathString}")
                loadFile(child)
            } else if (activationList.contains(relativeChild.pathString)) {
                plugin.logger.info("Activating file ${relativeChild.pathString}")
                loadFile(child)
            }
        }
    }

    fun save() {
        val saveDirectory = plugin.dataFolder.toPath().resolve(activationDir)
        for (key in registry.keys()) {
            val serializable = registry.get(key).orThrow()
            if (!serializable.dirty) {
                continue
            }
            val content = saveContent(key)
            if (content.isJsonNull || (content.isJsonObject && content.asJsonObject.isEmpty)) {
                plugin.logger.warning("Unable to save empty ${serializable.key}.json is this intended?")
                return
            }

            val savePath = saveDirectory.resolve("${serializable.key}.json")
            savePath.writeText(jsonHelper.gson.toJson(content), Charsets.UTF_8)
        }
    }

    private fun saveDefaults() {
        val loaderFile = Path(plugin.dataFolder.path, activationFile)

        if (loaderFile.notExists() && createExamples) {
            plugin.saveResource(activationFile, false)
        }

        for (example in examples) {
            val exampleFile = Path(plugin.dataFolder.path, activationDir, example)
            if (exampleFile.notExists() && createExamples) {
                plugin.saveResource("$activationDir/$example", false)
            }
        }
    }

    protected abstract fun loadFile(path: Path)
    protected abstract fun saveContent(key: String): JsonElement
}
