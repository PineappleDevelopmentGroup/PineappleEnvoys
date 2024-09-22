package sh.miles.pineappleenvoys.command

import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel

internal object EnvoysCommand : Command(
    CommandLabel(
        "pineapple-envoys", "pineapple-envoys.command", "The main pineapple envoys command", listOf("envoys")
    )
) {
    init {
        registerSubcommand(EnvoysDebugCommand)
        registerSubcommand(EnvoysLootCommand)

        // TESTING
        registerSubcommand(EnvoysTestCommand)
        // END TESTING
    }
}
