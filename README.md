# ⏱️ Lab 16 : Chronomètre avec Foreground Service (Java)

### 🎓 Cours : Programmation Mobile - Android

Ce projet démontre la maîtrise des **Services Android** en utilisant une architecture robuste qui combine un **Foreground Service** (Service de premier plan) pour la persistance et un **Bound Service** (Service lié) pour l'interaction en temps réel avec l'interface utilisateur.

---

## 📺 Démonstration Vidéo

*Ajoutez ici votre vidéo montrant le démarrage, le fonctionnement en arrière-plan (notification) et l'arrêt du service.*

[![Regarder la vidéo](https://img.shields.io/badge/VIDÉO-DÉMO-red?style=for-the-badge&logo=youtube)](LIEN_VERS_VOTRE_VIDEO_ICI)

---

## 🚀 Fonctionnalités Clés

- **Service de Premier Plan (Foreground)** : Le chronomètre continue de fonctionner même si l'application est fermée ou réduite.
- **Notification Persistante** : Affichage du temps en direct dans le tiroir de notifications, obligatoire depuis Android 8.0 (Oreo).
- **Communication Bidirectionnelle (Binding)** : Utilisation d'un `IBinder` pour permettre à la `MainActivity` de lire les données du service en temps réel.
- **Gestion des Permissions Modernes** : Support complet pour Android 13+ (API 33+) avec demande dynamique de la permission `POST_NOTIFICATIONS`.
- **UI Réactive** : Interface épurée avec mise à jour automatique via un `Handler`.

---

## 🛠️ Architecture Technique

### 1. Le Service (`ChronometreService.java`)
- **`onStartCommand`** : Utilise `START_STICKY` pour garantir que le système recrée le service s'il manque de mémoire.
- **`ScheduledExecutorService`** : Utilisé pour incrémenter le temps de manière précise et sécurisée (Thread-safe).
- **`NotificationChannel`** : Configuration obligatoire pour les API 26+.

### 2. L'Activité (`MainActivity.java`)
- **`ServiceConnection`** : Gère le cycle de vie de la connexion entre l'interface et le service.
- **`Handler/Runnable`** : Assure le rafraîchissement de l'affichage (TextView) toutes les secondes sans bloquer le thread principal.

### 3. Sécurité & Manifest
- Déclaration du type de service `dataSync` pour la compatibilité Android 14.
- Permissions spécifiques déclarées pour le bon fonctionnement en arrière-plan.

---

## 🎨 Design de l'Interface
L'application utilise une palette de couleurs contrastée pour une meilleure expérience utilisateur :
- **Démarrer** : Vert Émeraude (`#4CAF50`)
- **Arrêter** : Rouge Corail (`#F44336`)
- **Texte** : Gris Anthracite pour une lisibilité maximale.

---

## ⚙️ Prérequis & Installation
- **Minimum SDK** : API 24 (Android 7.0)
- **Cible SDK** : API 34 (Android 14)
- **Langage** : Java
- **IDE** : Android Studio

1. Clonez le dépôt.
2. Synchronisez le projet avec Gradle.
3. Lancez sur un émulateur ou un appareil physique (API 26+ recommandé pour tester les notifications).

---

## ✍️ Auteur
**[Votre Nom]**  
*Étudiant en Développement Mobile*
