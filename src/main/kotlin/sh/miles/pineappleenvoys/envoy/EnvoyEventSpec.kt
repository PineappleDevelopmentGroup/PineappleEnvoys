package sh.miles.pineappleenvoys.envoy

import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineappleenvoys.configuration.EnvoyConfiguration
import sh.miles.pineappleenvoys.util.hourMinuteEquals
import java.time.LocalTime

data class EnvoyEventSpec(
    val startTimes: List<LocalTime>,
    val startMessage: PineappleComponent,
    val endMessage: PineappleComponent,
    val collectMessage: PineappleComponent,
    val messages: List<EventMessage>,
    val lengthMinutes: Int
) {

    private var nextEvent: EnvoyEvent? = null

    fun tick(configuration: EnvoyConfiguration) {
        val now = LocalTime.now()
        if (nextEvent == null || nextEvent?.isOver != false) {
            nextEvent = nextEvent(configuration)
        }
        val event = nextEvent!!

        if (now.hourMinuteEquals(event.startTime) && !event.isRunning) {
            event.start()
        }

        if (event.isRunning) {
            event.tick()
        } else {
            event.await()
        }
    }

    fun startEventAt(configuration: EnvoyConfiguration, at: LocalTime) {
        nextEvent = EnvoyEvent(configuration, at, at.plusMinutes(lengthMinutes.toLong()))
    }

    fun determineNextEvent(configuration: EnvoyConfiguration) {
        nextEvent = nextEvent(configuration)
    }

    private fun nextEvent(configuration: EnvoyConfiguration): EnvoyEvent {
        val now = LocalTime.now()
        for (startTime in startTimes) {
            if (now.isBefore(startTime)) continue
            return EnvoyEvent(configuration, startTime, startTime.plusMinutes(lengthMinutes.toLong()))
        }

        val first = startTimes.first()
        return EnvoyEvent(configuration, first, first.plusMinutes(lengthMinutes.toLong()))
    }

    data class EventMessage(val minutesBefore: Int, val message: PineappleComponent) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is EventMessage) return false

            if (minutesBefore != other.minutesBefore) return false
            if (message != other.message) return false

            return true
        }

        override fun hashCode(): Int {
            var result = minutesBefore
            result = 31 * result + message.source.hashCode()
            return result
        }
    }
}
