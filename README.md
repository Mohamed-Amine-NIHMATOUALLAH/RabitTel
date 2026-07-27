# RabitTel

Système de gestion des lignes télécoms pour Tawfiq Microfinance (filiale du Groupe BCP).

Projet de Fin d'Année (PFA) — EMSI, 4e année DSI/IADA.

## Contexte

Tawfiq Microfinance gère plusieurs types de lignes télécoms (FTTH, RTC, VPN ADSL, 4G, 4G VPN, GSM Pro) réparties entre 13 directions régionales et le siège. RabitTel automatise l'extraction des données (fichiers Excel), le rapprochement base/factures, la gestion des lignes, des forfaits et des contrats, ainsi que le reporting.

## Architecture

Architecture microservices :

- `discovery-service` — Eureka Server (annuaire des services)
- `config-service` — Config Server (configuration centralisée)
- `gateway-service` — API Gateway (point d'entrée unique)
- `lignes-service` — gestion des lignes, agences, forfaits, contrats
- `utilisateurs-service` — authentification et gestion des rôles (à venir)
- `extraction-service` — extraction des fichiers Excel (à venir)
- `rapprochement-service` — moteur de rapprochement (à venir)
- `notification-service` — notifications email (à venir)

## Stack technique

| Composant | Technologie |
|---|---|
| Frontend | React |
| Backend métier | Spring Boot 3.x (Java 21) |
| Service d'extraction | Python 3 + FastAPI |
| Communication asynchrone | Apache Kafka (à partir de V3) |
| Base de données | PostgreSQL |
| Conteneurisation | Docker |

## Structure du repository

```
RabitTel/
├── RabitTel-backend/
│   ├── discovery-service/
│   ├── config-service/
│   ├── gateway-service/
│   ├── lignes-service/
│   └── ...
├── RabitTel-frontend/
├── docs/
├── README.md
└── .gitignore
```

## Démarrage du projet

### Prérequis
- Java 21 (JDK)
- Maven
- Node.js (pour le frontend)
- PostgreSQL

### Lancer `lignes-service`
```bash
cd RabitTel-backend/lignes-service
mvn spring-boot:run
```

### Lancer le frontend
```bash
cd RabitTel-frontend
npm install
npm start
```

## Roadmap

- **V1** — Socle fonctionnel (MVP) : gestion des lignes, import manuel, rapprochement simple
- **V2** — Intelligence métier : moteur de rapprochement complet, notifications d'expiration
- **V3** — Automatisation : ingestion email automatique, Kafka
- **V4** — Reporting et supervision
- **V5** — Déploiement et intégration serveur

## Documentation

Le cahier des charges et les diagrammes UML sont disponibles dans le dossier `docs/`.
