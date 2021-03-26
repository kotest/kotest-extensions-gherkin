package io.kotest.extensions.gherkin

import io.cucumber.gherkin.Gherkin.fromSources
import io.cucumber.gherkin.Gherkin.makeSourceEnvelope
import io.cucumber.messages.IdGenerator.Incrementing
import io.cucumber.messages.Messages
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import java.lang.RuntimeException
import java.util.stream.Stream
import kotlin.streams.toList

/**
 * Validates upon execution that this spec matches the feature described in [featureFilePath], which should be
 * a relative path starting from the `resources` folder.
 */
class GherkinExtension(
   private val featureFilePath: String
) : TestCaseExtension, SpecExtension {

   private val executedTests = mutableListOf<String>()
   private var gherkinTestNames: List<String>


   init {
       val envelope = makeSourceEnvelope(
          javaClass.classLoader.getResourceAsStream(featureFilePath)!!.bufferedReader().readText(),
          featureFilePath
       )
      gherkinTestNames = fromSources(listOf(envelope), false, true, false, Incrementing()).toTestNames()
   }

   override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
      executedTests += testCase.description.name.name
      return execute(testCase)
   }

   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      execute(spec)
      gherkinTestNames.forEach {
         if(it !in executedTests) {
            throw GherkinException(it)
         }
      }
   }
}

private fun Stream<Messages.Envelope>.toTestNames(): List<String> {
   val list = toList()
   val features = list.map { it.gherkinDocument.feature }
   val scenarios = features.map { it.childrenList.map { it.scenario } }.flatten()

   return scenarios.flatMap { it.stepsList.map { it.text } }
}

class GherkinException(val missingStep: String) : RuntimeException("Missing step $missingStep.")
