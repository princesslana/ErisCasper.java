# ErisCasper.java

ErisCasper.java is a Java library for making [Discord](https://discordapp.com) bots.

It uses [RxJava2](https://github.com/ReactiveX/RxJava) to add a
[reactive](https://www.reactivemanifesto.org/) API.

## Examples

There are example bots included in the github repository.
To run these you will need to setup your bot token in the `EC_TOKEN` environment variable.
Then:

```bash
$ mvn compile exec:java -Dexec.mainClass=com.github.princesslana.eriscasper.examples.<classname>
```

For example, to run PingBot:

```bash
$ mvn compile exec:java -Dexec.mainClass=com.github.princesslana.eriscasper.examples.PingBot
```
