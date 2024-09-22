package sh.miles.pineappleenvoys.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some
import sh.miles.pineappleenvoys.Registries

internal object EnvoysDebugCommand : Command(CommandLabel("debug", "pineapple-envoys.command.debug")) {

    init {
        registerSubcommand(EnvoysDebugLoot)
    }

}

private object EnvoysDebugLoot : Command(CommandLabel("loot", "pineapple-envoys.command.debug.loot")) {

    init {
        registerSubcommand(EnvoysDebugLootGive)
    }

    private object EnvoysDebugLootGive : Command(CommandLabel("give", "pineapple-envoys.command.debug.loot.give")) {
        override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
            if (sender !is Player) {
                sender.sendMessage("Only players can run /pineapple-envoys debug loot give <arg>")
                return true
            }

            when (val lootConfiguration = Registries.LOOT.get(args[0])) {
                is Some -> {
                    val entry = lootConfiguration.some().loot.poll()
                    entry.giveLoot(sender)
                    sender.spigot().sendMessage(
                        PineappleChat.parse(
                            "<green>Given loot to player from loot provider type ${entry.provider.key}"
                        )
                    )
                }

                is None -> {
                    sender.spigot().sendMessage(
                        PineappleChat.parse(
                            "<red>Failed to generate loot because no such table ${args[0]} exists"
                        )
                    )
                }
            }
            return true
        }

        override fun complete(sender: CommandSender, args: Array<out String>): MutableList<String> {
            if (args.size == 1) {
                return StringUtil.copyPartialMatches(args[0], Registries.LOOT.keys().toMutableList(), mutableListOf())
            }

            return mutableListOf()
        }
    }

}
