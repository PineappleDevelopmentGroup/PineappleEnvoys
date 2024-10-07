package sh.miles.pineappleenvoys.envoy

import org.bukkit.Material
import sh.miles.pineapple.util.spec.HologramSpec
import sh.miles.pineappleenvoys.Registries
import sh.miles.pineappleenvoys.configuration.LootConfiguration
import sh.miles.pineappleenvoys.envoy.click.EnvoyClickEffectEntry

data class EnvoyDrop(val lootId: String, val block: Material, val spawnWeight: Double, val hologram: HologramSpec, val effects: List<EnvoyClickEffectEntry<*>>) {
    val loot: LootConfiguration
        get() = Registries.LOOT.get(lootId).orThrow("No such lootId $lootId")
}
