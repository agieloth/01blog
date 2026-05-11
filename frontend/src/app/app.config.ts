import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';

/**
 * FIX : Remplace authInterceptor par jwtInterceptor
 *
 * jwtInterceptor fait tout ce que authInterceptor fait (ajout du header Bearer)
 * PLUS la gestion des erreurs 401/403 → déconnexion automatique si token expiré.
 *
 * authInterceptor était redondant et ne gérait pas le cas d'expiration du token.
 * Ne pas enregistrer les deux : cela causerait le token d'être ajouté deux fois.
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([jwtInterceptor])),
  ]
};