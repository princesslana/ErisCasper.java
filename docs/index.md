# ErisCasper.java

ErisCasper.java is a Java library for making [Discord](https://discordapp.com) bots.

It uses [RxJava2](https://github.com/ReactiveX/RxJava) to provide a
[reactive](https://www.reactivex.org/) API.

## Usage

Be sure to replace `VERSION` with the actual version you wish to use.

The latest version is ![Maven Central](https://img.shields.io/maven-central/v/com.github.princesslana/ErisCasper.java.svg)

### Maven

```xml
  <dependency>
    <groupId>com.github.princesslana</groupId>
    <artifactId>ErisCasper.java</artifactId>
    <version>VERSION</version>
  </dependency>
```

### Gradle

```groovy
compile 'com.github.princesslana:ErisCasper.java:VERSION'
```

## Examples

There are example bots included in the github repository
[here.](https://github.com/princesslana/ErisCasper.java/tree/master/src/main/java/com/github/princesslana/eriscasper/examples)
To run these you will need to setup your bot token in the `EC_TOKEN` environment variable.
Then:

```bash
$ mvn compile exec:java -Dexec.mainClass=com.github.princesslana.eriscasper.examples.<classname>
```

For example, to run PingBot:

```bash
$ mvn compile exec:java -Dexec.mainClass=com.github.princesslana.eriscasper.examples.PingBot
```
