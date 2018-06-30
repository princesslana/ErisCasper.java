# Core API

The core API of ErisCasper.java is designed to be minimal and powerful.

It exposes the concepts of `Bot`s, `Action`s, and `Repository`s as
described below.

There also exists the concept `Gateway` and `Routes`.
These are lower level wrappers around the Discord API and considered internal
to ErisCasper.java.
This means their use is heavily discouraged, but they are available to bot
developers if required.


## Bot

A `Bot` is a `Function` run with access to a `BotContext` and produces a `Completable`.

The incoming event stream, `Repository`s, and ability to create `Action`s are 
accessible via the `BotContext`.

The return type is a `Completable` as defined by RxJava.
This Completable will be subscribed to by ErisCasper.java.

`Bot`s can be composed. Multiple implementations of `Bot` may be merged into a single `Bot`.


## Actions

`Action`s are interactions with the Discord API that are expected to change its state,
but for which there is no response expected.
Examples include sending a message, or adding a role.


## Repositories

`Repository`s are the source of information from Discord.
This could include, for example, information about guilds, channels, or users.

`Repository` implmentations may perform caching, gateway requests, or HTTP requests
as required.

## Internal Concepts

### Gateway

Provide access to the (Discord Gateway)[https://discordapp.com/developers/docs/topics/gateway].
This is a websocket provided by Discord used for tasks such as authentication and receiving events.

### Routes

Provides access to Discord's REST API.
The REST API is used for most actions and for querying the state of servers, guilds, users, etc.