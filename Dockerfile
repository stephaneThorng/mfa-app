FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copie n'importe quel jar/war produit par Maven
#COPY target/*.jar app.jar
# ou si tu veux accepter jar + war :
COPY target/*.war app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
