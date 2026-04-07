#!/bin/bash

# --- CONFIGURACIÓN DE VARIABLES ---
CONTAINER_NAME="order-db-postgres"
DB_USER="admin"
DB_NAME="order-db"

echo "⏳ Conectando a $CONTAINER_NAME para limpiar tablas..."

# Ejecutamos el comando de forma no interactiva (sin -it) para que el script fluya
docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "
  DROP TABLE IF EXISTS order_line_items CASCADE;
  DROP TABLE IF EXISTS orders CASCADE;
"

if [ $? -eq 0 ]; then
    echo "✅ Tablas eliminadas con éxito (o no existían)."
else
    echo "❌ Hubo un error al intentar borrar las tablas."
fi