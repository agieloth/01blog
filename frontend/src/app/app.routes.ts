import { Routes } from '@angular/router';
import { Component } from '@angular/core';
import { AuthGuard } from './core/guards/auth.guard';
// import { AdminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/feed',  // ← Redirige vers login au début
    pathMatch: 'full'
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  // COMMENTÉ TEMPORAIREMENT - On créera ces composants après
  {
    path: 'feed',
    canActivate: [AuthGuard],
    loadComponent: () => import('./features/feed/feed').then(m => m.FeedComponent)
  }
  // {
  //   path: 'profile/:id',
  //   canActivate: [AuthGuard],
  //   loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent)
  // },
  // {
  //   path: 'admin',
  //   canActivate: [AuthGuard, AdminGuard],
  //   loadComponent: () => import('./features/admin/admin.component').then(m => m.AdminComponent)
  // }
];