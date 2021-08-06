package org.jetbrains.plugins.verifier.service.service.sarif

import com.jetbrains.pluginverifier.PluginVerificationResult
import com.jetbrains.pluginverifier.PluginVerificationTarget
import org.jetbrains.plugins.verifier.service.service.verifier.VerificationResultTypeDto
import org.jetbrains.plugins.verifier.service.service.verifier.convert
import org.jetbrains.plugins.verifier.service.service.verifier.convertResultType


fun PluginVerificationResult.InvalidPlugin.toSarif(): PluginVerificationResultSARIF {
  return PluginVerificationResultSARIF(
    runs = listOf(
      Runner(
        properties = toPluginVerifierPropertiesBag(),
        automationDetails = toAutomationDetails(),
        versionControlProvenance = toVersionControlProvenance(),
        invocations = toInvocationStatus(),
        tool = toToolWithRules(buildPluginStructureRules()),
        results = buildPluginStructureErrors()
      )
    )
  )
}

fun PluginVerificationResult.NotFound.toSarif(): PluginVerificationResultSARIF {
  val ruleId = "NotFound"
  return this.defaultSarifError(ruleId)
}

fun PluginVerificationResult.FailedToDownload.toSarif(): PluginVerificationResultSARIF {
  val ruleId = "FailedToDownload"
  return this.defaultSarifError(ruleId)
}


private fun PluginVerificationResult.InvalidPlugin.buildPluginStructureErrors(): List<InspectionResults> {
  return pluginStructureErrors.map {
    InspectionResults(
      ruleId = it.javaClass.canonicalName,
      level = SeverityValue.ERROR,
      message = Message(it.message),
      location = emptyList()
    )
  }
}

private fun PluginVerificationResult.InvalidPlugin.buildPluginStructureRules(): List<Rule> {
  if (pluginStructureErrors.isEmpty()) return emptyList()
  val defaultError = pluginStructureErrors.first()
  return listOf(
    Rule(
      id = defaultError.javaClass.canonicalName, // TODO
      shortDescription = Message(defaultError.description),
      fullDescription = Message(defaultError.description),
      defaultConfiguration = RuleConfiguration(
        level = SeverityValue.ERROR,
        parameters = RuleParameters(
          ideaSeverity = SeverityIdea.ERROR
        )
      )
    )
  )
}


private fun PluginVerificationResult.defaultSarifError(ruleId: String): PluginVerificationResultSARIF {
  val rule = Rule(
    id = ruleId,
    shortDescription = Message(this.verificationVerdict),
    fullDescription = Message(this.verificationVerdict),
    defaultConfiguration = RuleConfiguration(
      level = SeverityValue.ERROR,
      parameters = RuleParameters(
        ideaSeverity = SeverityIdea.ERROR
      )
    )
  )
  val results = InspectionResults(
    ruleId = ruleId,
    level = SeverityValue.ERROR,
    message = Message(this.verificationVerdict),
    location = emptyList()
  )
  return PluginVerificationResultSARIF(
    runs = listOf(
      Runner(
        properties = this.toPluginVerifierPropertiesBag(),
        automationDetails = this.toAutomationDetails(),
        versionControlProvenance = toVersionControlProvenance(),
        invocations = this.toInvocationStatus(),
        tool = toToolWithRules(listOf(rule)),
        results = listOf(results)
      )
    )
  )
}

private fun PluginVerificationResult.toInvocationStatus(): List<InvocationStatus> {
  val code = when (this) {
    is PluginVerificationResult.NotFound, is PluginVerificationResult.FailedToDownload -> 127
    is PluginVerificationResult.InvalidPlugin -> 1
    is PluginVerificationResult.Verified -> 0
  }
  val executionSuccessful = if (this is PluginVerificationResult.Verified) {
    this.convertResultType() != VerificationResultTypeDto.PROBLEMS
  } else false
  return listOf(
    InvocationStatus(
      exitCode = code,
      executionSuccessful = executionSuccessful,
      exitCodeDescription = this.verificationVerdict
    )
  )
}

private fun PluginVerificationResult.toAutomationDetails(): AutomationDetails {
  val plugin = this.plugin
  return AutomationDetails(id = plugin.presentableName + " " + plugin.presentableSinceUntilRange)
}

private fun PluginVerificationResult.toPluginVerifierPropertiesBag(): PluginVerifierPropertiesBag {
  val dependenciesGraph = if (this is PluginVerificationResult.Verified) dependenciesGraph.convert() else null
  val ideVersion = verificationTarget as PluginVerificationTarget.IDE
  val type = when (this) {
    is PluginVerificationResult.Verified -> this.convertResultType()
    is PluginVerificationResult.NotFound, is PluginVerificationResult.FailedToDownload -> VerificationResultTypeDto.NON_DOWNLOADABLE
    is PluginVerificationResult.InvalidPlugin -> VerificationResultTypeDto.INVALID_PLUGIN
  }
  return PluginVerifierPropertiesBag(
    ideVersion = ideVersion.ideVersion.asString(),
    javaVersion = ideVersion.jdkVersion.javaVersion,
    resultType = type,
    verdict = this.verificationVerdict,
    dependenciesGraph = dependenciesGraph
  )
}

private fun toVersionControlProvenance(): List<VersionControlProvenance> {
  return listOf(
    VersionControlProvenance(
      repositoryUri = "TODO()" // TODO()
    )
  )
}

private fun toToolWithRules(rules: List<Rule>): Tool {
  return Tool(Driver(rules = rules))
}