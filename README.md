<!-- I cannot use jdk 1.6 in buildhive
Current build status: [![Build Status](https://buildhive.cloudbees.com/job/nextreports/job/nextreports-engine/badge/icon)](https://buildhive.cloudbees.com/job/nextreports/job/nextreports-engine/)
-->
Current build status: [![Build Status](https://travis-ci.org/nextreports/nextreports-engine.png?branch=master)](https://travis-ci.org/nextreports/nextreports-engine)

For more information about NextReports Engine see the product page [link](http://www.next-reports.com/index.php/products/nextreports-engine.html).

How to build
-------------------
Requirements:
- [Git](http://git-scm.com/)
- JDK 7 (test with `java -version`)
- [Apache Ant](http://ant.apache.org/) (test with `ant -version`)

Steps:
- create a local clone of this repository (with `git clone https://github.com/nextreports/nextreports-engine.git`)
- go to project's folder (with `cd nextreports-engine`)
- build the artifacts (with `ant clean release`)

After above steps a folder _artifacts_ is created and all goodies are in that folder.

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
