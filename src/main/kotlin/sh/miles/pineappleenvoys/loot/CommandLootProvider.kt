package sh.miles.pineappleenvoys.loot

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext

internal object CommandLootProvider : LootProvider<String> {
    override val icon: ItemSpec
        get() {
            val spec = ItemSpec(Material.COMMAND_BLOCK)
            spec.setName("<gold><italic:!>Command Provider")
            spec.addLoreLine("<gray><italic:!>Executes a command when this provider is won")
            spec.setDefaultTextMutator { PineappleChat.parse(it) }
            return spec
        }
    override val rewardClass: Class<String> = String::class.java
    private val server = Bukkit.getServer()
    private val console = Bukkit.getServer().consoleSender

    override fun give(reward: String, player: Player) {
        server.dispatchCommand(console, reward.replace("%player_name%", player.displayName))
    }

    override fun asString(reward: String): String {
        return "/$reward"
    }

    override fun deserializeLoot(element: SerializedElement, context: SerializedDeserializeContext): String {
        return element.asPrimitive.asString
    }

    override fun serializeLoot(reward: String, context: SerializedSerializeContext): SerializedElement {
        return SerializedElement.primitive(reward)
    }

    override fun getKey(): String {
        return "command"
    }
}
