import mill._, scalalib._
import $ivy.`com.lihaoyi::mill-contrib-scoverage:`

import mill.contrib.scoverage._

object foo extends RootModule with ScoverageModule {
  def scoverageVersion = "2.1.0"
  def scalaVersion = "2.13.11"
  def ivyDeps = Agg(
    ivy"com.lihaoyi::scalatags:0.12.0",
    ivy"com.lihaoyi::mainargs:0.6.2"
  )

  object test extends ScoverageTests /*with TestModule.Utest */{
    def ivyDeps = Agg(ivy"com.lihaoyi::utest:0.7.11")
    def testFramework = "utest.runner.Framework"
  }
}

// This is a basic Mill build for a single `ScalaModule`, enhanced with
// Scoverage plugin. The root module extends the `ScoverageModule` and
// specifies the version of scoverage version to use here: `2.1.0`. This
// version can be changed if there is a newer one. Now you can call the
// scoverage targets to produce coverage reports.
// The sub test module extends `ScoverageTests` to transform the
// execution of the various testXXX targets to use scoverage and produce
// coverage data.
// This lets us perform the coverage operations but before that you
// must first run the test.
// `./mill test` then `./mill scoverage.consoleReport` and get your
// coverage into your console output.
//
// You can download this example project using the *download* link above
// if you want to try out the commands below yourself. The only requirement is
// that you have some version of the JVM installed; the `./mill` script takes
// care of any further dependencies that need to be downloaded.

/** Usage

> ./mill test # Run the tests and produce the coverage data
...
+ foo.FooTests.simple ...  <h1>hello</h1>
+ foo.FooTests.escaping ...  <h1>&lt;hello&gt;</h1>

> ./mill result scoverage._ # List what tasks are available to run from scoverage
...
scoverage.consoleReport
...
scoverage.htmlReport
...
scoverage.xmlCoberturaReport
...
scoverage.xmlReport
...

> ./mill scoverage.consoleReport
...
Statement coverage.: 16.67%
Branch coverage....: 100.00%
*/
