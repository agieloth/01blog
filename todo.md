# 📋 Analyse & Corrections — 01blog

## 🔍 Bugs & Problèmes identifiés

### Backend
- [ ] **[BUG] AdminService** — `deletePost()` supprime manuellement likes/comments alors que les relations JPA ont `CascadeType.ALL` → double suppression possible + NPE
- [ ] **[BUG] AdminService** — `toggleBanUser()` et `deletePost()` lèvent `RuntimeException` au lieu de `ResourceNotFoundException`
- [ ] **[BUG] AdminService** — utilise `@Autowired` (field injection) au lieu de l'injection par constructeur (incohérence avec le reste du projet, non testable)
- [ ] **[BUG] PostService** — `deletePost()` utilisateur normal : ne vérifie pas si l'utilisateur est banni avant d'agir
- [ ] **[BUG] UserDetailsServiceImpl** — Ne vérifie pas `isBanned` → un utilisateur banni peut toujours s'authentifier via JWT
- [ ] **[BUG] SecurityConfig** — Double intercepteur JWT (auth.interceptor + jwt.interceptor) dans app.config : seul `authInterceptor` est enregistré, le `jwtInterceptor` (avec gestion 401) est ignoré
- [ ] **[BUG] JwtAuthFilter** — Ne gère pas `ExpiredJwtException` ni `MalformedJwtException` → peut causer des 500 au lieu de 401
- [ ] **[BUG] PostResponse** — `ObjectMapper` statique non thread-safe dans un contexte Spring (doit être un bean injecté)
- [ ] **[BUG] FileStorageService** — `getOriginalFilename()` peut retourner `null` → NPE dans `StringUtils.cleanPath()`
- [ ] **[BUG] AdminController** — Duplication fonctionnelle avec `ReportController` (même endpoint `/api/admin/reports` vs `/api/reports`)
- [ ] **[BUG] ReportService** — `updateReportStatus` dans AdminService ne met pas `reviewedAt` ni `reviewedBy` (duplication incomplète)

### Frontend
- [ ] **[BUG] app.config.ts** — Deux intercepteurs existent (`authInterceptor` et `jwtInterceptor`) mais seul `authInterceptor` est enregistré → gestion 401/logout manquante
- [ ] **[BUG] FeedComponent** — `loadComments()` est appelé même si les commentaires sont déjà chargés, et `subscribeToComments()` crée plusieurs abonnements WebSocket si on ouvre/ferme plusieurs fois
- [ ] **[BUG] WebSocketService** — `connect()` appelé depuis `app.ts` mais non connecté au cycle de vie de l'utilisateur (pas de reconnexion si changement d'utilisateur)
- [ ] **[BUG] NavbarComponent** — `loadNotifications()` appelée à chaque rendu sans vérifier si l'utilisateur est connecté → erreur 401 si non connecté
- [ ] **[BUG] ProfileComponent** — Appels HTTP imbriqués (getUserStats + getUserPosts) → devrait utiliser `forkJoin`
- [ ] **[BUG] AdminComponent** — `switchTab()` ne recharge pas les données si on revient sur un onglet précédemment chargé mais potentiellement obsolète

### Sécurité
- [ ] **[SÉCURITÉ] application.properties** — Clé JWT hardcodée en clair dans le fichier de config commité
- [ ] **[SÉCURITÉ] WebSocketConfig** — `setAllowedOriginPatterns("*")` trop permissif en prod
- [ ] **[SÉCURITÉ] DataInitializer** — Mots de passe admin/test hardcodés (acceptable en dev seulement)

## ✅ Corrections appliquées — PR #1

### Priorité 1 — Bugs critiques
- [x] Fix UserDetailsServiceImpl : bloquer les utilisateurs bannis (enabled=false)
- [x] Fix JwtAuthFilter : gérer ExpiredJwtException, MalformedJwtException, SignatureException → 401/403
- [x] Fix app.config.ts : enregistrer jwtInterceptor avec gestion 401/logout automatique
- [x] Fix AdminService : injection par constructeur + ResourceNotFoundException
- [x] Fix AdminService.deletePost : suppression manuelle redondante retirée (CascadeType.ALL)
- [x] Fix GlobalExceptionHandler : ajouter DisabledException → 403 Forbidden

### Priorité 2 — Qualité & Cohérence
- [x] Fix PostResponse : ObjectMapper statique retiré, délégué à PostService (thread-safe)
- [x] Fix PostService : adapté à la nouvelle signature PostResponse, ObjectMapper injecté
- [x] Fix FileStorageService : null-check getOriginalFilename() + protection path traversal
- [x] Fix FeedComponent : Set<number> commentWsSubs pour éviter doublons WS + cleanup ngOnDestroy
- [x] Fix ProfileComponent : forkJoin pour getUserStats + getUserPosts en parallèle
- [x] Fix NavbarComponent : guard isAuthenticated() avant loadNotifications()
- [x] Fix AdminComponent : recharger les données à chaque switchTab()
- [x] Fix AuthService : validation structure User localStorage + replaceUrl sur logout

### Priorité 3 — Sécurité
- [x] Fix application.properties : JWT_SECRET externalisable via variable d'environnement
- [x] Fix WebSocketConfig : restreindre allowedOriginPatterns (plus de wildcard *)

## 🔗 Pull Request
https://github.com/agieloth/01blog/pull/1