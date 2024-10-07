package sh.miles.pineappleenvoys.configuration.registry

import sh.miles.pineapple.collection.registry.FrozenRegistry
import sh.miles.pineapple.function.Option
import sh.miles.pineappleenvoys.envoy.click.EnvoyClickEffect
import sh.miles.pineappleenvoys.envoy.click.EnvoyFireworkLaunchClickEffect
import sh.miles.pineappleenvoys.envoy.click.EnvoySoundClickEffect
import sh.miles.pineappleenvoys.loot.LootProvider

object EnvoyClickEffectRegistry : FrozenRegistry<EnvoyClickEffect<*>, String>({
    mapOf(
        EnvoyFireworkLaunchClickEffect.key to EnvoyFireworkLaunchClickEffect,
        EnvoySoundClickEffect.key to EnvoySoundClickEffect
    )
}) {
    val FIREWORK = EnvoyFireworkLaunchClickEffect
    val SOUND = EnvoySoundClickEffect

    fun getUnsafe(key: String): Option<EnvoyClickEffect<Any>> {
        val output = registry[key] ?: return Option.none()
        return Option.some(output as EnvoyClickEffect<Any>)
    }
}
