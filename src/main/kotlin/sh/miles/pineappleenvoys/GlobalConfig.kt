package sh.miles.pineappleenvoys

import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.config.annotation.Comment
import sh.miles.pineapple.config.annotation.ConfigPath

object GlobalConfig {

    @ConfigPath("create-examples")
    @Comment("Weather or not to create the example files, if this is false the default example files will not be generated")
    val CREATE_EXAMPLES = true

    @ConfigPath("expire-search")
    @Comment("Determines how long the envoy event can search for spawn locations for.")
    val EXPIRE_SEARCH = 120

    @ConfigPath("envoy-start-delay")
    @Comment("Determines how long between the use of /pineapple-envoy start <envoy-id> and the start of the envoy.")
    @Comment("Note that if the envoy can not find the spawn locations within this time the envoy will fail to start.")
    val ENVOY_START_DELAY = 60

    @ConfigPath("messages.command.no_arguments_given")
    val COMMAND_START_NO_ARGUMENTS_GIVEN: PineappleComponent =
        PineappleChat.component("<red>You must provide a valid argument <\$usage>!")

    @ConfigPath("messages.command.start.invalid_envoy")
    val COMMAND_START_INVALID_ENVOY: PineappleComponent = PineappleChat.component("<red>Unable to find envoy <\$id>!")

    @ConfigPath("messages.command.start.start_announcement")
    val COMMAND_START_ANNOUNCEMENT: PineappleComponent =
        PineappleChat.component("<green>An envoy event will begin in <\$seconds> seconds")
}
