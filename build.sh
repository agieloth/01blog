#!/bin/bash
# ============================================================
# StudentHub - Build & Run Script
# ============================================================

set -e

# Couleurs
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}============================================${NC}"
echo -e "${GREEN}🚀 StudentHub - Build & Run${NC}"
echo -e "${GREEN}============================================${NC}"

# Vérifier que Docker est installé
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker n'est pas installé.${NC}"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose n'est pas installé.${NC}"
    exit 1
fi

# Charger les variables d'environnement
if [ -f .env ]; then
    echo -e "${YELLOW}📄 Chargement des variables depuis .env${NC}"
    export $(grep -v '^#' .env | xargs)
fi

# Fonction: build
build() {
    echo -e "${YELLOW}🔨 Build des images Docker...${NC}"
    docker-compose build
    echo -e "${GREEN}✅ Build terminé !${NC}"
}

# Fonction: up
up() {
    echo -e "${YELLOW}🚀 Lancement des conteneurs...${NC}"
    docker-compose up -d
    echo -e "${GREEN}✅ Conteneurs lancés !${NC}"
    echo ""
    echo -e "📌 Accès:"
    echo -e "   🌐 Frontend:  http://localhost:${FRONTEND_PORT:-80}"
    echo -e "   🔧 Backend:   http://localhost:${BACKEND_PORT:-8080}"
    echo -e "   🗄️ Database:  localhost:${POSTGRES_PORT:-5432}"
    echo ""
    echo -e "${YELLOW}📋 Logs: docker-compose logs -f${NC}"
}

# Fonction: down
down() {
    echo -e "${YELLOW}🛑 Arrêt des conteneurs...${NC}"
    docker-compose down
    echo -e "${GREEN}✅ Conteneurs arrêtés.${NC}"
}

# Fonction: logs
logs() {
    docker-compose logs -f "$@"
}

# Fonction: clean
clean() {
    echo -e "${YELLOW}🧹 Nettoyage des conteneurs et volumes...${NC}"
    docker-compose down -v
    echo -e "${GREEN}✅ Nettoyage terminé.${NC}"
}

# ── Menu ──────────────────────────────────────────────────────

case "$1" in
    build)
        build
        ;;
    up)
        up
        ;;
    down)
        down
        ;;
    restart)
        down
        up
        ;;
    logs)
        shift
        logs "$@"
        ;;
    clean)
        clean
        ;;
    *)
        echo -e "${YELLOW}Usage:${NC}"
        echo "  ./build.sh build    - Construire les images Docker"
        echo "  ./build.sh up       - Lancer les conteneurs (en arrière-plan)"
        echo "  ./build.sh down     - Arrêter les conteneurs"
        echo "  ./build.sh restart  - Redémarrer les conteneurs"
        echo "  ./build.sh logs     - Afficher les logs"
        echo "  ./build.sh clean    - Arrêter et supprimer les volumes"
        ;;
esac