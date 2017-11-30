+++
categories = ["recipes"]
tags = ["[depenency management]", "[java]", "[spring]", "[ant]", "[maven]"]
summary = "Tips for mavenizing."
title = "Dependency Management in Air Gapped Environment."
date = 2017-11-30T09:40:48-05:00
+++

## Mavenize the application (Ant to Maven).

Legacy applications that use Ant as a packaging tool store dependencies alongside the sources.  This violates [One of the factors that determine the cloud nativeness.](https://12factor.net/dependencies)

### Identify used dependencies.

Often, legacy applications do not follow the standards for directory structures thus making it difficult to locate dependency artifacts.

One of the easier ways to find this information is to unpack the war file and look at WEB-INF/lib directory.

### Load local Maven repository.
**Applicable when build environments do not have access to public Maven repositories.**

Dependencies identified in previous step can be loaded to local maven _.m2_ repository using [mvn install:install-file](https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html) commands.  It is useful to create a script that performs this step for all libraries.

This repository will need to be uploaded to company owned repository. [jFrog Artifactory](https://www.jfrog.com/artifactory/) or [Nexus Repository Manager](https://www.sonatype.com/nexus-repository-sonatype) is a good choice.

## Use recommended directory structure for web applications.

Copy all sources to directories as defined by [standard web application directory layout.](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)  It is recommended to use [Spring Starters](https://start.spring.io/) for Spring Boot applications.  Alternatively, one can locate [Maven Archetype](https://maven.apache.org/archetypes/) appropriate for the use case.

## Identify unused dependencies.

It is much easier to manage dependencies after application was _mavenized_.

Legacy applications may have a trail of dependencies that are redundant, or not being used at all.  Following [maven commands](https://maven.apache.org/plugins/maven-dependency-plugin/analyze-mojo.html) is a useful tool to identify these dependencies.

**We strongly recommended to increase test coverage before proceeding to the next steps.**

```bash
mvn dependency:tree
mvn dependency:analyze
```

## Run application locally.

Legacy applications packaged as war files can be ran locally using combination of [Maven War Plugin](https://maven.apache.org/plugins/maven-war-plugin/) and [Maven Tomcat Plugin.](http://tomcat.apache.org/maven-plugin-trunk/tomcat7-maven-plugin/)

After configuring the pom file, application could be ran with following commands.

Package and run.
```bash
maven tomcat7:run-war
```
Run the war file that already exists.
```bash
maven tomcat7:run-war-only
```

Sample below is an illustration of very basic _build_ section of pom.xml file.
```xml
<build>
  <finalName>simpleton-web</finalName>
  <plugins>
    <plugin>
      <artifactId>maven-war-plugin</artifactId>
      <version>3.2.0</version>
      <configuration>
        <webResources>
          <resource>
            <directory>/Users/ashumilov/Downloads</directory>
            <targetPath>WEB-INF/lib</targetPath>
            <includes>
              <include>*.jar</include>
            </includes>
          </resource>
        </webResources>
      </configuration>
    </plugin>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.6.1</version>
      <configuration>
        <source>${jdk.version}</source>
        <target>${jdk.version}</target>
      </configuration>
    </plugin>
    <plugin>
      <groupId>org.apache.tomcat.maven</groupId>
      <artifactId>tomcat7-maven-plugin</artifactId>
      <version>2.2</version>
      <configuration>
        <path>/</path>
      </configuration>
    </plugin>
  </plugins>
</build>
```
