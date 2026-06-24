# ── Stage 1 : Build Angular ───────────────────────────────────────────────
FROM node:22-alpine AS builder

WORKDIR /app

# Cache des dépendances : on copie d'abord les manif/lock
COPY package.json package-lock.json ./
RUN npm ci --legacy-peer-deps

# Code source (node_modules/dist sont exclus via .dockerignore)
COPY . .

# Build de production (utilise environment.prod.ts via fileReplacements)
RUN npm run build -- --configuration=production

# ── Stage 2 : Nginx ────────────────────────────────────────────────────────
FROM nginx:1.27-alpine

RUN rm -rf /usr/share/nginx/html/*

COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
