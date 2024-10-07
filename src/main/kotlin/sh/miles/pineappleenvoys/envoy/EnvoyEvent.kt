package sh.miles.pineappleenvoys.envoy

import com.google.common.util.concurrent.Atomics
import org.bukkit.Bukkit
import sh.miles.pineapple.PineappleLib
import sh.miles.pineappleenvoys.GlobalConfig
import sh.miles.pineappleenvoys.configuration.EnvoyConfiguration
import sh.miles.pineappleenvoys.envoy.tile.EnvoyTileType
import sh.miles.pineappleenvoys.util.UnrealizedLocation
import sh.miles.pineappleenvoys.util.hourMinuteEquals
import sh.miles.pineappleenvoys.util.isNowOrBefore
import java.time.LocalTime

class EnvoyEvent(
    private val configuration: EnvoyConfiguration,
    val startTime: LocalTime,
    val endTime: LocalTime
) {

    val spec = configuration.event

    var isRunning: Boolean = false
        private set
    var isOver: Boolean = false
        private set
    var hasPrepared: Boolean = false
        private set
    var envoysLeft: Int = 0
        private set

    private var spawnLocations = Atomics.newReference<List<UnrealizedLocation>>(listOf())
    private var sentMessage = hashSetOf<EnvoyEventSpec.EventMessage>()

    fun prepare() {
        hasPrepared = true
        configuration.generateValidSpawnLocations().whenComplete { locations, exception ->
            if (exception != null) {
                PineappleLib.getLogger().severe("Spawn location scan for envoy event failed with an exception")
                exception.printStackTrace()
                return@whenComplete
            }

            spawnLocations.set(locations)
            PineappleLib.getLogger()
                .info("Spawn location scan for envoy event at ${startTime.hour}:${startTime.minute} has finished")
        }
    }

    // runs every second
    fun await() {
        val now = LocalTime.now()

        if (startTime.minusSeconds(GlobalConfig.EXPIRE_SEARCH.toLong()).isNowOrBefore(now) && !hasPrepared) {
            prepare()
        }

        for (message in spec.messages) {
            if (startTime.minusMinutes(message.minutesBefore.toLong()).hourMinuteEquals(now)) {
                if (sentMessage.contains(message)) {
                    continue
                }

                sentMessage.add(message)
                Bukkit.getServer().spigot().broadcast(message.message.component())
            }
        }
    }

    fun start() {
        if (!hasPrepared) {
            PineappleLib.getLogger()
                .severe("This envoy was not given enough time to start so no spawn locations could be gathered")
        }
        isRunning = true
        Bukkit.getServer().spigot().broadcast(spec.startMessage.component())
        val locations = spawnLocations.get().map { it.realize(configuration.world) }
        val drops = configuration.drops
        for (location in locations) {
            EnvoyTileType.place(location, this, drops.poll())
        }
        envoysLeft = locations.size
    }

    // runs every second
    fun tick() {
        val now = LocalTime.now()
        if (envoysLeft == 0 || now.hourMinuteEquals(endTime)) {
            end()
        }
    }

    fun end() {
        isRunning = false
        isOver = true
        if (envoysLeft > 0) {
            for (location in spawnLocations.get().map { it.realize(configuration.world) }) {
                EnvoyTileType.destroy(location)
            }
        }
        Bukkit.getServer().spigot().broadcast(spec.endMessage.component())
        spec.determineNextEvent(configuration)
    }

    fun decEnvoysLeft() {
        this.envoysLeft -= 1
    }
}
