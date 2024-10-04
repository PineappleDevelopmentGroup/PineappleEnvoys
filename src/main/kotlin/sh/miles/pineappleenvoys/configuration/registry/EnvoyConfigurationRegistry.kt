package sh.miles.pineappleenvoys.configuration.registry

import sh.miles.pineapple.collection.registry.WriteableRegistry
import sh.miles.pineappleenvoys.configuration.EnvoyConfiguration

object EnvoyConfigurationRegistry : WriteableRegistry<EnvoyConfiguration, String>()
