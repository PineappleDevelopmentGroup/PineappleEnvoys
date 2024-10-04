package sh.miles.pineappleenvoys.envoy.tile

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Display
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.item.ItemBuilder
import sh.miles.pineapple.tiles.api.TileType
import sh.miles.pineapple.tiles.api.Tiles
import sh.miles.pineappleenvoys.PineappleEnvoysPlugin
import sh.miles.pineappleenvoys.envoy.EnvoyDrop
import sh.miles.pineappleenvoys.util.BlockReplacer

object EnvoyTileType : TileType<EnvoyTile>(false) {

    val KEY = NamespacedKey.fromString("pineapple-envoys:envoy")!!

    override fun createTile(): EnvoyTile {
        return EnvoyTile()
    }

    override fun createTile(item: ItemStack): EnvoyTile {
        throw UnsupportedOperationException("Can not create an ItemStack tile for EnvoyTileType")
    }

    override fun createItemShell(tile: EnvoyTile?): ItemBuilder {
        throw UnsupportedOperationException("Can not create Item Shell for EnvoyTileType")
    }

    override fun getKey(): NamespacedKey {
        return KEY
    }

    override fun onBreak(event: BlockBreakEvent, tile: EnvoyTile) {
        event.isCancelled = true
    }

    override fun onInteract(event: PlayerInteractEvent, tile: EnvoyTile) {
        if (event.hand == EquipmentSlot.OFF_HAND) return
        val drop = tile.drop!!
        drop.loot.loot.poll().giveLoot(event.player)
        val location = tile.location!!
        PineappleEnvoysPlugin.ticker.queue(BlockReplacer(location, Material.AIR.createBlockData()))
        Bukkit.getEntity(tile.textDisplay!!)?.remove()
        Tiles.getInstance().deleteTile(tile.location!!) { true }
    }

    fun place(location: Location, drop: EnvoyDrop) {
        val tile = EnvoyTile()
        tile.drop = drop
        tile.location = location
        tile.textDisplay = drop.hologram.spawn(location) {
            it.billboard = Display.Billboard.CENTER
        }.uniqueId
        PineappleEnvoysPlugin.ticker.queue(BlockReplacer(location, drop.block.createBlockData()))

        Tiles.getInstance().placeTile(location, tile)
    }
}
