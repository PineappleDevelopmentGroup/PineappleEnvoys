package sh.miles.pineappleenvoys.util

import sh.miles.pineapple.collection.registry.RegistryKey

interface MarkedKey<K> : RegistryKey<K> {
    var dirty: Boolean
}
