package sh.miles.pineappleenvoys.envoy

object EnvoyEventManager {
    private val cache = mutableSetOf<EnvoyEventSpec>()

    fun cache(event: EnvoyEventSpec) {
        cache.add(event)
    }

    fun invalidate(event: EnvoyEventSpec) {
        cache.remove(event)
    }
}
