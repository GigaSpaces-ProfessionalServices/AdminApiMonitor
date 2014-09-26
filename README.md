AdminApiMonitor
=========================

A little tool that logs a lot of AdminApi info. 

## Output modes

In default mode, one metric is emitted per line.

\[time\] :: \[hostname\] :: \[partition\]:: \[pid\] :: \[metric name\] :: \[metric value\]

For example:

22-8-2014 11:11:29 :: hostname.yourcompany.com :: 2 :: 63706 :: gc_count :: 44

In csv mode, all metrics are emitted on the same line, as comma separated values.

Please see "Logging configuration" section for more details.

## To build one:

1. Set the gigaspaces.version in pom.xml // default is 9.7.0
1. Copy src/main/resources/admin_api.properties to your runtime environment and edit. (If run against multiple spaces, you can add more than one space name, separated by commas: `spaceMonitor.spaceName=space1,space2`)
1. `mvn package`
1. Use target/AdminApiMonitor.jar **NOT** target/AdminApiMonitor-1.0-SNAPSHOT.jar

## To run:

`java -Dproperties=/full/path/to/admin_api.properties -jar AdminApiMonitor.jar` 

If the space is secured, use -s in addition to the properties settings:

`java -Dproperties=/full/path/to/admin_api.properties -Dlogback.configurationFile=/path/to/config.xml -jar AdminApiMonitor.jar -s`

For csv output, use -csv:

`java -Dproperties=/full/path/to/admin_api.properties -Dlogback.configurationFile=/path/to/config.xml -jar AdminApiMonitor.jar -c`

To change logging behavior (non-csv output), configure a logback configuration file and point the tool at it:

`java -Dproperties=/full/path/to/admin_api.properties -Dlogback.configurationFile=/path/to/config.xml -jar AdminApiMonitor.jar`

## Configuration

Application and logging configuration examples are provided in **target/AdminApiMonitor-ConfigurationFiles.zip**.

Few different logging configurations are provided in target/logback directory. Formats:
1. default-logback.xml sends emails produces lines like
`25-9-2014 13:38:38 ::  -  :: inventorySpace.2 [1] ::  -  :: cacheNum_com.belk.inventory.domain.InventoryItem :: 50048 ::  - `
2. csv-logback is soppesed to be used with -c command line argument, it sends emails and produces:
 `timestamp, reads_per_sec_inventorySpace.2 [1], reads_inventorySpace.2 [1], reads_per_sec_inventorySpace.1 [2], reads_inventorySpace.1 [2], (more metric headers here),
  25-9-2014 13:50:28, 0.300, 3, 0.000, 0, (more metrics values here) 
  25-9-2014 13:50:37, 0.060, 3, 0.000, 0, (more metrics values here)`
3. logback-email.xml - sends emails and doesn't write to any file.
4  logback-stdout - writes output to console.
5. logback-wily.xml  
Logback documentation:
http://logback.qos.ch/manual/configuration.html

Application configuration property files:
1. admin_api_email.properties - contains alerts.email.reporting=true.
2. admin_api_multiple_spaces.properties - contains example of spaceMonitor.spaceName property format for few spaces monitoring.
3. admin_api_secured.properties - contains spaceMonitor.secured=true.
4. admin_api_simple.properties - supposed to be used in common cases.

-- the end