# milieu
Small annotation based configuration library

## Usage
Annotate your configuration classes with the `Configuration` annotation.

```java
public class DatabaseConfiguration {

    @Configuration("DB_URL")
    private String jdbcUrl;
    
    @Configuration("DB_USERNAME")
    private String username;
    
    @Configuration("DB_PASSWORD")
    private String password; 
}
```
Load your configuration with `Configurations`. The following example will use the default `ConfigurationResolver`, which loads the configuration from the process environment.

```java
DatabaseConfiguration databaseCfg = Configurations.get(DatabaseConfiguration.class);
```
To configure your application you only have to set the specific environment variables, before you start your application.
```bash
export DB_URL="jdbc:mysql://localhost:3306/milieu"
export DB_USERNAME="milieu"
export DB_PASSWORD="secret"
# start your application
```
