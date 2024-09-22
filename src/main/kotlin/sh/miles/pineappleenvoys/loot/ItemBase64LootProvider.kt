package sh.miles.pineappleenvoys.loot

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext

internal object ItemBase64LootProvider : LootProvider<ItemStack> {

    override val icon: ItemSpec
        get() {
            val spec = ItemSpec(Material.JIGSAW)
            spec.setName("<color:#AC00E6><italic:!>Custom Item Provider")
            spec.addLoreLine("<gray><italic:!>Sets the loot to a custom item with all of its relevant data when this provider is won")
            spec.addLoreLine("<gray><italic:!>this provider is best used when you want to give out an item from another plugin")
            spec.setDefaultTextMutator { PineappleChat.parse(it) }
            return spec
        }
    override val rewardClass: Class<ItemStack> = ItemStack::class.java

    override fun give(reward: ItemStack, player: Player) {
        val notAdded = player.inventory.addItem(reward.clone())
        val dropLocation = player.location
        notAdded.forEach { (_, item) ->
            dropLocation.world!!.dropItemNaturally(dropLocation, item)
        }
    }

    override fun asString(reward: ItemStack): String {
        return "${reward.type}[name=${reward.itemMeta!!.displayName}"
    }

    override fun deserializeLoot(element: SerializedElement, context: SerializedDeserializeContext): ItemStack {
        return PineappleLib.getNmsProvider()
            .itemsFromBytes(java.util.Base64.getDecoder().decode(element.asPrimitive.asString), 1).first()
    }

    override fun serializeLoot(reward: ItemStack, context: SerializedSerializeContext): SerializedElement {
        return SerializedElement.primitive(
            java.util.Base64.getEncoder().encodeToString(PineappleLib.getNmsProvider().itemsToBytes(listOf(reward)))
        )
    }

    override fun getKey(): String {
        return "item_base64"
    }
}
