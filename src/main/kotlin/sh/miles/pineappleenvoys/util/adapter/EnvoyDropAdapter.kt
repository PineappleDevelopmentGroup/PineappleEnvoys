package sh.miles.pineappleenvoys.util.adapter

import org.bukkit.Material
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter
import sh.miles.pineapple.util.spec.HologramSpec
import sh.miles.pineappleenvoys.envoy.EnvoyDrop
import sh.miles.pineappleenvoys.envoy.click.EnvoyClickEffectEntry

object EnvoyDropAdapter : SerializedAdapter<EnvoyDrop> {

    private const val LOOT_ID = "loot"
    private const val BLOCK_TYPE = "block_type"
    private const val WEIGHT = "weight"
    private const val HOLOGRAM = "hologram"
    private const val EFFECTS = "effects"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): EnvoyDrop {
        val parent = element.asObject
        val lootId = parent.getPrimitive(LOOT_ID).map { it.asString }.orThrow("Missing required field $LOOT_ID")
        val blockType = parent.getPrimitive(BLOCK_TYPE).map { Material.matchMaterial(it.asString) }.orThrow("Missing required field $BLOCK_TYPE or given $BLOCK_TYPE does not exist")
        if (blockType == null || !blockType.isBlock) throw IllegalStateException("The given $BLOCK_TYPE is not a block")
        val weight = parent.getPrimitive(WEIGHT).map { it.asDouble }.orThrow("Missing required field $WEIGHT")
        val hologram = parent.get(HOLOGRAM).map { context.deserialize(it, HologramSpec::class.java) }.orThrow("Missing required field $HOLOGRAM")
        val effects = parent.getArray(EFFECTS).map {
            it.stream().map { e -> context.deserialize(e, EnvoyClickEffectEntry::class.java) }.toList()
        }.orThrow("Missing required field $EFFECTS")

        return EnvoyDrop(lootId, blockType, weight, hologram, effects)
    }

    override fun serialize(obj: EnvoyDrop, context: SerializedSerializeContext): SerializedElement {
        throw UnsupportedOperationException("Can not serialize EnvoyDrop")
    }

    override fun getKey(): Class<*> {
        return EnvoyDrop::class.java
    }
}
