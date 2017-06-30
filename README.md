# Demo for a proposed integration of FF4j with Spring Boot/Cloud properties for JHipster microservices

## Without the registry/config-server
Run the app with: `mvn spring-boot:run` and open [localhost:8080](localhost:8080)

FF4j Console at : [http://localhost:8080/ff4j-console/features](http://localhost:8080/ff4j-console/features)
Edit the spring boot property `curl -X POST http://localhost:8080/management/env -d ff4j.features.testflag.enable=true` and see the changes in the console.

## With the registry/config-server

Run the jhipster-registry with: `docker-compose -f jhipster-registry/jhipster-registry.yml up -d` and open the config screen: [http://localhost:8761/#/config](http://localhost:8761/#/config) (login with admin/admin)

Edit config in `jhispter-registry/central-config/application.yml, see how the config server's config is refreshed in [http://localhost:8761/#/config](http://localhost:8761/#/config)
Force hot refresh of the config for your apps: `curl -X POST http://localhost:8080/management/refresh`