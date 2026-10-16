![](https://img.shields.io/maven-central/v/com.lyra/server-rest-sdk.svg)

# REST API Client SDK

This SDK allows to call Rest API in order to invoke operations on the payment platform.
For more information about the SDK server, please, refer to your service provider.

## Requirements

Java 8 or higher is required to use this library.

## Installation

You can use your favourite dependency manager in order to use this library in your project.

For example, for Maven:

    <dependencies>
        <dependency>
            <groupId>com.lyra</groupId>
            <artifactId>server-rest-sdk</artifactId>
            <version>2.3.0</version>
        </dependency>
    </dependencies>

You can also directly download the JAR from this path and add it to your classpath: [https://github.com/lyra/rest-api-server-java-sdk/releases/download/v2.3.0/server-rest-sdk-2.3.0.jar](https://github.com/lyra/rest-api-server-java-sdk/releases/download/v2.3.0/server-rest-sdk-2.3.0.jar)

In this last case, you should make sure that you add also [Gson 2.8.6](https://github.com/google/gson) to your project
classpath.

## Basic Usage

The first thing you have to do is to configure your client.

### Configuration

#### Default configuration by file

In order to do so, you can just create in your classpath a file called _api-client-configuration.properties_
and set the right values:

    #Merchant account parameters
    username=<your shopId here>
    password=<your Rest API password>
    restApiServerName=<your payment platform URL>

    #Connection parameters
    proxyHost=<only if needed>
    proxyPort=<only if needed>
    connectionTimeout=45000
    requestTimeout=45000

### Perform an API call

In order to call a REST resource from the API, you only need to call the _post_ method, indicating the target
resource you want to target and the parameters.
This is the signature of the method:

```java
    public static String post(String targetResource, Map<String, Object> parameters) throws ClientException
```

To facilitate the definition of targetResource you can use the ClientResource enum, that contains
predefined resources and overrides toString().

In the case of internal error the method will throw a ClientException, which is non checked
exception.

The call parameters can be defined using a simple and flexible Map object.

This is an usage example:

```java
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("amount", 100);
    parameters.put("currency", 978);

    String result = Client.post(ClientResource.CREATE_PAYMENT.toString(), parameters);
```

Payment, token and SDK test resources use API V4. To create a funds transfer, use
`ClientResource.CREATE_FUNDS_TRANSFER.toString()` (or `"FundsTransfer/Create"`) as the target resource.
The client automatically uses API V4.1 for this operation.

## Advanced usage

#### Override configuration programmatically

You can easily override the default configuration when performing the call to the _post_ method.
This can be very useful, for example, if you need to share this same SDK library with different client applications.

In order to do so, you have to create a ClientConfiguration object.
This object implements a builder pattern that makes really easy to instantiate it:

```java
    ClientConfiguration configuration = ClientConfiguration.builder()
                .username("usernameToOverride")
                .password("passwordToOverride")
                .build();
```

Please note that:

- You can set a default configuration file and just override specific values by _ClientConfiguration_ object.
- The _ClientConfiguration_ will only change the configuration for the request. If you make another call
  and you want to use the same configuration, you will have to pass again the object.

For example, if you need to perform a post call with a different username/password:

```java
    String result = Client.post(ClientResource.CREATE_PAYMENT.toString(), parameters,
            ClientConfiguration.builder().username("anotherUsername").password("anotherPassword").build());
```

## Download and compile

You can download the code source and compile using maven.

    mvn clean install

Note that the project use [Lombok](https://projectlombok.org/), a library that automates and improves Java
construction, avoiding boilerplate code. This means that maybe you will need to install a plugin in your IDE in order to support it.

## Publishing to Maven Central

Use a full JDK 11 (including `javadoc`) to build this project. If Maven uses the internal Nexus mirror,
the JDK must trust the corporate certificate authorities.

Publishing uses the `central` profile, which replaces the legacy `ossrh` profile.
This profile also runs an OSS Index dependency audit. An authentication or service warning (such as
`401 Unauthorized`) means the audit was not completed, even if the build succeeds. Resolve the warning
and obtain a successful audit before releasing.

The Central Portal account must have publishing access to the `com.lyra` namespace.
Generate a [Central Portal user token](https://central.sonatype.org/publish/generate-portal-token/)
and add its credentials to the existing `<servers>` section of `~/.m2/settings.xml`:

```xml
<server>
    <id>central</id>
    <username>YOUR_TOKEN_USERNAME</username>
    <password>YOUR_TOKEN_PASSWORD</password>
</server>
```

Use the token credentials, not the legacy OSSRH/Jira password. Never commit credentials or private keys.
Install the release signing key in GPG and ensure its public key is available on a
[supported public key server](https://central.sonatype.org/publish/requirements/gpg/).

Before publishing, set a non-SNAPSHOT version that has not already been published to Maven Central.
Published versions cannot be overwritten.

Build the JAR, sources and Javadoc without uploading or signing:

```sh
mvn -U clean package -Pcentral
```

Build and sign locally without uploading:

```sh
mvn clean verify -Pcentral
```

Upload the signed release for Central Portal validation:

```sh
mvn clean deploy -Pcentral
```

The upload does not automatically publish the release. Review the validated deployment and explicitly
publish it at https://central.sonatype.com/publishing/deployments.
