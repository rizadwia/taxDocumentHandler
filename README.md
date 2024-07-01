
# Getting Started

Publish sftp access as RESTful API with following functionality : 
* list files of directory
* upload file
* download file

### Reference Documentation
For further reference, please consider the following sections:
* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.3.1/maven-plugin/reference/html/)

### Maven
You need to install maven to build the project.
* Use `mvn clean package` to clean and package the build
* To run the app you can use `mvn exec:java` or common java command `java -jar ./target/taxDocumentHandler-1.0.0.jar`
Ensure your host can connect to the sftp server.

### Configuration
Configuration is located in `/src/main/resources/application.properties` file.
Following options are sftp related configuration : 
* `sftp.host` is sftp host
* `sftp.port` is sftp port
* `sftp.username` is sftp username
* `sftp.password` is sftp password
* `sftp.pool-max` is maximum connection pool size that can connect to sftp
