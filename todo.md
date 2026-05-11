# Todo - Feature: Hide Post + Audit Check

## 1. Analyse & Audit
- [x] Lire audit.md
- [x] Explorer la structure du projet
- [x] Lire les fichiers clés (Post entity, AdminController, AdminService, admin.ts, admin.html, models, admin.service.ts)

## 2. Backend - Feature "Masquer un post"
- [ ] Ajouter champ `hidden` (boolean) à l'entité `Post`
- [ ] Ajouter méthode `toggleHidePost()` dans `AdminService`
- [ ] Ajouter endpoint `PATCH /api/admin/posts/{id}/hide` dans `AdminController`
- [ ] Exposer le champ `hidden` dans les réponses GET /api/admin/posts
- [ ] Filtrer les posts masqués dans `PostService.getAllPosts()` (invisibles aux users normaux)
- [ ] Exposer le champ `hidden` dans `PostResponse` / `PostService`

## 3. Frontend - Feature "Masquer un post"
- [ ] Ajouter `hidden` et `hidePost()` dans `AdminService` (admin.service.ts)
- [ ] Ajouter `hidden` au modèle `AdminPost` (models.ts)
- [ ] Ajouter état modal "masquer/démasquer" dans `admin.ts`
- [ ] Ajouter bouton "Masquer/Afficher" + modal confirmation dans `admin.html`

## 4. Push & PR
- [ ] Créer un commit avec les changements
- [ ] Pousser la branche
- [ ] Créer une Pull Request