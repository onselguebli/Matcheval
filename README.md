# Matcheval – Intelligent Recruitment Platform

Matcheval est une application web de recrutement basée sur une architecture **Spring Boot (backend)**, **Angular (frontend)** et une **API Flask (IA)** pour le matching intelligent des CVs avec les offres d’emploi.  
L’objectif est de centraliser la gestion des candidats, recruteurs, managers et administrateurs dans un seul outil moderne, riche en statistiques et automatisations.

---

## 🚀 Architecture

### 🖥️ Frontend (Angular – CoreUI Template)
- Framework : **Angular 17**  
- UI : **CoreUI Angular Admin Template**  
- Styling : **SCSS moderne (dark theme, responsive, lisible)**  
- Fonctionnalités front :
  - Authentification (JWT) + redirection selon rôle.
  - Dashboards riches avec graphiques (Chart.js / CoreUI).
  - Gestion des offres et candidatures (Recruteur).
  - Gestion des utilisateurs et statistiques globales (Admin).
  - MeetingRoom avec intégration **Jitsi Meet** (Manager).
  - Visualisation de CVs (PDF viewer intégré).
  - Publication multi-sites des offres.

### ⚙️ Backend (Spring Boot – Java)
- Langage : **Java 17**  
- Framework : **Spring Boot 3.x**  
- Modules utilisés :
  - **Spring Security + JWT** pour l’authentification/autorisation.
  - **Spring Data JPA (Hibernate)** pour la persistance.
  - **Postgresql** comme base de données relationnelle.
- Architecture :  
  - `controller/` → APIs REST.  
  - `service/` → logique métier.  
  - `repo/` → accès aux données.  
  - `dto/` → transfert de données.  
  - `config/` → sécurité & configuration.  
- Fonctionnalités backend :
  - Authentification sécurisée (JWT, redirection par rôle).
  - Gestion des utilisateurs (Admin).
  - Gestion des offres (ajout, modification, suppression).
  - Gestion des candidatures et CVs.
  - Intégration avec l’API Flask pour le scoring IA.
  - Publication vers sites externes via webhook.

### 🤖 API IA (Flask – Python)
- Framework : **Flask**  
- Fichiers clés :
  - `app/` → endpoints Flask.
  - `requirements.txt` → dépendances (scikit-learn, xgboost, pandas, numpy…).  
- Fonctionnalités IA :
  - Parsing et prétraitement des CVs (extraction de texte).
  - Génération d’embeddings/mots-clés pour matching.
  - Calcul d’un score de correspondance (0–100).
  - Envoi du score au backend pour enrichir les candidatures.

---

## 🔑 Rôles & Fonctionnalités

### 👨‍💼 Admin
- Création/gestion des comptes utilisateurs.
- Envoi automatique d’un email avec identifiants.
- Activation/désactivation des comptes.
- Dashboard riche avec statistiques globales.

### 👩‍💻 Recruteur
- Gestion des offres (ajout, modification, suppression).
- Publication multi-sites des offres.
- Gestion des candidatures associées à ses offres.
- Visualisation des CVs.
- Sélection de CVs (CheckedMatch).
- Dashboard riche avec statistiques sur ses offres et candidatures.

### 👨‍🏫 Manager
- Visualisation des offres de ses recruteurs.
- Gestion des réunions/interviews via **Jitsi Meet**.
- Consultation des CVs présélectionnés.
- Dashboard riche avec indicateurs (offres, CVs, entretiens).

### 🧠 Matching IA
- Matching entre offres et CVs existants en base.
- Upload de CVs externes pour matching instantané.
- Sélection et stockage des résultats (CheckedMatch).
- Dashboards spécifiques pour visualiser les performances.

---

## 📊 Dashboards et Statistiques
- Evolution des **CVs, offres et recruteurs** par jour/mois/année.
- Taux de conversion candidatures → entretiens → validation.
- Statistiques par provenance (LinkedIn, ATS, upload direct).
- Graphiques dynamiques (Chart.js, CoreUI).

---

## 🔧 Installation & Lancement

### Backend (Spring Boot)
```bash
cd stage
mvn spring-boot:run
