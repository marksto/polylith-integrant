# Polylith + Integrant

This example demonstrates a basic setup of a stateful system (implemented as a
[Polylith](https://polylith.gitbook.io/) `base`) whose lifecycle is handled by
the [Integrant](https://github.com/weavejester/integrant). It caters for both
in-REPL development and production use cases.

## Prerequisites

The [`poly` tool](https://polylith.gitbook.io/poly) must be installed locally.

The following environment variables are used:

| Env Var           | Required     | Description                                                             |
|-------------------|--------------|-------------------------------------------------------------------------|
| `PROFILE`         | in non-local | Environment, e.g. "dev", "prod" (think Leiningen profile, not Polylith) |
| `DATABASE_URL`    | in any env   | A JDBC URL for PostgreSQL used by the application (not used in tests)   |

In a local development environment setting the `PROFILE` var is not necessary,
since the app config is loaded with the `:dev` param by default in `user.clj`.
Yet, don't forget to set it in case if you want to run the app via the `-main`
entrypoint locally.

## System Components

The most frequently asked system was taken as an illustrative example. It uses
several single-purpose components to work with a traditional database, in this
case PostgreSQL.

Unfortunately, the term "component" becomes overloaded in the current context.
It can both mean a Polylith component (a type of brick) and an Integrant system
component (a.k.a. a "key" in Integrant's parlance). In order not to go nuts and
at the same time not to introduce new terms further complicating understanding,
we will use this term with qualifiers — "stateful" and "stateless".

By "stateful" we mean components that are part of the Integrant system (used at
runtime) and that may also have Polylith counterparts (used at build time). And
by "stateless" we mean regular Polylith components that do not become a part of
the Integrant system's state.

The minimal set of system components:

| Component     | Polylith name | Integrant system key                      | Description                                                                                                                                                                           |
|---------------|---------------|-------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Config        | `config`      | n/a                                       | A regular "stateless" component encapsulating a usual application configuration, which also happens to be an Integrant config map, to keep things simple for this particular example. |
| DB DataSource | n/a           | `:marksto.example.app.system/db`          | A "stateful" component which is only required at runtime (to be started and stopped properly), i.e. it (intentionally, although not necessarily) lacks a Polylith counterpart.        |
| Embedded DB   | `embedded-pg` | `:marksto.example.app.system/embedded-pg` | A "stateful" component which should be divided into two parts along the boundary between the component and the Integrant system that merely prepares arguments and calls its methods. |
| App Logic     | `logic`       | n/a                                       | A regular "stateless" component whose methods are parametrized by the app context with the required system state (e.g. `db` object with actual DB DataSource) or its derivatives.     |
| Timepiece     | `timepiece`   | n/a                                       | A regular "stateless" component used to showcase passing a regular parameter downstream from the app context to a Polylith component method.                                          |

## Optional Components

Also note that the 'Embedded DB' component is only used for testing. Its key is
only present in the `bases/app/test-resources/app/test-config.edn` config file,
but not in the `bases/app/resources/app/config.edn` config file. Both files end
up serving as Integrant config maps used in different contexts, and if some key
is not present, its namespace (`marksto.example.app.system.embedded-pg` in this
case) won't get loaded and that component won't make it into the system.

We also make sure to exclude the 'Embedded DB' component's Polylith brick from
the `app` project, so that it does not end up in the deployable artifact (JAR)
and, therefore, on the app classpath in any environment other than local. This
can be checked by running the `clojure -T:build uberjar :project app` and then
inspecting the contents of the `projects/app/target` dir.
