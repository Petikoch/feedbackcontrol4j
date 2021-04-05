Here's the draft for the release notes of the next release in this branch:

* Features
  * No new features
* Bugfixes
  * No bug fixes
* Changes
  * Minimum Java version 8 (instead of 7)
  * Update gradle wrapper to 6.8.2 (from 3.1)
  * Remove Groovy and Sprock from the tests and use JUnit 5, AssertJ and Awaitility instead
  * Publish to Maven Central instead Bintray/JCenter (Bintray/JCenter will be shut down soon)
  * Simplify build setup: remove Jacoco and Coveralls