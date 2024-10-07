package sh.miles.pineappleenvoys.util.adapter

import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter
import sh.miles.pineappleenvoys.envoy.EnvoyEventSpec
import java.time.Duration
import java.time.LocalTime
import java.util.stream.Collectors

object EnvoyEventSpecAdapter : SerializedAdapter<EnvoyEventSpec> {

    private const val TIMES = "times"
    private const val EVENT_LENGTH = "event_length"
    private const val START_MESSAGE = "start_message"
    private const val END_MESSAGE = "end_message"
    private const val ENVOY_COLLECT_MESSAGE = "envoy_collect_message"
    private const val MESSAGES = "messages"
    private const val MESSAGES_MESSAGE = "message"
    private const val MESSAGES_MINUTES_BEFORE = "before"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): EnvoyEventSpec {
        val parent = element.asObject
        val length = parent.getPrimitive(EVENT_LENGTH).map { it.asPrimitive.asInt }
            .orThrow("Missing required field $EVENT_LENGTH")
        val times = parent.getArray(TIMES).map {
            it.stream().map { element ->
                val split = element.asPrimitive.asString.split(":")
                LocalTime.of(split[0].toInt(), split[1].toInt())
            }.sorted().collect(Collectors.toList())
        }.orThrow("Missing required field $TIMES")
        val startMessage = parent.get(START_MESSAGE).map { context.deserialize(it, PineappleComponent::class.java) }
            .orThrow("Missing required field $START_MESSAGE")
        val endMessage = parent.get(END_MESSAGE).map { context.deserialize(it, PineappleComponent::class.java) }
            .orThrow("Missing required field $END_MESSAGE")
        val collectMessage =
            parent.get(ENVOY_COLLECT_MESSAGE).map { context.deserialize(it, PineappleComponent::class.java) }
                .orThrow("Missing required field $ENVOY_COLLECT_MESSAGE")
        val messages = parent.getArray(MESSAGES).map {
            it.stream().map { element ->
                val message = element.asObject
                EnvoyEventSpec.EventMessage(message.getPrimitive(MESSAGES_MINUTES_BEFORE).map { it.asPrimitive.asInt }
                    .orThrow("Missing required field $MESSAGES_MINUTES_BEFORE"), message.get(
                    MESSAGES_MESSAGE
                ).map { context.deserialize(it, PineappleComponent::class.java) }
                    .orThrow("Missing required field $MESSAGES_MESSAGE"))
            }.toList()
        }.orElse(listOf())

        lateinit var next: LocalTime
        var last: LocalTime? = null
        val iterator = times.iterator()
        while (iterator.hasNext()) {
            next = iterator.next()
            if (last == null) {
                last = next
                continue
            }

            val duration = Duration.between(last, next)
            if (duration.toMinutes() < length) {
                PineappleLib.getLogger()
                    .info("Removing event at ${next.hour}:${next.minute} because it conflicts with the event at ${last.hour}:${last.minute}")
                iterator.remove()
            }
        }

        return EnvoyEventSpec(times, startMessage, endMessage, collectMessage, messages, length)
    }

    override fun serialize(obj: EnvoyEventSpec, context: SerializedSerializeContext): SerializedElement {
        throw UnsupportedOperationException("serializing EnvoyEventSpec isn't currently supported")
    }

    override fun getKey(): Class<*> {
        return EnvoyEventSpec::class.java
    }
}
