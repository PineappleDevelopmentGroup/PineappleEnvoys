package sh.miles.pineappleenvoys.loot

import org.bukkit.entity.Player
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.item.ItemSpec

class LootEntry<T>(var provider: LootProvider<T>, var loot: T?, var weight: Double, var icon: ItemSpec) {

    fun giveLoot(player: Player) {
        if (loot == null) {
            PineappleLib.getLogger()
                .warning("Loot entry with provider ${provider.key} and reward class ${provider.rewardClass} has no reward set")
            return
        }
        provider.give(loot!!, player)
    }

    fun lootAsString(): String {
        if (loot == null) {
            return "None Set"
        }
        return provider.asString(loot!!)
    }
}
