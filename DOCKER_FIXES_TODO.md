# Docker - Diagnostic & Corrections

## Problèmes identifiés
- [x] **Frontend: pas de `fileReplacements`** dans angular.json → le build prod utilise `environment.ts` (localhost:8080) au lieu de `environment.prod.ts` (/api). CAUSE PRINCIPALE des erreurs.
- [x] **Frontend Dockerfile: Node 20** alors qu'Angular 21 exige Node >= 20.19 / idéalement 22 → build instable.
- [x] **Frontend: pas de `.dockerignore`** → `node_modules`/`dist` locaux copiés dans l'image = code obsolète + build lent/cassé.
- [x] **Backend `.dockerignore` vide** → `target/`, `uploads/` copiés → "ne prend pas la dernière version" + jar obsolète possible.
- [x] **docker-compose `version: '4.32'`** invalide (obsolète + valeur erronée) → warning/erreur.
- [x] **CORS backend** limité à `http://localhost:4200` → requêtes via Nginx (port 80) refusées.
- [x] **Cache de build Docker** → ne reconstruit pas la dernière version.

## Corrections
- [x] Ajouter fileReplacements prod dans angular.json
- [x] Mettre Node 22 dans frontend/Dockerfile
- [x] Créer frontend/.dockerignore
- [x] Remplir backend/.dockerignore
- [x] Corriger docker-compose.yml (retirer version, healthcheck wget alpine)
- [x] Corriger CORS WebConfig
- [x] Vérifier le build localement
- [x] Push branche + PR
