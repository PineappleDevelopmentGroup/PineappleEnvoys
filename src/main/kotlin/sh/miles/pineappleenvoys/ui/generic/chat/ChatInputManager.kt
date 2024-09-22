package sh.miles.pineappleenvoys.ui.generic.chat

import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

object ChatInputManager {

    private val inputs = mutableMapOf<UUID, ChatInput>()

    fun start(input: ChatInput) {
        inputs[input.player.uniqueId] = input
        input.start()
    }

    fun handleChat(event: AsyncPlayerChatEvent) {
        val input = inputs.remove(event.player.uniqueId) ?: return
        input.handleChat(event)
    }

    fun handleCommands(event: PlayerCommandPreprocessEvent) {
        val input = inputs.remove(event.player.uniqueId) ?: return
        input.handleCommands(event)
    }

    fun handleLeave(event: PlayerQuitEvent) {
        val input = inputs.remove(event.player.uniqueId) ?: return
        input.forceEnd()
    }

}
