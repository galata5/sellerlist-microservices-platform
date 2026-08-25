FROM maven:3.9.6-eclipse-temurin-17 AS build

ARG MODULE_NAME

WORKDIR /workspace

COPY . .
COPY docker/mvn-package-with-retry.sh /usr/local/bin/mvn-package-with-retry

RUN test -n "$MODULE_NAME" || (echo "ERROR: MODULE_NAME build arg is required." && exit 1)
RUN test -f pom.xml && grep -q "<artifactId>sellerlist-microservices-platform</artifactId>" pom.xml || (echo "ERROR: build from repo root. Use the repository root as the build context." && exit 1)
RUN chmod +x /usr/local/bin/mvn-package-with-retry && mvn-package-with-retry "$MODULE_NAME"

FROM eclipse-temurin:17-jre

ARG MODULE_NAME
ARG MODULE_PORT

WORKDIR /home/app

RUN useradd --system --create-home --home-dir /home/app --shell /usr/sbin/nologin appuser

COPY --from=build /workspace/${MODULE_NAME}/target/*.jar app.jar

RUN chown -R appuser:appuser /home/app

USER appuser

EXPOSE ${MODULE_PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]
