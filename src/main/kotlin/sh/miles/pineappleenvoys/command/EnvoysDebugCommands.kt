package sh.miles.pineappleenvoys.command

import com.google.common.primitives.Ints
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some
import sh.miles.pineappleenvoys.GlobalConfig
import sh.miles.pineappleenvoys.Registries
import sh.miles.pineappleenvoys.envoy.tile.EnvoyTile
import sh.miles.pineappleenvoys.envoy.tile.EnvoyTileType
import java.util.stream.IntStream
import kotlin.streams.toList

internal object EnvoysDebugCommand : Command(CommandLabel("debug", "pineapple-envoys.command.debug")) {

    init {
        registerSubcommand(EnvoysDebugLoot)
        registerSubcommand(EnvoysDebugEvent)
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

            val arg = if (args.isEmpty()) "none" else args[0]
            when (val lootConfiguration = Registries.LOOT.get(arg)) {
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
                            "<red>Failed to generate loot because no such table $arg exists"
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

private object EnvoysDebugEvent : Command(CommandLabel("event", "pineapple-envoys.command.debug.event")) {

    init {
        registerSubcommand(DebugEnvoysPlace)
        registerSubcommand(DebugEnvoysEventScan)
    }

    private object DebugEnvoysPlace : Command(CommandLabel("place", "pineapple-envoys.command.debug.event.place")) {
        override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
            if (sender !is Player) {
                sender.sendMessage("Only players can run /pineapple-envoys event place <envoy-configuration>")
                return true
            }

            val arg = if (args.isEmpty()) "none" else args[0]
            when (val envoyOption = Registries.ENVOYS.get(arg)) {
                is Some -> {
                    val envoy = envoyOption.some()
                    sender.spigot().sendMessage(
                        PineappleChat.parse(
                            "<green>Starting envoy scan this could take up to ${GlobalConfig.EXPIRE_SEARCH} seconds"
                        )
                    )

                    val target = sender.getTargetBlockExact(10)
                    if (target == null) {
                        sender.spigot().sendMessage(
                            PineappleChat.parse(
                                "<red>No valid block targeted within a 10 block range for placement"
                            )
                        )
                        return true
                    }

                    EnvoyTileType.place(target.location, envoy.drops.poll())
                }

                is None -> {
                    sender.spigot().sendMessage(
                        PineappleChat.parse(
                            "<red>Failed to generate loot because no such envoy $arg exists"
                        )
                    )
                }
            }
            return true
        }

        override fun complete(sender: CommandSender, args: Array<out String>): MutableList<String> {
            if (args.size == 1) {
                return StringUtil.copyPartialMatches(args[0], Registries.ENVOYS.keys().toList(), mutableListOf())
            }

            return mutableListOf()
        }
    }

    private object DebugEnvoysEventScan : Command(CommandLabel("scan", "pineapple-envoys.command.debug.event.scan")) {

        override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
            if (sender !is Player) {
                sender.sendMessage("Only players can run /pineapple-envoys event scan <envoy-configuration>")
                return true
            }

            val arg = if (args.isEmpty()) "none" else args[0]
            when (val envoyOption = Registries.ENVOYS.get(arg)) {
                is Some -> {
                    val envoy = envoyOption.some()
                    sender.spigot().sendMessage(
                        PineappleChat.parse(
                            "<green>Starting envoy scan this could take up to ${GlobalConfig.EXPIRE_SEARCH} seconds"
                        )
                    )
                    envoy.generateValidSpawnLocations().whenComplete { result, exception ->
                        if (exception != null) {
                            PineappleChat.parse(
                                "<red>Envoy scan failed ${exception.message} see console for more details"
                            )
                            throw RuntimeException(exception)
                        }

                        val builder = StringBuilder().append("<green>Scanned Locations Found")
                            .append("\n<gray><italic>Report Start\n")
                        for (unrealizedLocation in result) {
                            builder.append("<dark_gray>- <gold>${unrealizedLocation.x}<dark_gray>,<gold>${unrealizedLocation.y}<dark_gray>,<gold>${unrealizedLocation.z}")
                                .append("\n")
                        }
                        builder.append("<gray><italic>Report End")
                        sender.spigot().sendMessage(
                            PineappleChat.parse(builder.toString())
                        )
                    }
                }

                is None -> {
                    sender.spigot().sendMessage(
                        PineappleChat.parse(
                            "<red>Failed to generate loot because no such envoy $arg exists"
                        )
                    )
                }
            }


            return true
        }

        override fun complete(sender: CommandSender, args: Array<out String>): MutableList<String> {
            if (args.size == 1) {
                return StringUtil.copyPartialMatches(args[0], Registries.ENVOYS.keys().toList(), mutableListOf())
            }

            return mutableListOf()
        }
    }
}
