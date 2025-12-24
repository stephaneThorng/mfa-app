Parfait 👍
Voici un **README orienté projet / produit**, sans angle “cours” ou “apprentissage”, rédigé comme pour un **repository professionnel**.

Tu peux le copier tel quel dans ton dépôt GitHub.

---

# 🔐 Two-Factor Authentication Platform (2FA)

## 📖 Description

Ce projet implémente une **plateforme de double authentification (2FA)** basée sur une architecture **microservices**, conçue pour être **scalable, découplée et résiliente**.

Le système permet :

* la génération de **codes OTP temporaires**
* l’envoi des codes via un service de notification
* la validation sécurisée des codes fournis par l’utilisateur
* une communication **événementielle** entre services

L’ensemble est entièrement **conteneurisé** et peut être lancé localement via Docker.

---

## 🧩 Architecture globale

L’application repose sur :

* une **architecture hexagonale (Ports & Adapters)** par microservice
* une communication **asynchrone via Kafka**
* une séparation stricte entre **logique métier** et **infrastructure**

### Microservices

| Service                  | Description                            |
| ------------------------ | -------------------------------------- |
| **auth-service**         | Génération et validation des codes OTP |
| **notification-service** | Envoi des codes OTP (email/SMS simulé) |

```
├── domain 
│ └── model 
├── application 
│ └── usecase 
├── ports 
│ ├── in 
│ └── out 
├── adapters 
│ ├── in 
│ │ └── rest 
│ └── out 
│ ├── cache 
│ └── messaging 
└── Application.java
```

---

## 🔄 Flux fonctionnels

### Génération d’un code 2FA

```
Client → auth-service (REST)
        → Génération OTP
        → Stockage Redis (TTL)
        → Publication événement Kafka (OtpGenerated)
```

### Envoi du code

```
Kafka → notification-service
      → Envoi du code (mock)
```

### Validation du code

```
Client → auth-service (REST)
        → Vérification Redis
        → Succès / échec
```

---

## 🧱 Architecture hexagonale (par service)

Chaque microservice est structuré selon les principes suivants :

* **Domain**

    * Entités métier (OTP, identifiant utilisateur)
    * Règles de validation (expiration, tentatives)

* **Application**

    * Cas d’usage (génération, validation)

* **Ports**

    * Interfaces définissant les dépendances externes

* **Adapters**

    * REST API
    * Kafka (publisher / consumer)
    * Redis
    * PostgreSQL

Cette organisation garantit :

* un faible couplage
* une forte testabilité
* une évolutivité facilitée

---

## 🗄️ Gestion des données

### Rôles des technologies

| Technologie        | Rôle                                                |
| ------------------ | --------------------------------------------------- |
| **Redis**          | Stockage temporaire des codes OTP (TTL, tentatives) |
| **PostgreSQL**     | Persistance des données d’audit                     |
| **Kafka (KRaft)**  | Transport des événements entre services             |
| **Docker**         | Conteneurisation                                    |
| **Docker Compose** | Orchestration locale                                |

Kafka est utilisé **sans ZooKeeper**, en mode **KRaft**.

---

## 🌐 API REST (auth-service)

### Demande de code OTP

```http
POST /api/2fa/request
```

**Payload**

```json
{
  "userId": "user@example.com"
}
```

---

### Validation du code OTP

```http
POST /api/2fa/validate
```

**Payload**

```json
{
  "userId": "user@example.com",
  "code": "123456"
}
```

---

## 🐳 Lancement du projet

### Prérequis

* Docker
* Docker Compose

### Démarrage

```bash
docker-compose up --build
```

Les services suivants seront démarrés :

* auth-service
* notification-service
* Kafka
* Redis
* PostgreSQL

---

## 🔒 Sécurité & limitations

* Les canaux d’envoi (email/SMS) sont **simulés**
* Les codes OTP sont **temporaires** et supprimés après validation
* Le projet ne gère pas :

    * l’authentification complète des utilisateurs
    * OAuth / OpenID Connect
    * l’envoi réel de SMS ou emails

---

## 📈 Évolutions possibles

* Intégration d’un vrai fournisseur SMS / email
* Limitation avancée des tentatives
* Observabilité (metrics, tracing)
* Sécurisation des endpoints
* Scalabilité multi-brokers Kafka

---

## 📄 Licence

Projet sous licence libre — voir le fichier `LICENSE`.

---
