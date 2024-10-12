package sh.miles.pineappleenvoys.command

import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some
import sh.miles.pineappleenvoys.GlobalConfig
import sh.miles.pineappleenvoys.Registries
import sh.miles.pineappleenvoys.envoy.EnvoyEvent
import java.time.LocalTime

object EnvoysStartCommand : Command(CommandLabel("start", "pineapple-envoys.command.start")) {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.spigot().sendMessage(
                GlobalConfig.COMMAND_START_NO_ARGUMENTS_GIVEN.component(
                    mutableMapOf<String, Any>(
                        "usage" to "/pineapple-envoy start <envoy-id>"
                    )
                )
            )
            return true
        }

        when (val envoy = Registries.ENVOYS.get(args[0])) {
            is Some -> {
                envoy.some().event.startEventAt(
                    envoy.some(),
                    LocalTime.now().plusSeconds(GlobalConfig.ENVOY_START_DELAY.toLong())
                )
                sender.spigot().sendMessage(
                    GlobalConfig.COMMAND_START_ANNOUNCEMENT.component(
                        mutableMapOf<String, Any>(
                            "seconds" to GlobalConfig.ENVOY_START_DELAY
                        )
                    )
                )
            }

            is None -> {
                sender.spigot().sendMessage(
                    GlobalConfig.COMMAND_START_INVALID_ENVOY.component(
                        mutableMapOf<String, Any>(
                            "id" to args[0]
                        )
                    )
                )
            }
        }

        return true
    }

    override fun complete(sender: CommandSender, args: Array<out String>): MutableList<String> {
        if (args.size == 1) {
            return StringUtil.copyPartialMatches(args[0], Registries.ENVOYS.keys(), mutableListOf())
        }

        return mutableListOf()
    }

}
