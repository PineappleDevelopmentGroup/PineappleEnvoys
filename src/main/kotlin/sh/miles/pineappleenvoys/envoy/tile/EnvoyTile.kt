package sh.miles.pineappleenvoys.envoy.tile

import com.google.common.primitives.Doubles
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.tiles.api.Tile
import sh.miles.pineapple.tiles.api.TileType
import sh.miles.pineapple.tiles.api.Tiles
import sh.miles.pineappleenvoys.PineappleEnvoysPlugin
import sh.miles.pineappleenvoys.envoy.EnvoyDrop
import sh.miles.pineappleenvoys.envoy.EnvoyEvent
import sh.miles.pineappleenvoys.util.BlockReplacer
import java.util.UUID

class EnvoyTile : Tile {

    private val LOC_DEL = NamespacedKey.fromString("pineapple-envoy:location")!!
    private val TEXT_DEL = NamespacedKey.fromString("pineapple-envoy:text_display")!!

    var event: EnvoyEvent? = null
    var drop: EnvoyDrop? = null
    var location: Location? = null
    var textDisplay: UUID? = null

    override fun save(container: PersistentDataContainer, excludeFields: MutableSet<String>?) {
        if (location != null) {
            container.set(
                LOC_DEL,
                PersistentDataType.STRING,
                "${location!!.world!!.name},${location!!.x},${location!!.y},${location!!.z}"
            )
        }

        if (textDisplay != null) {
            container.set(
                TEXT_DEL,
                PersistentDataType.STRING,
                textDisplay.toString()
            )
        }
    }

    override fun load(container: PersistentDataContainer) {
        val locstring = container.get(LOC_DEL, PersistentDataType.STRING)!!.split(",")
        this.location = Location(
            Bukkit.getWorld(locstring[0])!!,
            Doubles.tryParse(locstring[1])!!,
            Doubles.tryParse(locstring[2])!!,
            Doubles.tryParse(locstring[3])!!
        )
        this.textDisplay = UUID.fromString(container.get(TEXT_DEL, PersistentDataType.STRING))

        PineappleEnvoysPlugin.ticker.queue(BlockReplacer(location!!, Material.AIR.createBlockData()))
        Bukkit.getScheduler().runTaskLater(PineappleEnvoysPlugin.plugin, Runnable {
            Bukkit.getEntity(textDisplay!!)?.remove()
            Tiles.getInstance().deleteTile(location!!, true)
        }, 20L)

        throw IllegalStateException("Envoy was saved, Deleting it, this should be avoided! Did the server crash? This error can usually be ignored")
    }

    override fun getTileType(): TileType<*> {
        return EnvoyTileType
    }
}
