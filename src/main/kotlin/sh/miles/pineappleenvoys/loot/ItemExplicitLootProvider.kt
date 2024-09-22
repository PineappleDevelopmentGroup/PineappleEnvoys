package sh.miles.pineappleenvoys.loot

import org.bukkit.Material
import org.bukkit.entity.Player
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext

internal object ItemExplicitLootProvider : LootProvider<ItemSpec> {
    override val icon: ItemSpec
        get() {
            val spec = ItemSpec(Material.ITEM_FRAME)
            spec.setName("<color:#ffaa00><italic:!>Simple Item Provider")
            spec.addLoreLine("<gray><italic:!>Sets the data to a basic conversion of the given item.")
            spec.addLoreLine("<gray><italic:!>this provider is best used when you're giving a basic named item or an unmodified item")
            spec.setDefaultTextMutator { PineappleChat.parse(it) }
            return spec
        }

    override val rewardClass: Class<ItemSpec> = ItemSpec::class.java

    override fun give(reward: ItemSpec, player: Player) {
        val notAdded = player.inventory.addItem(reward.buildSpec())
        val dropLocation = player.location
        notAdded.forEach { (_, item) ->
            dropLocation.world!!.dropItemNaturally(dropLocation, item)
        }
    }

    override fun asString(reward: ItemSpec): String {
        return "${reward.itemType}[name=${reward.name}]"
    }

    override fun deserializeLoot(element: SerializedElement, context: SerializedDeserializeContext): ItemSpec {
        return context.deserialize(element, ItemSpec::class.java)
    }

    override fun serializeLoot(reward: ItemSpec, context: SerializedSerializeContext): SerializedElement {
        return context.serialize(reward, ItemSpec::class.java)
    }

    override fun getKey(): String {
        return "item_explicit"
    }

}
