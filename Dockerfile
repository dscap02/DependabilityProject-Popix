############################################################
# STAGE 1 — BUILD CON MAVEN + JDK 21
############################################################
# Usa l'immagine ufficiale Maven con Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Imposta la cartella di lavoro dentro il container
WORKDIR /app

# Copia solo il pom.xml per sfruttare la cache Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia il sorgente del progetto
COPY src ./src

# Compila il progetto e genera il WAR
RUN mvn clean package -DskipTests


############################################################
# STAGE 2 — RUNTIME CON TOMCAT 9.0.86 + JDK21
############################################################
FROM tomcat:9.0.86-jdk21

# Rimuove le app preinstallate da Tomcat (ROOT, docs, examples)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia il WAR generato nello stage di build
COPY --from=build /app/target/popix-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Espone la porta 8080
EXPOSE 8080

# Avvia Tomcat
CMD ["catalina.sh", "run"]
