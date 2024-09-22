package sh.miles.pineappleenvoys.configuration

import sh.miles.pineapple.collection.WeightedRandom
import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineappleenvoys.loot.LootEntry
import sh.miles.pineappleenvoys.util.MarkedKey

data class LootConfiguration(
    val id: String,
    val icon: ItemSpec,
    private var lootTemp: WeightedRandom<LootEntry<*>>,
    override var dirty: Boolean
) : MarkedKey<String> {
    var loot = lootTemp
        private set

    fun addLootEntry(exact: LootEntry<*>) {
        this.loot.add(exact.weight, exact)
    }

    fun removeLootEntry(exact: LootEntry<*>) {
        val random = WeightedRandom<LootEntry<*>>()
        loot.entries.filter { it.value != exact }.forEach { random.add(it.value.weight, it.value) }
        this.loot = random
        dirty = true
    }

    fun removeLootEntries(exact: Set<LootEntry<*>>) {
        val random = WeightedRandom<LootEntry<*>>()
        loot.entries.filter { !exact.contains(it.value) }.forEach { random.add(it.value.weight, it.value) }
        this.loot = random
        dirty = true
    }

    override fun getKey(): String {
        return id
    }
}
