# 01Blog

Une plateforme de blog communautaire pensée comme un réseau social étudiant : publiez des articles, suivez d'autres utilisateurs, commentez, et modérez la communauté grâce à un système de signalement.

## ✨ Fonctionnalités

- **Comptes utilisateurs** — inscription et authentification
- **Publications** — création de posts avec jusqu'à 3 images par post
- **Interactions sociales** — commentaires sur les posts, système de follow entre utilisateurs
- **Modération** — signalement des posts et des profils utilisateurs
- **Rôle administrateur** — suppression des posts signalés, bannissement / débannissement des utilisateurs

## 🛠️ Stack technique

| Couche | Technologie |
|---|---|
| Backend | Java, Spring Boot |
| Frontend | Angular, TypeScript, SCSS |
| Conteneurisation | Docker, Docker Compose |
| Serveur web (frontend) | Nginx |

## 🚀 Lancer le projet en local

Prérequis : [Docker](https://www.docker.com/) et Docker Compose installés.

```bash
git clone https://github.com/agieloth/01blog.git
cd 01blog
docker-compose up
```

L'application backend et frontend démarrent automatiquement dans des conteneurs séparés.

## 📁 Structure du projet

```
.
├── backend/          # API Spring Boot
│   └── src/
├── frontend/          # Application Angular
│   └── src/
└── docker-compose.yml # Orchestration des services
```

## 📌 À venir
<!-- A ajouter avec "![Description](chemin/vers/image.png)" -->
- Captures d'écran de l'interface

---

*Projet développé dans le cadre de ma formation en développement full-stack à Zone01 Oujda.*