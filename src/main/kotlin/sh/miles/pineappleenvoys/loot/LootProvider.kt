package sh.miles.pineappleenvoys.loot

import org.bukkit.entity.Player
import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext

interface LootProvider<R> : RegistryKey<String> {
    val icon: ItemSpec
    val rewardClass: Class<R>

    fun give(reward: R, player: Player)

    fun asString(reward: R): String

    fun deserializeLoot(element: SerializedElement, context: SerializedDeserializeContext): R

    fun serializeLoot(reward: R, context: SerializedSerializeContext): SerializedElement
}
