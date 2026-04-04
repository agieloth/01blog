// En production Docker, Nginx proxifie /api → backend:8080/api
// et /ws → backend:8080/ws
// On utilise des URLs relatives → fonctionne sur n'importe quel domaine/IP
export const environment = {
  production: true,
  apiUrl: '/api',
  wsUrl:  '/ws'
};