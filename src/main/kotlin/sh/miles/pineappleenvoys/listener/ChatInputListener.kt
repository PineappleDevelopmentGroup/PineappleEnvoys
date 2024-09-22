package sh.miles.pineappleenvoys.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerQuitEvent
import sh.miles.pineappleenvoys.ui.generic.chat.ChatInputManager

object ChatInputListener : Listener {

    @EventHandler
    fun onPlayerTalk(event: AsyncPlayerChatEvent) {
        ChatInputManager.handleChat(event)
    }

    @EventHandler
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        ChatInputManager.handleCommands(event)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        ChatInputManager.handleLeave(event)
    }

}
