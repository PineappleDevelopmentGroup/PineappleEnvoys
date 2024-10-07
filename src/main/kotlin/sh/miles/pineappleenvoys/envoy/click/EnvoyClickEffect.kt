package sh.miles.pineappleenvoys.envoy.click

import org.bukkit.Location
import org.bukkit.entity.Player
import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement

interface EnvoyClickEffect<E> : RegistryKey<String> {
    fun play(player: Player, location: Location, effect: E)
    fun deserializeEffect(element: SerializedElement, context: SerializedDeserializeContext): E
}
