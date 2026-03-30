import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostService } from '../../core/services/post.service';
import { Post } from '../../core/models/models';
import { NavbarComponent } from '../../shared/navbar/navbar';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './feed.html',
  styleUrl: './feed.scss'
})
export class FeedComponent implements OnInit {
  posts: Post[] = [];
  loading = true;
  error = '';

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.loading = true;
    this.error = '';

    this.postService.getAllPosts().subscribe({
      next: (posts) => {
        this.posts = posts;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading posts:', err);
        this.error = 'Erreur lors du chargement des posts.';
        this.loading = false;
      }
    });
  }

  toggleLike(postId: number): void {
    this.postService.toggleLike(postId).subscribe({
      next: () => {
        // Recharger les posts pour mettre à jour le compteur
        this.loadPosts();
      },
      error: (err) => {
        console.error('Error toggling like:', err);
      }
    });
  }

  getInitial(username: string): string {
    return username ? username[0].toUpperCase() : '?';
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diff = (now.getTime() - date.getTime()) / 1000; // en secondes

    if (diff < 60) return "à l'instant";
    if (diff < 3600) return `il y a ${Math.floor(diff / 60)} min`;
    if (diff < 86400) return `il y a ${Math.floor(diff / 3600)} h`;
    
    return date.toLocaleDateString('fr-FR', { 
      day: 'numeric', 
      month: 'long', 
      year: 'numeric' 
    });
  }

  getImageUrl(filename: string): string {
    return `${environment.uploadsUrl}/${filename}`;
  }
}