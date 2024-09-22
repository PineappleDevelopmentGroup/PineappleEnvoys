package sh.miles.pineappleenvoys.configuration.adapter

import sh.miles.pineapple.collection.WeightedRandom
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter
import sh.miles.pineappleenvoys.configuration.LootConfiguration
import sh.miles.pineappleenvoys.loot.LootEntry

object LootConfigurationAdapter : SerializedAdapter<LootConfiguration> {

    private const val ID = "id"
    private const val ICON = "icon"
    private const val LOOT = "table"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): LootConfiguration {
        val parent = element.asObject
        val id = parent.getPrimitive(ID).orThrow().asString
        val icon = context.deserialize(parent.get(ICON).orThrow(), ItemSpec::class.java)
        val loot = WeightedRandom<LootEntry<*>>()
        val lootArray = parent.getArray(LOOT).orThrow()
        for (serializedLootEntry in lootArray) {
            val lootEntry = context.deserialize(serializedLootEntry, LootEntry::class.java)
            loot.add(lootEntry.weight, lootEntry)
        }

        return LootConfiguration(id, icon, loot, false)
    }

    override fun serialize(configuration: LootConfiguration, context: SerializedSerializeContext): SerializedElement {
        val id = SerializedElement.primitive(configuration.id)
        val icon = context.serialize(configuration.icon)
        val lootArray = SerializedElement.array(configuration.loot.size())
        for (entry in configuration.loot.entries) {
            lootArray.add(context.serialize(entry.value, LootEntry::class.java))
        }

        val parent = SerializedElement.`object`()
        parent.add(ID, id)
        parent.add(ICON, icon)
        parent.add(LOOT, lootArray)

        return parent
    }

    override fun getKey(): Class<*> {
        return LootConfiguration::class.java
    }
}
