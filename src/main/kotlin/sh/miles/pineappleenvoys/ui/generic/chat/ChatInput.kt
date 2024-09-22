package sh.miles.pineappleenvoys.ui.generic.chat

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.scheduler.BukkitTask
import sh.miles.pineappleenvoys.PineappleEnvoysPlugin

data class ChatInput(
    val player: Player,
    val prompt: BaseComponent,
    val timeout: Long,
    val escapeSequence: String,
    val onAnswer: (String) -> Unit,
    val onExit: () -> Unit,
) {

    private var timeoutTask: BukkitTask? = null

    fun start() {
        player.spigot().sendMessage(prompt)
        timeoutTask = Bukkit.getScheduler().runTaskLater(PineappleEnvoysPlugin.plugin, Runnable {
            onExit.invoke()
        }, timeout)
    }

    fun handleChat(event: AsyncPlayerChatEvent) {
        if (event.message == escapeSequence) {
            forceEnd()
        } else {
            sync {
                onAnswer.invoke(event.message)
                timeoutTask?.cancel()
            }
            event.recipients.removeIf { it != event.player }
        }
    }

    fun handleCommands(event: PlayerCommandPreprocessEvent) {
        event.isCancelled = true
        if (event.message == escapeSequence) {
            forceEnd()
        } else {
            onAnswer.invoke(event.message.replace("/", ""))
            timeoutTask?.cancel()
        }
    }

    fun forceEnd() {
        timeoutTask?.cancel()
        onExit.invoke()
    }

    private fun sync(runnable: () -> Unit) {
        Bukkit.getScheduler().runTask(PineappleEnvoysPlugin.plugin, Runnable {
            runnable.invoke()
        })
    }

}
