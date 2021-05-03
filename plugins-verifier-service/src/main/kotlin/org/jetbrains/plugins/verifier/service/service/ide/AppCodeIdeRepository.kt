package org.jetbrains.plugins.verifier.service.service.ide

import com.jetbrains.plugin.structure.ide.IntelliJPlatformProduct
import com.jetbrains.plugin.structure.intellij.version.IdeVersion
import com.jetbrains.pluginverifier.ide.AvailableIde
import com.jetbrains.pluginverifier.ide.repositories.IdeRepository
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildConfigurationId
import org.jetbrains.teamcity.rest.TeamCityInstanceFactory
import java.net.URL

class AppCodeIdeRepository(
  private val buildServerUrl: String,
  private val configurationIds: List<String>
) : IdeRepository {

  private val teamCityInstance = TeamCityInstanceFactory.guestAuth(buildServerUrl)

  private val releaseRegex = Regex("(\\d\\d\\d\\d)\\.(\\d)(\\.\\d)?")

  private val appCodeProduct = IntelliJPlatformProduct.APPCODE

  override fun fetchIndex(): List<AvailableIde> {
    val allBuilds = configurationIds.asSequence().flatMap { configurationId ->
      teamCityInstance.builds()
        .fromConfiguration(BuildConfigurationId(configurationId))
        .withTag("Published")
        .all()
        .filter { it.buildNumber != null && IdeVersion.isValidIdeVersion(it.buildNumber!!) }
        .filter { it.finishDateTime != null }
    }.toList()
    val interesting = arrayListOf<Build>()
    interesting += allBuilds.filterNot { "EAP" in it.tags }
    val latestEap = allBuilds.filter { "EAP" in it.tags }.maxBy { IdeVersion.createIdeVersion(it.buildNumber!!) }
    if (latestEap != null) {
      interesting += latestEap
    }

    val result = arrayListOf<AvailableIde>()
    for (build in interesting) {
      val ideVersion = IdeVersion.createIdeVersion("${appCodeProduct.productCode}-${build.buildNumber}")
      val path = "${appCodeProduct.productName}-${ideVersion.asStringWithoutProductCode()}.sit"
      val artifact = build.getArtifacts().find { it.name == path }
      if (artifact != null) {
        // Tags
        // 2021.1 Published        ===> release of 2021.1
        // 2021.1 Beta Published   ===> beta (not release) of 2021.1
        val productVersion = build.tags.find { it.matches(releaseRegex) }
        val isRelease = build.tags.size == 2 && productVersion in build.tags && "Published" in build.tags
        result += AvailableIde(
          ideVersion,
          if (isRelease) productVersion else null,
          URL(buildServerUrl.trimEnd('/') + "/guestAuth/app/rest/builds/id:${build.id.stringId}/artifacts/content/$path"),
          build.finishDateTime!!.toLocalDate(),
          appCodeProduct
        )
      }
    }

    // Drop similar builds.
    return result.groupBy { "${it.version.baselineVersion}.${it.version.build}" }
      .mapValues { entry -> entry.value.maxBy { it.version }!! }
      .values
      .toList()
  }

  override fun toString(): String = "AppCodeIdeRepository at $buildServerUrl tracking ${configurationIds.joinToString()}"
}