# # ── Stage 1 : Build Angular ──────────────────────────────────────────────────
# FROM node:20-alpine AS builder

# WORKDIR /app

# COPY package.json package-lock.json ./
# RUN npm ci --legacy-peer-deps

# COPY . .
# RUN npm run build -- --configuration=production

# # ── Stage 2 : Nginx ──────────────────────────────────────────────────────────
# FROM nginx:1.27-alpine

# RUN rm -rf /usr/share/nginx/html/*

# COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html
# COPY nginx.conf /etc/nginx/conf.d/default.conf

# EXPOSE 80

# CMD ["nginx", "-g", "daemon off;"]




# ============================================================
# Stage 1: Build Angular
# ============================================================
FROM node:22-alpine AS build

WORKDIR /app

# Copier package.json et package-lock.json (optimisation cache)
COPY package*.json ./
RUN npm ci --legacy-peer-deps

# Copier le code source et builder
COPY . .
RUN npm run build

# ============================================================
# Stage 2: Runtime (Nginx)
# ============================================================
FROM nginx:alpine

# Copier la configuration Nginx personnalisée
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copier les fichiers buildés
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Port exposé
EXPOSE 80

# Démarrer Nginx
CMD ["nginx", "-g", "daemon off;"]