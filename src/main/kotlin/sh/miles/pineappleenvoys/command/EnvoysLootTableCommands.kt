package sh.miles.pineappleenvoys.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel
import sh.miles.pineappleenvoys.Registries
import sh.miles.pineappleenvoys.ui.editor.LootTableEditor

internal object EnvoysLootCommand : Command(CommandLabel("loot", "pineapple-envoys.command.loot")) {
    init {
        registerSubcommand(EnvoysLootEditCommand)
    }
}

private object EnvoysLootEditCommand : Command(CommandLabel("edit", "pineapple-envoys.command.loot")) {
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to execute this command")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("You must provide an argument")
            return true
        }

        val configuration = Registries.LOOT.getOrNull(args[0])
        if (configuration == null) {
            sender.sendMessage("No such loot configuration ${args[0]} exists")
            return true
        }

        val editor = LootTableEditor(sender, configuration)
        editor.open()
        return true
    }

    override fun complete(sender: CommandSender, args: Array<out String>): MutableList<String> {
        if (args.size == 1) {
            return Registries.LOOT.keys().toMutableList()
        }

        return mutableListOf()
    }
}
