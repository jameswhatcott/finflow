#!/bin/bash

# FinFlow Docker Deployment Script

set -e

echo "🚀 Starting FinFlow Docker deployment..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker and try again."
    exit 1
fi

# Function to display usage
usage() {
    echo "Usage: $0 [dev|prod]"
    echo "  dev  - Deploy with H2 database (development)"
    echo "  prod - Deploy with PostgreSQL (production)"
    exit 1
}

# Check command line arguments
if [ $# -eq 0 ]; then
    echo "❌ Please specify deployment mode: dev or prod"
    usage
fi

MODE=$1

case $MODE in
    dev)
        echo "🔧 Deploying in development mode with H2 database..."
        
        # Stop existing containers
        docker-compose down
        
        # Build and start the application
        docker-compose up --build -d
        
        echo "✅ FinFlow is starting up..."
        echo "🌐 Access the application at: http://localhost:8081"
        echo "📊 H2 Console at: http://localhost:8081/h2-console"
        echo ""
        echo "📝 To view logs: docker-compose logs -f finflow-app"
        echo "🛑 To stop: docker-compose down"
        ;;
        
    prod)
        echo "🏭 Deploying in production mode with PostgreSQL..."
        
        # Stop existing containers
        docker-compose -f docker-compose.prod.yml down
        
        # Build and start the application
        docker-compose -f docker-compose.prod.yml up --build -d
        
        echo "✅ FinFlow is starting up in production mode..."
        echo "🌐 Access the application at: http://localhost:8081"
        echo ""
        echo "📝 To view logs: docker-compose -f docker-compose.prod.yml logs -f finflow-app"
        echo "🛑 To stop: docker-compose -f docker-compose.prod.yml down"
        ;;
        
    *)
        echo "❌ Invalid mode: $MODE"
        usage
        ;;
esac

echo ""
echo "🎉 Deployment completed successfully!" 