package sh.miles.pineappleenvoys.command

import com.google.common.base.Objects
import com.google.common.primitives.Doubles
import com.google.common.primitives.Floats
import com.google.common.primitives.Ints
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.StringUtil
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel
import sh.miles.pineappleenvoys.ui.generic.AnvilInput

internal object EnvoysTestCommand : Command(CommandLabel("test", "pineapple-envoys.command.test")) {
    init {
        registerSubcommand(EnvoysPromptInput)
    }
}

private object EnvoysPromptInput : Command(CommandLabel("input", "pineapple-envoys.command.test.input")) {

    init {
        registerSubcommand(Anvil)
    }

    private object Anvil : Command(CommandLabel("anvil", "pineapple-envoys.command.test.input.anvil")) {
        override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
            if (sender !is Player) {
                sender.sendMessage("You must be a player to execute this")
                return true
            }

            val typeString = args[0]
            val typeMapper: (String) -> Any?
            if (typeString.equals("int")) {
                typeMapper = {
                    if (it.isNullOrBlank()) null else Ints.tryParse(it)
                }
            } else if (typeString.equals("double")) {
                typeMapper = { if (it.isNullOrBlank()) null else Doubles.tryParse(it) }
            } else if (typeString.equals("float")) {
                typeMapper = { if (it.isNullOrBlank()) null else Floats.tryParse(it) }
            } else {
                typeMapper = { it }
            }

            val input = AnvilInput(sender,
                PineappleChat.parse("<red>Prompted Input"),
                null,
                ItemStack(Material.PAPER),
                { typeMapper.invoke(it) },
                { value, player ->
                    player.sendMessage(
                        """
                    Value Class ${value::class.java.simpleName}
                    Value $value
                """.trimIndent()
                    )
                },
                { invalid, player ->
                    player.sendMessage(
                        """
                        Invalid Input $invalid 
                    """.trimIndent()
                    )
                    return@AnvilInput true
                })
            input.open()

            return true
        }

        override fun complete(sender: CommandSender, args: Array<out String>): MutableList<String> {
            if (args.size == 1) {
                return StringUtil.copyPartialMatches(
                    args[0], mutableListOf("int", "double", "float", "string"), mutableListOf()
                )
            }

            return mutableListOf()
        }
    }

}

