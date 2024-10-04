package sh.miles.pineappleenvoys

import sh.miles.pineapple.config.annotation.ConfigPath

object GlobalConfig {
    @ConfigPath("create-examples")
    val CREATE_EXAMPLES = true

    @ConfigPath("expire-search")
    val EXPIRE_SEARCH = 120
}
