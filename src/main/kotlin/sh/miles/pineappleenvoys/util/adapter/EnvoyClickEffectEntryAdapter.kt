package sh.miles.pineappleenvoys.util.adapter

import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter
import sh.miles.pineappleenvoys.Registries
import sh.miles.pineappleenvoys.envoy.click.EnvoyClickEffectEntry

object EnvoyClickEffectEntryAdapter : SerializedAdapter<EnvoyClickEffectEntry<*>> {

    private const val CLICK_EFFECT = "click_effect"
    private const val EFFECT = "effect"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): EnvoyClickEffectEntry<*> {
        val parent = element.asObject
        val clickEffect = Registries.CLICK_EFFECT.getUnsafe(parent.getPrimitive(CLICK_EFFECT).map { it.asString }
            .orThrow("Missing required field $CLICK_EFFECT"))
            .orThrow("No $CLICK_EFFECT entry found for provided click effect")
        val effect =
            clickEffect.deserializeEffect(parent.get(EFFECT).orThrow("Missing required field $EFFECT"), context)

        return EnvoyClickEffectEntry(clickEffect, effect)
    }

    override fun serialize(obj: EnvoyClickEffectEntry<*>, context: SerializedSerializeContext): SerializedElement {
        throw UnsupportedOperationException("Serialization of EnvoyClickEffect is not yet supported")
    }

    override fun getKey(): Class<*> {
        return EnvoyClickEffectEntry::class.java
    }
}
