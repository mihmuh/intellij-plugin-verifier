package com.jetbrains.pluginverifier.dependencies.resolution

import com.jetbrains.plugin.structure.ide.Ide
import com.jetbrains.plugin.structure.intellij.plugin.PluginDependency
import com.jetbrains.pluginverifier.plugin.PluginDetailsCache
import com.jetbrains.pluginverifier.repository.bundled.BundledPluginsRepository

/**
 * [DependencyFinder] that searches for the plugin
 * among the [bundled] [Ide.getBundledPlugins] [ide] plugins.
 */
class BundledPluginDependencyFinder(val ide: Ide,
                                    private val pluginDetailsCache: PluginDetailsCache) : DependencyFinder {

  override fun findPluginDependency(dependency: PluginDependency): DependencyFinder.Result {
    val pluginsRepository = BundledPluginsRepository(ide, ide.idePath.toURI().toURL())
    val id = dependency.id
    val bundledPluginInfo = if (dependency.isModule) {
      pluginsRepository.findPluginByModule(id)
    } else {
      pluginsRepository.findPluginById(id)
    }

    if (bundledPluginInfo != null) {
      if (dependency.isModule && id in bundledPluginInfo.idePlugin.definedModules) {
        /**
         * Skip the module dependency on itself. An example is the IDEA CORE plugin
         * that declares a module 'com.intellij.modules.lang' that is contained in the IDEA CORE:
         *
         * PlatformLangPlugin.xml (declares module 'com.intellij.modules.lang') ->
         *  x-include /idea/RichPlatformPlugin.xml ->
         *  x-include /META-INF/DesignerCorePlugin.xml ->
         *  depends on module 'com.intellij.modules.lang'
         */
        return DependencyFinder.Result.Skip
      }
      return DependencyFinder.Result.DetailsProvided(pluginDetailsCache.getPluginDetailsCacheEntry(bundledPluginInfo))
    }
    return DependencyFinder.Result.NotFound("Dependency $id is not found among the bundled plugins of $ide")
  }

}