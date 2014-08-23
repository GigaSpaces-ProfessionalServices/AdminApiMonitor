AdminApiMonitor
=========================

A little tool that logs a lot of AdminApi info. 

## Output modes

In default mode, one metric is emitted per line.

\[time\] :: \[hostname\] :: \[partition\]:: \[pid\] :: \[metric name\] :: \[metric value\]

For example:

22-8-2014 11:11:29 :: hostname.yourcompany.com :: 2 :: 63706 :: gc_count :: 44

In csv mode, all metrics are emitted on the same line, as comma separated values.

## To build one:

1. Set the gigaspaces.version in pom.xml // default is 9.7.0
1. Copy src/main/resources/admin_api.properties to your runtime environment and edit. (If run against multiple spaces, you can add more than one space name, separated by commas: `spaceMonitor.spaceName=space1,space2`)
1. Optional, unless you're running on Windows: Change the default log file in src/main/resources/logback.xml (or Copy out to runtime environment as logback-test.xml and modify there.) 
1. `mvn package`
1. Use target/AdminApiMonitor.jar **NOT** target/AdminApiMonitor-1.0-SNAPSHOT.jar

## To run:

`java -Dproperties=/full/path/to/admin_api.properties -jar AdminApiMonitor.jar` 

If the space is secured, use -s in addition to the properties settings:

`java -Dproperties=/full/path/to/admin_api.properties -jar AdminApiMonitor.jar -s`

For csv output, use -csv:

`java -Dproperties=/full/path/to/admin_api.properties -jar AdminApiMonitor.jar -c`

To change logging behavior (non-csv output), configure a logback configuration file and point the tool at it:

`java -Dproperties=/full/path/to/admin_api.properties -Dlogback.configurationFile=/path/to/config.xml -jar AdminApiMonitor.jar`

-- the end