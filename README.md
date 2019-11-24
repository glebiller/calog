calog
=====

Consume access logs and print some stats.

Run
---

Requires java 11, first compile with maven `mvn clean package`, 
then run with `java -jar calog-1.0-SNAPSHOT-jar-with-dependencies.jar`

Usage
-----

    java -jar calog-1.0-SNAPSHOT-jar-with-dependencies.jar [options]
      Options:
        --file
          File path to watch.
          Default: /tmp/access.log
        --help
    
        --warn-threshold
          Minimum average number of requests per seconds in 2 minutes to generate 
          an alert.
          Default: 10
    
