FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -DskipTests package

FROM tomcat:10-jdk21
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN apt-get update \
	&& apt install -y postgresql-common \
	&& /usr/share/postgresql-common/pgdg/apt.postgresql.org.sh -y \
	&& apt install -y \
	postgresql-client-16 \
	yaz \
	&& rm -rf /var/cache/apk/*
RUN rm -rf "${CATALINA_HOME}/webapps/ROOT"
COPY --from=build /app/target/Biblivre6/WEB-INF/tags ${CATALINA_HOME}/webapps/bibliotecajp/WEB-INF/tags
COPY --from=build /app/target/Biblivre6/WEB-INF/templates ${CATALINA_HOME}/webapps/bibliotecajp/WEB-INF/templates
COPY --from=build /app/target/Biblivre6/WEB-INF/tlds ${CATALINA_HOME}/webapps/bibliotecajp/WEB-INF/tlds
COPY --from=build /app/target/Biblivre6/WEB-INF/lib ${CATALINA_HOME}/webapps/bibliotecajp/WEB-INF/lib
COPY --from=build /app/target/Biblivre6/WEB-INF/jsp ${CATALINA_HOME}/webapps/bibliotecajp/WEB-INF/jsp
COPY --from=build /app/target/Biblivre6/WEB-INF/classes ${CATALINA_HOME}/webapps/bibliotecajp/WEB-INF/classes
COPY --from=build /app/target/Biblivre6/WEB-INF/web.xml ${CATALINA_HOME}/webapps/bibliotecajp/WEB-INF/web.xml
ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions --enable-preview"
EXPOSE 8080
