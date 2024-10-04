package sh.miles.pineappleenvoys.configuration.adapter

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.util.BoundingBox
import sh.miles.pineapple.collection.WeightedRandom
import sh.miles.pineapple.collection.set.PolicySet
import sh.miles.pineapple.collection.set.PolicySet.SetPolicy
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter
import sh.miles.pineappleenvoys.configuration.EnvoyConfiguration
import sh.miles.pineappleenvoys.envoy.EnvoyDrop

object EnvoyConfigurationAdapter : SerializedAdapter<EnvoyConfiguration> {

    private const val ID = "id"
    private const val DROPS = "drops"
    private const val WORLD = "world"
    private const val ENVOY_AMOUNT = "envoy_amount"
    private const val REGION = "region"
    private const val BLOCKS = "blocks"
    private const val BLOCKS_C_POLICY = "policy"
    private const val BLOCKS_C_ENTRIES = "entries"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): EnvoyConfiguration {
        val parent = element.asObject
        val id = parent.getPrimitive(ID).map { it.asString }.orThrow("Missing required field $ID")
        val weightedDrops = WeightedRandom<EnvoyDrop>()
        parent.getArray(DROPS).map { it.map { context.deserialize(it, EnvoyDrop::class.java) } }
            .orThrow("Missing required field $DROPS").forEach { weightedDrops.add(it.spawnWeight, it) }
        val world = parent.getPrimitive(WORLD)
            .map { Bukkit.getWorld(it.asString) ?: throw IllegalStateException("no such world ${it.asString}") }
            .orThrow("Missing required field $WORLD")
        val envoyAmount =
            parent.getPrimitive(ENVOY_AMOUNT).map { it.asInt }.orThrow("Missing required field $ENVOY_AMOUNT")
        val region = parent.get(REGION).map { context.deserialize(it, BoundingBox::class.java) }
            .orThrow("Missing required field $REGION")

        val blocksParent = parent.get(BLOCKS).map { it.asObject }.orThrow("Missing required field $BLOCKS")
        val blocksPolicy = blocksParent.getPrimitive(BLOCKS_C_POLICY).map { SetPolicy.valueOf(it.asString.uppercase()) }
            .orThrow("Missing required field $BLOCKS_C_POLICY")
        val blocksEntries = blocksParent.getArray(BLOCKS_C_ENTRIES).map { e ->
            e.asSequence().filter { it.isPrimitive }.map { Material.matchMaterial(it.asPrimitive.asString) }
                .filter { it != null && !it.isAir }.toHashSet()
        }.orThrow("Missing required field $BLOCKS_C_ENTRIES")

        return EnvoyConfiguration(id, weightedDrops, world, region, envoyAmount, PolicySet(blocksEntries, blocksPolicy))
    }

    override fun serialize(obj: EnvoyConfiguration, context: SerializedSerializeContext): SerializedElement {
        throw UnsupportedOperationException("Can not serialize Envoy Configuration")
    }

    override fun getKey(): Class<*> {
        return EnvoyConfiguration::class.java
    }
}
