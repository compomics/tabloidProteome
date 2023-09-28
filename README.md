# tabloid-proteome
tabloid proteome new version

## Docker setup
In docker-compose.yaml replace the filepath for neo4j data and logs folder with the paths on your local machine.

Run

`docker compose up`

## Tomcat setup
Install tomcat@8

Copy /target/tabloidproteome.war to *webapps* directory

Access at http://localhost:8080:tabloidproteome
