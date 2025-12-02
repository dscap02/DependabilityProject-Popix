# ==========================================
# STAGE 1: Build (Maven + JDK 21)
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# 1. Copia solo il pom.xml e scarica le dipendenze (caching efficiente)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copia i sorgenti
COPY src ./src

# 3. Compila il pacchetto (senza test per velocità)
RUN mvn clean package -DskipTests -B

# 4. Estrae il driver MySQL in una cartella specifica (così non dobbiamo indovinare la versione)
RUN mvn dependency:copy-dependencies \
    -DincludeGroupIds=com.mysql \
    -DincludeArtifactIds=mysql-connector-j \
    -DoutputDirectory=target/lib


# ==========================================
# STAGE 2: Runtime (Tomcat 9 + JDK 21)
# ==========================================
FROM tomcat:9.0.86-jdk21

# 1. Pulizia: Rimuovi le webapp di default di Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# 2. Copia l'applicazione compilata
COPY --from=build /app/target/popix-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# 3. Copia la configurazione del Context (DataSource)
COPY docker/tomcat/ROOT.xml /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml

# 4. Copia il driver MySQL (prende qualsiasi .jar trovato nella cartella preparata prima)
COPY --from=build /app/target/lib/*.jar /usr/local/tomcat/lib/

EXPOSE 8080

CMD ["catalina.sh", "run"]
