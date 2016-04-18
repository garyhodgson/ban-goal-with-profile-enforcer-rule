# ban-goal-with-profile-enforcer-rule

A [Maven enforcer plugin](https://maven.apache.org/enforcer/maven-enforcer-plugin/) custom rule which bans combinations of goals and profiles, optionally for a given artifact.

See also: [Writing a custom rule](https://maven.apache.org/enforcer/enforcer-api/writing-a-custom-rule.html).

### Parameters
* profiles: Comma-separated list of banned profiles.
* goals: Comma-separated list of banned goals.
* artifactId: Optional artifact to limit where to apply this rule.
* message: Optional message to append to the exception message.
* verbose: More log messages.

### Usage

* Clone, Edit, Compile
* Include via

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>1.4.1</version>
    <executions>
        <execution>
            <id>default</id>
            <phase>validate</phase>
            <goals>
                <goal>enforce</goal>
            </goals>
            <configuration>
                <fail>true</fail>
                <rules>
                    <banGoalWithProfileRule implementation="com.garyhodgson.maven.enforcer.rule.BanGoalWithProfileRule">
                        <profiles>format-source</profiles>
                        <goals>initialize</goals>
                        <artifactId>${project.artifactId}</artifactId>
                        <message>Profile not allowed during release as it causes hard-coded property values to be stored in the released pom.</message>
                        <verbose>true</verbose>
                    </banGoalWithProfileRule>
                </rules>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>com.garyhodgson.maven.enforcer</groupId>
            <artifactId>ban-goal-with-profile-enforcer-rule</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
</plugin>
```

* Optionally, run a usage example with `mvn -f usage-pom.xml initialize` which should produce something like:
```
$ mvn -f usage-pom.xml initialize
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building ban-goal-with-profile-enforcer-rule-sample-usage 1
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-enforcer-plugin:1.4.1:enforce (default) @ ban-goal-with-profile-enforcer-rule-sample-usage ---
[INFO] banned profiles: [format-source]
[INFO] banned goals: [initialize]
[INFO] session goals: [initialize]
[INFO] active profiles: {external=[nexus], com.garyhodgson.maven.enforcer:ban-goal-with-profile-enforcer-rule-sample-usage:1=[format-source], =[]}
[WARNING] Rule 0: com.garyhodgson.maven.enforcer.rule.BanGoalWithProfileRule failed with message:
Failing because goal [initialize] was called with active profile [format-source (source: com.garyhodgson.maven.enforcer:ban-goal-with-profile-enforcer-rule-sample-usage:1)]. Profile not allowed during release as it causes hard-coded property values to be stored in the released pom.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```
