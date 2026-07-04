# Gestion des absences médicales des étudiants


https://github.com/user-attachments/assets/ac9a8c0d-0e0d-4ce2-81ce-87ccdb9f1357



# 🏥 Gestion des Absences Médicales
### Application Web — Spring Boot 3.2 + React CDN + MySQL

---

##  Description du projet

Système complet de gestion des absences médicales des étudiants, développé dans le cadre du module **Développement Web et Mobile** à la Faculté des Sciences et Techniques de Marrakech.

La solution permet aux étudiants de soumettre leurs demandes d'absence depuis un navigateur web, et aux agents administratifs de les traiter via un tableau de bord moderne. Toutes les données sont sécurisées par authentification JWT.

---

##  Lancement rapide

### Prérequis

| Outil | Version | Téléchargement |
|-------|---------|----------------|
| Java JDK | 21+ | https://adoptium.net |
| IntelliJ IDEA | 2024+ | https://jetbrains.com/idea |
| XAMPP | Dernière | https://apachefriends.org |
| Navigateur | Chrome / Firefox / Edge | — |

>  **Node.js non requis** — le frontend React est chargé via CDN

---

### Étape 1 — Démarrer MySQL via XAMPP

1. Ouvrir **XAMPP Control Panel**
2. Cliquer **Start** à côté de **MySQL**
3. La base `absences_db` sera créée automatiquement au démarrage

---

### Étape 2 — Ouvrir le backend dans IntelliJ

1. `File` → `Open` → sélectionner le dossier **`backend/`**
2. Cliquer **Trust Project** si demandé
3. Attendre que Maven télécharge les dépendances
4. `File` → `Project Structure` → `SDK` → choisir **Java 21**

---

### Étape 3 — Lancer l'application

1. Ouvrir `src/main/java/com/absences/GestionAbsencesApplication.java`
2. Cliquer le bouton ▶️ vert à côté de `main`
3. Attendre les logs de démarrage dans la console

Console attendue :
```
 Admin créé: admin@absences.ma / admin123
 Agent créé: agent@absences.ma / agent123
 Etudiant créé: etudiant@absences.ma / etudiant123
Tomcat started on port 8080
Started GestionAbsencesApplication
```

---

### Étape 4 — Accéder à l'application

Ouvrir dans le navigateur :

```
http://localhost:8080
```

---

##  Comptes par défaut

Les comptes suivants sont créés automatiquement au premier démarrage :

| Rôle | Email | Mot de passe | Accès |
|------|-------|-------------|-------|
|  Administrateur | admin@absences.ma | admin123 | Dashboard + Absences + Utilisateurs |
|  Agent | agent@absences.ma | agent123 | Dashboard + Traitement des absences |
|  Étudiant | etudiant@absences.ma | etudiant123 | Déclarer + Suivre ses absences |

---

##  Architecture du projet

```
GestionAbsences-HTML/
│
├── backend/                          ← Spring Boot API REST
│   ├── src/main/java/com/absences/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java   ← JWT + CORS + règles d'accès
│   │   │   └── DataInitializer.java  ← Création des comptes par défaut
│   │   ├── controllers/
│   │   │   ├── AuthController.java   ← POST /api/auth/login & register
│   │   │   ├── AbsenceController.java← CRUD absences + validation
│   │   │   ├── UserController.java   ← CRUD utilisateurs (admin)
│   │   │   └── FileController.java   ← Servir les justificatifs
│   │   ├── entities/
│   │   │   ├── User.java             ← Table users
│   │   │   ├── Absence.java          ← Table medical_absences
│   │   │   ├── Document.java         ← Table documents
│   │   │   └── StatusLog.java        ← Table absence_status_logs
│   │   ├── repositories/             ← Interfaces JPA
│   │   ├── services/                 ← Logique métier
│   │   ├── security/
│   │   │   ├── JwtUtil.java          ← Génération/validation JWT
│   │   │   └── JwtAuthFilter.java    ← Filtre d'authentification
│   │   └── enums/
│   │       ├── Role.java             ← ETUDIANT, AGENT, ADMIN
│   │       └── AbsenceStatus.java    ← EN_ATTENTE, EN_COURS, ACCEPTEE, REFUSEE
│   └── src/main/resources/
│       ├── static/
│       │   └── index.html            ← Frontend React (servi par Spring Boot)
│       └── application.properties   ← Configuration base de données + JWT
│
└── frontend/
    └── index.html                    ← Copie du frontend (backup)
```

---

##  Base de données

La base `absences_db` contient 4 tables :

```sql
-- Utilisateurs (étudiants, agents, administrateurs)
CREATE TABLE users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nom        VARCHAR(100) NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       ENUM('ETUDIANT','AGENT','ADMIN') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Demandes d'absence médicale
CREATE TABLE medical_absences (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT NOT NULL,
    start_date    DATE NOT NULL,
    end_date      DATE NOT NULL,
    reason        TEXT,
    status        ENUM('EN_ATTENTE','EN_COURS','ACCEPTEE','REFUSEE') DEFAULT 'EN_ATTENTE',
    agent_comment TEXT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Justificatifs médicaux uploadés
CREATE TABLE documents (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    absence_id  INT NOT NULL,
    file_path   VARCHAR(255) NOT NULL,
    file_type   VARCHAR(50),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (absence_id) REFERENCES medical_absences(id) ON DELETE CASCADE
);

-- Historique des changements de statut
CREATE TABLE absence_status_logs (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    absence_id  INT NOT NULL,
    changed_by  INT,
    old_status  VARCHAR(50),
    new_status  VARCHAR(50) NOT NULL,
    comment     TEXT,
    changed_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (absence_id) REFERENCES medical_absences(id) ON DELETE CASCADE,
    FOREIGN KEY (changed_by)  REFERENCES users(id) ON DELETE SET NULL
);
```

---

##  API REST — Endpoints principaux

| Méthode | Endpoint | Rôle requis | Description |
|---------|----------|-------------|-------------|
| POST | `/api/auth/login` | Public | Connexion — retourne un token JWT |
| POST | `/api/auth/register` | Public | Inscription d'un étudiant |
| GET | `/api/absences/mes-absences` | ETUDIANT | Liste des absences de l'étudiant connecté |
| POST | `/api/absences` | ETUDIANT | Créer une absence + upload justificatif |
| GET | `/api/absences` | AGENT, ADMIN | Liste complète de toutes les absences |
| GET | `/api/absences/{id}` | Authentifié | Détail d'une absence |
| POST | `/api/absences/validate` | AGENT, ADMIN | Valider une absence (commentaire obligatoire) |
| POST | `/api/absences/refuse` | AGENT, ADMIN | Refuser une absence (commentaire obligatoire) |
| POST | `/api/absences/encours` | AGENT, ADMIN | Passer une absence en cours de traitement |
| GET | `/api/absences/{id}/logs` | Authentifié | Historique des décisions d'une absence |
| GET | `/api/users` | ADMIN | Liste de tous les utilisateurs |
| POST | `/api/users` | ADMIN | Créer un utilisateur |
| PUT | `/api/users/{id}` | ADMIN | Modifier un utilisateur |
| DELETE | `/api/users/{id}` | ADMIN | Supprimer un utilisateur |
| GET | `/uploads/{filename}` | Public | Télécharger un justificatif |

---

##  Fonctionnalités par rôle

###  Étudiant
- Connexion / Inscription
- Déclarer une absence avec dates, motif et justificatif obligatoire (PDF, JPG, PNG — max 5 Mo)
- Consulter la liste de ses absences avec statut coloré
- Voir le détail d'une absence et l'historique complet des décisions
- Consulter le commentaire laissé par l'agent

###  Agent administratif
- Consulter toutes les absences avec filtres (statut, recherche)
- Visualiser les justificatifs médicaux
- Valider ou refuser une absence avec commentaire obligatoire
- Passer une absence en cours de traitement
- Accéder au tableau de bord statistique (taux d'acceptation, répartition)

###  Administrateur
- Toutes les fonctionnalités de l'agent
- Gérer les comptes utilisateurs : créer, modifier, supprimer
- Filtrer les utilisateurs par rôle
- Accéder aux statistiques globales

---

##  Sécurité

| Mécanisme | Détail |
|-----------|--------|
| **JWT** | Token signé, valide 24h, envoyé dans le header `Authorization: Bearer <token>` |
| **BCrypt** | Mots de passe hachés avec un facteur de coût 10, jamais stockés en clair |
| **RBAC** | Contrôle d'accès basé sur les rôles — chaque endpoint vérifie le rôle de l'utilisateur |
| **SQL préparé** | Requêtes JPA paramétrées pour éviter les injections SQL |
| **Upload sécurisé** | Fichiers renommés avec UUID, types vérifiés côté serveur |

---

##  Responsive Design

L'interface s'adapte automatiquement à toutes les tailles d'écran :

| Taille | Comportement |
|--------|-------------|
|  Mobile `< 768px` | Menu hamburger, cartes empilées, tableau → cartes |
|  Tablette `768–1024px` | Sidebar fixe, grilles en 2 colonnes |
|  Desktop `> 1024px` | Layout complet, stats en 5 colonnes, tableau utilisateurs |

---

##  Stack technique

| Composant | Technologie | Version |
|-----------|-------------|---------|
| Backend | Spring Boot | 3.2.0 |
| ORM | Hibernate / JPA | 6.3 |
| Sécurité | Spring Security + JWT (jjwt) | 6.2 / 0.11.5 |
| Base de données | MySQL | 8.x |
| Upload fichiers | Multer (Spring Multipart) | — |
| Frontend | React | 18 (CDN) |
| Transpileur | Babel Standalone | CDN |
| Requêtes HTTP | Fetch API native | — |

---

##  Configuration

Le fichier `backend/src/main/resources/application.properties` :

```properties
# Base de données
spring.datasource.url=jdbc:mysql://localhost:3306/absences_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=          ← Modifier si MySQL a un mot de passe

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false

# JWT
jwt.secret=AbsencesMedicalesSecretKeyLongueEtSecurisee2024JWTSpringBoot
jwt.expiration=86400000              ← 24 heures en millisecondes

# Upload
file.upload-dir=uploads
spring.servlet.multipart.max-file-size=5MB

# Serveur
server.port=8080
```

---

##  Dépannage

| Problème | Cause probable | Solution |
|----------|---------------|----------|
| `Connection refused` au démarrage | MySQL non démarré | Lancer MySQL dans XAMPP |
| `Access denied for user 'root'` | Mauvais mot de passe MySQL | Mettre à jour `application.properties` |
| Page blanche sur `localhost:8080` | Backend non démarré | Vérifier la console IntelliJ |
| `Impossible de contacter le serveur` | Ouverture du fichier HTML directement | Utiliser `http://localhost:8080` uniquement |
| Upload échoue | Fichier > 5 Mo ou mauvais format | Utiliser PDF, JPG, PNG < 5 Mo |
| Token invalide | Session expirée (> 24h) | Se reconnecter |

---

##  Auteur

Projet académique — Licence SIR, FST Marrakech  
Module : Développement Web et Mobile  
Année universitaire : 2025–2026

---

> **Note :** Ce projet est à usage éducatif. Pour un déploiement en production, il faudrait configurer HTTPS, renforcer le secret JWT, et mettre en place une stratégie de sauvegarde de la base de données.
