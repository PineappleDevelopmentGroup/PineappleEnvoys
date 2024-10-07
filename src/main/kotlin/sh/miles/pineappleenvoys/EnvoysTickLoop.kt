package sh.miles.pineappleenvoys

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import sh.miles.pineapple.task.work.ServerThreadTicker

class EnvoysTickLoop(private val ticker: ServerThreadTicker) : Runnable {

    private val checkEvery = 1
    private var tickCount = 0

    private val registry = Registries.ENVOYS

    override fun run() {
        this.ticker.run()

        if (tickCount >= checkEvery * 20) {
            for (key in registry.keys()) {
                val configuration = registry.getOrNull(key)!!
                configuration.event.tick(configuration)
            }
            tickCount = 0
        } else tickCount++

    }

    fun start(plugin: Plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 1L)
    }

}
