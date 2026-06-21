import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { routes } from './app.routes';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    // FIX : provideZoneChangeDetection initialise correctement le scheduler
    // de détection de changements avec Zone.js pour Angular 17+.
    // eventCoalescing: true regroupe les événements consécutifs en un seul
    // cycle de CD → meilleure performance et rendu fiable au premier chargement.
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([jwtInterceptor])),
    provideAnimationsAsync(),
  ]
};
