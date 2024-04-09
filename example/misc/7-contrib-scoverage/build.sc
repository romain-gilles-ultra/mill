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
// version can be change if there is newer ones. This will all use to
// call the scoverage targets to produce coverage reports.
// Then the sub test module extends `ScoverageTests` to transform the
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
//
// The source code for this module lives in the `src/` folder.
// Output for this module (compiled files, resolved dependency lists, ...)
// lives in `out/`.
//
// This example project uses two third-party dependencies - MainArgs for CLI
// argument parsing, Scalatags for HTML generation - and uses them to wrap a
// given input string in HTML templates with proper escaping.
//
// You can run `assembly` to generate a standalone executable jar, which then
// can be run from the command line or deployed to be run elsewhere.

/** Usage

> ./mill resolve _ # List what tasks are available to run
assembly
...
clean
...
compile
...
run
...
show
...
inspect
...

> ./mill inspect compile # Show documentation and inputs of a task
compile(ScalaModule.scala:...)
    Compiles the current module to generate compiled classfiles/bytecode.
Inputs:
    scalaVersion
    upstreamCompileOutput
    allSourceFiles
    compileClasspath

> ./mill compile # compile sources into classfiles
...
compiling 1 Scala source to...

> ./mill run # run the main method, if any
error: Missing argument: --text <str>

> ./mill run --text hello
<h1>hello</h1>

> ./mill test
...
+ foo.FooTests.simple ...  <h1>hello</h1>
+ foo.FooTests.escaping ...  <h1>&lt;hello&gt;</h1>

> ./mill assembly # bundle classfiles and libraries into a jar for deployment

> ./mill show assembly # show the output of the assembly task
".../out/assembly.dest/out.jar"

> java -jar ./out/assembly.dest/out.jar --text hello
<h1>hello</h1>

> ./out/assembly.dest/out.jar --text hello # mac/linux
<h1>hello</h1>

*/

// The output of every Mill task is stored in the `out/` folder under a name
// corresponding to the task that created it. e.g. The `assembly` task puts its
// metadata output in `out/assembly.json`, and its output files in
// `out/assembly.dest`. You can also use `show` to make Mill print out the
// metadata output for a particular task.
//
// Additional Mill tasks you would likely need include:
//
// [source,bash]
// ----
// $ mill runBackground # run the main method in the background
//
// $ mill clean <task>  # delete the cached output of a task, terminate any runBackground
//
// $ mill launcher      # prepares a foo/launcher.dest/run you can run later
//
// $ mill jar           # bundle the classfiles into a jar suitable for publishing
//
// $ mill -i console    # start a Scala console within your project
//
// $ mill -i repl       # start an Ammonite Scala REPL within your project
// ----
//
// You can run `+mill resolve __+` to see a full list of the different tasks that
// are available, `+mill resolve _+` to see the tasks within `foo`,
// `mill inspect compile` to inspect a task's doc-comment documentation or what
// it depends on, or `mill show foo.scalaVersion` to show the output of any task.
//
// The most common *tasks* that Mill can run are cached *targets*, such as
// `compile`, and un-cached *commands* such as `foo.run`. Targets do not
// re-evaluate unless one of their inputs changes, whereas commands re-run every
// time.
