# Polylith + Integrant

This example demonstrates a basic setup of a stateful system (implemented as a
[Polylith](https://polylith.gitbook.io/) `base`) whose lifecycle is handled by
the [Integrant](https://github.com/weavejester/integrant). It caters for both
in-REPL development and production use cases.

It also attempts to address some of the most frequently asked questions about
system design in this particular setting, namely:

- What goes where? What's in the `base`? What can/should be a component?
- What should (not) go into the Integrant system? (IMHO, only stateful objects.)
- How do Integrant components (keys) map to their Polylith counterparts, if any?
- How to deal with configuration, application context and dependency injection?
- How to ensure that everything is testable, both in isolation and as a system?


## Prerequisites

The [`poly` tool](https://polylith.gitbook.io/poly) must be installed locally.

### PostgreSQL

There are two separate instances of Postgres used in this example application:

1. The one that is used in the target environment, i.e. locally and elsewhere.
   For [local exploration and development](#in-repl), make sure that you have
   launched a local PostgreSQL server (in your favourite way) and have created
   a database and, optionally, a user role for the app before starting the app
   system.

2. An embedded PostgreSQL used for [testing](#testing). This one gets launched
   by the test Integrant system, which is therefore located in the `test` dir
   of the `app` base, in order to demonstrate an optional Integrant component.

### Env Vars

The following environment variables are used:

| Env Var           | Required     | Description                                                             |
|-------------------|--------------|-------------------------------------------------------------------------|
| `PROFILE`         | in non-local | Environment, e.g. "dev", "prod" (think Leiningen profile, not Polylith) |
| `DATABASE_URL`    | in any env   | A JDBC URL for PostgreSQL used by the application (not used in tests)   |

In a local development environment setting the `PROFILE` var is not necessary,
since the app config is loaded with the `:dev` param by default in `user.clj`.
Still, don't forget to set it in case if you want to [launch](#launching) the
app via the `-main` entrypoint locally.


## System Components

An example application employs several single-purpose components to do its job.

Unfortunately, the term "component" becomes overloaded in the current context.
It can both mean a Polylith component (a type of brick) and an Integrant system
component (a.k.a. a "key" in Integrant's parlance). In order not to go nuts and
at the same time not to introduce new terms further complicating understanding,
we will use this term with qualifiers — "stateful" and "stateless".

By "stateful" we mean components that are part of the Integrant system (used at
runtime) and that may also have Polylith counterparts (used at build time). And
by "stateless" we mean regular Polylith components that do not become a part of
the Integrant system's state.

| Component     | Polylith name | Integrant system key                      | Description                                                                                                                                                                           |
|---------------|---------------|-------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Config        | `config`      | n/a                                       | A regular "stateless" component encapsulating a usual application configuration, which also happens to be an Integrant config map, to keep things simple for this particular example. |
| DB DataSource | n/a           | `:marksto.example.app.system/db`          | A "stateful" component which is only required at runtime (to be started and stopped properly), i.e. it (intentionally, although not necessarily) lacks a Polylith counterpart.        |
| Embedded DB   | `embedded-pg` | `:marksto.example.app.system/embedded-pg` | A "stateful" component which should be divided into two parts along the boundary between the component and the Integrant system that merely prepares arguments and calls its methods. |
| App Logic     | `logic`       | n/a                                       | A regular "stateless" component whose methods are parametrized by the app context with the required system state (e.g. `db` object with actual DB DataSource) or its derivatives.     |
| Supplier      | `supplier`    | n/a                                       | A regular "stateless" component used to showcase calling and stubbing (e.g. for testing) a particular Polylith component method via indirection.                                      |
| Timepiece     | `timepiece`   | n/a                                       | A regular "stateless" component used to showcase passing a regular parameter downstream from the app context to a Polylith component method.                                          |

### Optional Components

Also note that the 'Embedded DB' component is only used for testing. Its key is
only present in the `bases/app/test-resources/app/test-config.edn` config file,
but not in the `bases/app/resources/app/config.edn` config file. Both files end
up serving as Integrant config maps used in different contexts, and if some key
is not present, its namespace (`marksto.example.app.system.embedded-pg` in this
case) won't get loaded and that component won't make it into the system.

We also make sure to exclude the 'Embedded DB' component's Polylith brick from
the `app` project, so that it does not end up in the deployable artifact (JAR)
and, therefore, on the app classpath in any environment other than local. This
can be checked by [building](#building) the app and inspecting the contents of
the `projects/app/target` dir.


## Trying Out the App

The procedure is straightforward and standard to any other Polylith workspace.

### In REPL

The `development/src/user.clj` ns gets loaded by default. Feel free to open it
and explore its contents.

Make sure that you start your REPL server with `:dev:test` aliases.

### Testing

Prior to running tests, please, make sure that you use a specific dependency of
the embedded PostgreSQL binaries (OS/architecture, version) by checking out and
modifying the `components/embedded-pg/deps.edn`, if necessary.

> ⚠️ The embedded PostgreSQL was used for "historical reasons" to avoid having
> Docker and Testcontainers from the get-go. However, it is known that it won't
> work well in some environments such as NixOS. This can be changed later.

Run a suite of tests with the `poly` tool:

```shell
poly test
```

Or run a particular test ns in REPL, e.g.:

```clojure
(in-ns 'marksto.example.app.core-test)

;; Load the ns into your REPL

(clojure.test/run-tests)
```

### Building

As of now, this workspace contains a single `build.clj` script in the root dir.

> ❕ There is also a `:scripts` alias for local development of any scripts, e.g.
> `build.clj`, Babashka tasks, etc.

```shell
clojure -T:build uberjar :project app
```

### Launching

Launching the `app` project without building it in advance:

```shell
cd projects/app && PROFILE="..." DATABASE_URL="..." clojure -M:run-main
```

Launching the uberjar after it [has been built](#building):

```shell
cd projects/app && PROFILE="..." DATABASE_URL="..." java -jar target/example-app-standalone.jar
```

Again, make sure that you have started a local Postgres server and created a DB
and, optionally, a user role for the app beforehand.
