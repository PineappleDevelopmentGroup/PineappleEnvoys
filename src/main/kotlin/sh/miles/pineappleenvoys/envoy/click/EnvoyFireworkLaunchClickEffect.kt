package sh.miles.pineappleenvoys.envoy.click

import org.bukkit.Location
import org.bukkit.entity.Player
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.spec.FireworkSpec

object EnvoyFireworkLaunchClickEffect : EnvoyClickEffect<FireworkSpec> {
    override fun play(player: Player, location: Location, effect: FireworkSpec) {
        effect.spawn(location)
    }

    override fun deserializeEffect(element: SerializedElement, context: SerializedDeserializeContext): FireworkSpec {
        return context.deserialize(element, FireworkSpec::class.java)
    }

    override fun getKey(): String {
        return "firework"
    }
}
