# REST API Client SDK 

This SDK allows to call Rest API in order to invoke operations on the payment platform.
For more information about the SDK server, please, refer to your service provider.

## Requirements

Java 7 or higher is required.

## Installation

You can use your favourite dependency manager in order to use this library in your project.

For example, for Maven: 

	<dependencies>
		<dependency>
			<groupId>com.lyra</groupId>
			<artifactId>server-rest-sdk</artifactId>
			<version>1.0.1</version>
		</dependency>
	</dependencies>

You can also directly download the JAR from this path and add it to your classpath: [https://github.com/lyra/rest-api-server-java-sdk/releases/download/v1.0.1/server-rest-sdk-1.0.1.jar](https://github.com/lyra/rest-api-server-java-sdk/releases/download/v1.0.1/server-rest-sdk-1.0.1.jar)

In this last case, you should make sure that you add also [Gson 2.8.5](https://github.com/google/gson) to your project 
classpath. 

## Basic Usage

The first thing you have to do is to configure your client.

### Configuration

#### Default configuration by file

In order to do so, you can just create in your classpath a file called _lyra-client-configuration.properties_ 
and set the right values: 

    #Merchant account parameters
    username=<your shopId here>
    password=<your Rest API password>
    endpointDomain=<your endpoint domain URL>

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
    public static String post(String targetResource, Map<String, Object> parameters) throws LyraClientException
```

To facilitate the definition of targetResource you can use the LyraClientResource enum, that contains 
predefined resources and overrides toString().

In the case of internal error the method will throw a LyraClientException, which is non checked 
exception.

The call parameters can be defined using a simple and flexible Map object.

This is an usage example: 

```java
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("amount", 100);
    parameters.put("currency", 978);
    
    String result = LyraClient.post(LyraClientResource.CREATE_PAYMENT.toString(), parameters);
```

## Advanced usage

#### Override configuration programmatically

You can easily override the default configuration when performing the call to the _post_ method.
This can be very useful, for example, if you need to share this same SDK library with different client applications. 

In order to do so, you have to create a LyraClientConfiguration object.
This object implements a builder pattern that makes really easy to instantiate it: 

```java
    LyraClientConfiguration configuration = LyraClientConfiguration.builder()
                .username("usernameToOverride")
                .password("passwordToOverride")
                .build();
```

Please note that: 

* You can set a default configuration file and just override specific values by _LyraClientConfigutation_ object.
* The _LyraClientConfiguration_ will only change the configuration for the request. If you make another call 
and you want to use the same configuration, you will have to pass again the object.

For example, if you need to perform a post call with a different username/password: 

```java
    String result = LyraClient.post(LyraClientResource.CREATE_PAYMENT.toString(), parameters, 
            LyraClientConfiguration.builder().username("anotherUsername").password("anotherPassword").build);
```

## Download and compile

You can download the code source and compile using maven.

    mvn clean install

Note that the project use [Lombok](https://projectlombok.org/), a library that automates and improves Java 
construction, avoiding boilerplate code. This means that maybe you will need to install a plugin in your IDE in order to support it. 
