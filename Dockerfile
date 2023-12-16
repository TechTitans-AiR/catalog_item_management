FROM openjdk:19-jdk-alpine

# Instaliraj Maven
RUN apk add --no-cache maven

# Postavi radni direktorij
WORKDIR /usr/src/app

# Kopiraj POM i sve potrebne datoteke za preuzimanje dependencija
COPY ./pom.xml .
COPY ./src ./src

# Postavi varijable okoline koje će biti dostupne tijekom izvođenja Docker slike
ARG MONGO_URI

# Stvori .env datoteku unutar Docker kontejnera
RUN echo "MONGO_URI=${MONGO_URI}" > src/main/resources/.env

# Izgradi aplikaciju
RUN mvn clean install

# Pokreni aplikaciju na portu 8081
CMD ["java", "-jar", "-Dserver.port=8081", "target/catalog_item_management.jar"]
