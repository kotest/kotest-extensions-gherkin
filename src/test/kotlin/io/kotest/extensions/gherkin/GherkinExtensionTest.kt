package io.kotest.extensions.gherkin

import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GherkinExtensionTest : FreeSpec({

   extension(GherkinExtension("abc.feature"))
   extension(GherkinExtension("background.feature"))

   "Should work with nested tests" - {
      "A" - {
        "B" - {
           "C" {
              // Success!
           }
        }
      }
   }

   "Should work with a 'background scenario'" - {
      "Background" - {

         "D" - {
            "E" {

            }
         }

         "F" - {
            "G" {

            }
         }
      }
   }
})

class GherkinExtensionFailureTest : FunSpec({

   extensions(expectFailureExtension, GherkinExtension("foo.feature"))

   test("FooBar") {

   }

   test("BootFar") {

   }

   test("NotLastTest") {

   }

})

private val expectFailureExtension = object : SpecExtension {
   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      try {
         execute(spec)
      } catch (gherkinException: GherkinException) {
         gherkinException.missingStep shouldBe "ZootZat"
         return
      }
      throw RuntimeException("Should fail, but didn't")
   }
}
