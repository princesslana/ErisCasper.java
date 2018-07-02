# Core API

The core API of ErisCasper.java is designed to be minimal and powerful.

It exposes the concepts of `Bot`s, the event stream (`Observable<Event>`), `Action`s, and `Repository`s as
described below.

At a lower level is the `Gateway` and `Routes`.
These are wrappers around the Discord API and considered internal
to ErisCasper.java.
This means their use is heavily discouraged, but they are available to bot
developers if required.


## Bot

A `Bot` is a `Function` run with access to a `BotContext` and produces a `Completable`.

The event stream, `Repository`s, and ability to create `Action`s are 
accessible via the `BotContext`.

The return type is a `Completable` as defined by RxJava.
This Completable will be subscribed to by ErisCasper.java.

`Bot`s can be composed. Multiple implementations of `Bot` may be merged into a single `Bot`.


## Event Stream

The event stream consists of events sent from Discord.
This includes events such as message sends, new guild members, etc.

The event stream uses the `Observable` type from RxJava to make the events available to bot developers.


## Actions

`Action`s are interactions with the Discord API that are expected to change its state,
but for which there is no response expected.
Examples include sending a message or adding a role.


## Repositories

`Repository`s are the source of information from Discord.
This includes information about guilds, channels, or users.

`Repository` implementations may perform caching, gateway requests, or HTTP requests
as required.

## Internal Concepts

### Gateway

Provides access to the (Discord Gateway)[https://discordapp.com/developers/docs/topics/gateway].
This is a websocket provided by Discord used for tasks such as authentication, sending gateway commands,
and receiving events

### Routes

Provides access to (Discord's REST API)[https://discordapp.com/developers/docs/reference].
The REST API is used for most actions and for querying the state of servers, guilds, users, etc.
