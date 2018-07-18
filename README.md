[![Build Status](https://travis-ci.org/SolaceProducts/solace-java-spring-boot.svg?branch=master)](https://travis-ci.org/SolaceProducts/solace-java-spring-boot)

# Spring Boot Auto-Configuration for the Solace Java API

This project provides Spring Boot Auto-Configuration and an associated Spring Boot Starter for the Solace Java API. The goal of this project is to make it easy to auto wire the Solace Java API within your application so you can take advantage of all the benefits of Spring Boot auto-configuration.

## Contents

* [Overview](#overview)
* [Using Auto-Configuration in your App](#using-auto-configuration-in-your-app)
* [Building the Project Yourself](#building-the-project-yourself)
* [Running the Sample](#running-the-sample)
* [Contributing](#contributing)
* [Authors](#authors)
* [License](#license)
* [Resources](#resources)

---

## Overview

As stated this project provides a Spring Boot Auto-Configuration implementation and a Spring Boot Starter pom for the Solace Java API. The goal of this project is to make it easier to use the Solace Java API with Spring Boot auto-configuration through the @Autowired annotation. This project is used internally within Solace to enable Spring Boot applications and as such it will be maintained and updated as our internal needs required.

The artifacts are published to Maven Central so it should be familiar and intuitive to use this project in your applications. If you find Solace Java API properties that this project does not yet support, simply raise an issue and we'll look into adding this support or submit a pull request with the update.

One item to note as described below is that this project introduces a new factory for Solace Java API sessions: `SpringJCSMPFactory`. Overtime the Solace Java API may introduce a similar factory and remove the need for this custom extension. For now however, this is included in the auto-configuration jar for ease of use.

## Using Auto-Configuration in your App

See the associated `solace-java-sample-app` for an example of how this is all put together in a simple application. You'll need to do three steps:

1. Update your build.
2. Autowire a `SpringJCSMPFactory`.
3. Configure the application to use a Solace PubSub+ service.

### Updating your build

The releases from this project are hosted in [Maven Central](https://mvnrepository.com/artifact/com.solace.spring.boot/solace-java-spring-boot-starter)

The easiest way to get started is to include the `solace-java-spring-boot-starter` in your application. For an examples see the [Java Sample App](https://github.com/SolaceProducts/solace-java-spring-boot/tree/master/solace-java-sample-app) in this project.

Here is how to include the spring boot starter in your project using Gradle and Maven.

#### Using it with Gradle

```groovy
// Solace Java API & auto-configuration
compile("com.solace.spring.boot:solace-java-spring-boot-starter:1.+")
```

#### Using it with Maven

```xml
<!-- Solace Java API & auto-configuration-->
<dependency>
	<groupId>com.solace.spring.boot</groupId>
	<artifactId>solace-java-spring-boot-starter</artifactId>
	<version>1.+</version>
</dependency>
```

### Using Spring Dependency Auto-Configuration (@SpringBootApplication & @Autowired)

Now in your application code, you can simply declare the `SpringJCSMPFactory` and annotate it so that it is autowired:

```java
@Autowired
private SpringJCSMPFactory solaceFactory;
```

Once you have the `SpringJCSMPFactory`, it behaves just like the `JCSMPFactory` and can be used to create sessions. For example:

```java
final JCSMPSession session = solaceFactory.createSession();
```

The `SpringJCSMPFactory` is a wrapper of the singleton `JCSMPFactory` which contains an associated `JCSMPProperties`. This facilitates auto-wiring by Spring but otherwise maintains the familiar `JCSMPFactory` interface known to users of the Solace Java API.

Alternatively, you could autowire one or more of the following to create your own customized `SpringJCSMPFactory`:

```java
/* A factory for creating SpringJCSMPFactory. */
@Autowired
private SpringJCSMPFactoryCloudFactory springJcsmpFactoryCloudFactory;

/* A POJO describing the credentials for the first detected Solace PubSub+ service */
@Autowired
private SolaceServiceCredentials solaceServiceCredentials;

/* The properties of a JCSMP connection for the first detected Solace PubSub+ service */
@Autowired
private JCSMPProperties jcsmpProperties;
```

However note that the `SolaceServiceCredentials` will only provide meaningful information if the application is configured by [exposure of a Solace PubSub+ service manifest](#exposing-a-solace-pubsub-service-manifest-in-the-applications-environment), and not by using the [application properties file](#updating-your-application-properties).

### Configure the Application to use your Solace PubSub+ Service Credentials
#### Deploying your Application to a Cloud Platform

By using [Spring Cloud Connectors](https://cloud.spring.io/spring-cloud-connectors/), this library can automatically configure a `SpringJCSMPFactory` using the detected Solace PubSub+ services when deployed on a Cloud Platform such as Cloud Foundry.

Currently, the [Solace Cloud Foundry Cloud Connector](https://github.com/SolaceProducts/sl-spring-cloud-connectors) is the only connector that is supported by default in this library, but could easily be augmented by adding your own Solace Spring Cloud Connectors as dependencies to the [auto-configuration's POM](solace-java-spring-boot-autoconfigure/pom.xml).

For example:

```xml
<dependency>
	<groupId>com.solace.cloud.cloudfoundry</groupId>
	<artifactId>solace-spring-cloud-connector</artifactId>
	<version>2.1.0</version>
</dependency>
```

#### Exposing a Solace PubSub+ Service Manifest in the Application's Environment

Configuration of the `SpringJCSMPFactory` can be done through exposing a Solace PubSub+ service manifest to the application's JVM properties or OS environment.

For example, you can set a `SOLCAP_SERVICES` variable in either your JVM properties or OS's environment to directly contain a `VCAP_SERVICES`-formatted manifest file. In which case, the autoconfigure will pick up any Solace PubSub+ services in it and use them to accordingly configure your `SpringJCSMPFactory`.

The properties provided by this externally-provided manifest can also be augmented using the values from the [application's properties file](#updating-your-application-properties).

For details on valid manifest formats and other ways of exposing Solace service manifests to your application, see the _Manifest Load Order and Expected Formats_ section in the [Solace Services Info](https://github.com/SolaceProducts/solace-services-info#manifest-load-order-and-expected-formats) project.

#### Updating your Application Properties

Alternatively, configuration of the `SpringJCSMPFactory` can also be done through the [`application.properties` file](https://github.com/SolaceProducts/solace-java-spring-boot/blob/master/solace-java-sample-app/src/main/resources/application.properties). This is where users can control the Solace Java API properties. Currently this project supports direct configuration of the following properties:

```
solace.java.host
solace.java.msgVpn
solace.java.clientUsername
solace.java.clientPassword
solace.java.clientName
solace.java.connectRetries
solace.java.reconnectRetries
solace.java.connectRetriesPerHost
solace.java.reconnectRetryWaitInMillis
```

Where reasonable, sensible defaults are always chosen. So a developer using a Solace PubSub+ message broker and wishing to use the default message-vpn must only set the `solace.java.host`.

See [`SolaceJavaProperties`](https://github.com/SolaceProducts/solace-java-spring-boot/blob/master/solace-java-spring-boot-autoconfigure/src/main/java/com/solace/spring/boot/autoconfigure/SolaceJavaProperties.java) for the most up to date list.

Any additional Solace Java API properties can be set through configuring `solace.java.apiProperties.<Property>` where `<Property>` is the name of the property as defined in the [Solace Java API documentation for `com.solacesystems.jcsmp.JCSMPProperties`](https://docs.solace.com/API-Developer-Online-Ref-Documentation/java/constant-values.html#com.solacesystems.jcsmp.JCSMPProperties.ACK_EVENT_MODE), for example:

```
solace.java.apiProperties.reapply_subscriptions=false
```

Note that the direct configuration of `solace.java.` properties takes precedence over the `solace.java.apiProperties.`.

## Building the Project Yourself

This project depends on maven for building. To build the jar locally, check out the project and build from source by doing the following:

    git clone https://github.com/SolaceProducts/solace-java-spring-boot.git
    cd solace-java-spring-boot
    mvn package

This will build the auto-configuration jar and associated sample.

Note: As currently setup, the build requires Java 1.8. If you want to use another older version of Java adjust the build accordingly.

## Running the Sample

The simplest way to run the sample is from the project root folder using maven. For example:

	mvn spring-boot:run

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors

See the list of [contributors](https://github.com/SolaceProducts/solace-java-spring-boot/graphs/contributors) who participated in this project.

## License

This project is licensed under the Apache License, Version 2.0. - See the [LICENSE](LICENSE) file for details.

## Resources

For more information about Spring Boot Auto-Configuration and Starters try these resources:

- [Spring Docs - Spring Boot Auto-Configuration](http://docs.spring.io/autorepo/docs/spring-boot/current/reference/htmlsingle/#using-boot-auto-configuration)
- [Spring Docs - Developing Auto-Configuration](http://docs.spring.io/autorepo/docs/spring-boot/current/reference/htmlsingle/#boot-features-developing-auto-configuration)
- [GitHub Tutorial - Master Spring Boot Auto-Configuration](https://github.com/snicoll-demos/spring-boot-master-auto-configuration)

For more information about Solace technology in general please visit these resources:

- The Solace Developer Portal website at: http://dev.solace.com
- Understanding [Solace technology.](http://dev.solace.com/tech/)
- Ask the [Solace community](http://dev.solace.com/community/).
