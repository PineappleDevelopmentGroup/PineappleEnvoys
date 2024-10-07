package sh.miles.pineappleenvoys.envoy.click

import org.bukkit.Location
import org.bukkit.entity.Player

class EnvoyClickEffectEntry<E>(private val clickEffect: EnvoyClickEffect<E>, private val effect: E) {

    fun play(player: Player, location: Location) {
        clickEffect.play(player, location, effect)
    }

}
