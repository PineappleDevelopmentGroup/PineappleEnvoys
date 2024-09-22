package sh.miles.pineappleenvoys.configuration.registry

import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.collection.registry.FrozenRegistry
import sh.miles.pineapple.function.Option
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineappleenvoys.loot.CommandLootProvider
import sh.miles.pineappleenvoys.loot.ItemBase64LootProvider
import sh.miles.pineappleenvoys.loot.ItemExplicitLootProvider
import sh.miles.pineappleenvoys.loot.LootProvider

object LootProviderRegistry : FrozenRegistry<LootProvider<*>, String>({
    mapOf(
        ItemExplicitLootProvider.key to ItemExplicitLootProvider,
        ItemBase64LootProvider.key to ItemBase64LootProvider,
        CommandLootProvider.key to CommandLootProvider
    )
}) {
    val ITEM_PROVIDER: LootProvider<ItemSpec> = ItemExplicitLootProvider
    val ITEM_BASE64_PROVIDER: LootProvider<ItemStack> = ItemBase64LootProvider
    val COMMAND_PROVIDER: LootProvider<String> = CommandLootProvider

    fun getUnsafe(key: String): Option<LootProvider<Any>> {
        val output = registry[key] ?: return Option.none()
        return Option.some(output as LootProvider<Any>)
    }

    fun getUnsafeOrNull(key: String): LootProvider<Any>? {
        val output = registry[key] ?: return null
        return output as LootProvider<Any>
    }
}
