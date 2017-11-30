+++
categories = ["recipes"]
tags = ["[java]", "[jndi]", "[spring]"]
summary = "Recipe Summary"
title = "Remove External Jndi Provider Dependency"
date = 2017-11-30T10:52:32-05:00
+++

Legacy J(2)EE applications tend to heavily rely on JNDI lookups to access resources such as DataSources, Properties, etc.

Several options are available to replatform applications that rely on looking up resources from JNDI to run on Cloud Foundry.

## Using TomEE Buildpack.

[TomEE Buildpack](https://github.com/cloudfoundry-community/tomee-buildpack-resource-configuration) allows the definition of JNDI resources consumed by application by defining resources in _WEB-INF/resources.xml_.

Using TomEE buildpack in lieu of standard Tomcat provided by Standard Java Buildpack creates a _snowflake_ environment
and should be given a serious thought by organizations.

## Using standard java buildpack with embedded Tomcat.

With this approach, Tomcat configuration is extended programmatically with additional settings defined in _context.xml_ file packaged with an application.  _setRelativeWebContentFolder()_ method sets the location of _context.xml_

```Java
tomcatConfigurer = new TomcatLaunchConfigurer(configServerUrl, "hello-tomcat", new String[] { "development", "db" });
tomcatConfigurer.setRelativeWebContentFolder("src/main/webapp");
```

Furthermore, _context.xml_ defines JNDI property..

```xml
<Context>
  ...
  <Environment name="maxConnections" value="10"
           type="java.lang.Integer" override="false"/>
...
</Context>
```
..that is referred to in _web.xml_

```xml
<resource-ref id="connections">
  <description>Max Db Connections</description>
  <res-ref-name>java/maxConnections</res-ref-name>
  <res-type>java.lang.Integer</res-type>
  <res-auth>Container</res-auth>
</resource-ref>
```

## Refactoring out JNDI in favor of Spring Beans.

Preferred method however is to refactor JNDI references in favor of Spring Beans.

Mocking all JNDI resources in Spring _Configuration_ bean is a good first step in this refactoring.

At this point, JNDI references can be removed in stages by substituting them by Spring Beans.

```Java
@Slf4j
@Configuration
public class JndiContextMock {

    public void init() {
        try {
            SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
            builder.bind( "jdbc/mydb" , new ImaginaryDataSource() );
            builder.bind("jms/myConnectionFacory", new RMQConnectionFactory());
            builder.activate();
        } catch (NamingException e) {
            String message = "Error activating DataSource";
            log.error(message);
            throw new RuntimeException(message);
        }
    }
}
```
