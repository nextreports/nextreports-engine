<!-- I cannot use jdk 1.6 in buildhive
Current build status: [![Build Status](https://buildhive.cloudbees.com/job/nextreports/job/nextreports-engine/badge/icon)](https://buildhive.cloudbees.com/job/nextreports/job/nextreports-engine/)
-->
Current build status: [![Build Status](https://travis-ci.org/nextreports/nextreports-engine.png?branch=master)](https://travis-ci.org/nextreports/nextreports-engine)

Using Maven
-------------------
In your pom.xml you must define the dependencies to nextreports-engine artifacts with:

```xml
<dependency>
    <groupId>ro.nextreports</groupId>
    <artifactId>nextreports-engine</artifactId>
    <version>${nextreports-engine.version}</version>
</dependency>    
```

where ${nextreports-engine.version} is the last nextreports-engine version.

You may want to check for the latest released version using [Maven Search](http://search.maven.org/#search%7Cga%7C1%7Cnextreports-engine)
