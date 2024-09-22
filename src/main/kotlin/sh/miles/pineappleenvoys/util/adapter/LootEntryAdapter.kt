package sh.miles.pineappleenvoys.util.adapter

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter
import sh.miles.pineappleenvoys.Registries
import sh.miles.pineappleenvoys.loot.LootEntry

internal object LootEntryAdapter : SerializedAdapter<LootEntry<*>> {

    private const val PROVIDER = "provider"
    private const val LOOT = "loot"
    private const val WEIGHT = "weight"
    private const val ICON = "icon"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): LootEntry<*> {
        val parent = element.asObject
        val provider = Registries.LOOT_PROVIDER.getUnsafe(parent.getPrimitiveOrNull(PROVIDER)!!.asString).orThrow()
        val loot = if (parent.has(LOOT)) provider.deserializeLoot(parent.getOrNull(LOOT)!!, context) else null
        val weight = parent.getPrimitiveOrNull(WEIGHT)!!.asPrimitive.asDouble
        val icon = parent.get(ICON).map { context.deserialize(it, ItemSpec::class.java) }.orElse {
            if (provider.rewardClass.isAssignableFrom(ItemStack::class.java)) {
                val spec = ItemSpec.fromStack(loot as ItemStack)
                spec.setDefaultTextMutator { PineappleChat.parse(it) }
                return@orElse spec
            } else if (provider.rewardClass.isAssignableFrom(ItemSpec::class.java)) {
                return@orElse loot as ItemSpec
            } else {
                val spec = ItemSpec(Material.PAPER)
                spec.setDefaultTextMutator { PineappleChat.parse(it) }
                return@orElse spec
            }
        }

        return LootEntry(provider, loot, weight, icon)
    }

    override fun serialize(obj: LootEntry<*>, context: SerializedSerializeContext): SerializedElement {
        val entry = obj as LootEntry<Any>
        val provider = entry.provider.key
        var loot: SerializedElement? = null
        if (obj.loot != null) {
            loot = entry.provider.serializeLoot(obj.loot!!, context)
        }
        val weight = entry.weight
        val icon = context.serialize(obj.icon, ItemSpec::class.java)

        val parent = SerializedElement.`object`()
        parent.add(PROVIDER, provider)
        if (loot != null) parent.add(LOOT, loot)
        parent.add(WEIGHT, weight)
        parent.add(ICON, icon)

        return parent
    }

    override fun getKey(): Class<*> {
        return LootEntry::class.java
    }
}
