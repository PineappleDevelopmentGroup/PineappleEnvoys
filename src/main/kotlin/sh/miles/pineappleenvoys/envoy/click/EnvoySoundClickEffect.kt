package sh.miles.pineappleenvoys.envoy.click

import org.bukkit.Location
import org.bukkit.entity.Player
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.spec.SoundSpec

object EnvoySoundClickEffect : EnvoyClickEffect<SoundSpec> {
    override fun play(player: Player, location: Location, effect: SoundSpec) {
        effect.play(location)
    }

    override fun deserializeEffect(element: SerializedElement, context: SerializedDeserializeContext): SoundSpec {
        return context.deserialize(element, SoundSpec::class.java)
    }

    override fun getKey(): String {
        return "sound"
    }
}
