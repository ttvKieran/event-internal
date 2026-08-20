#!/bin/bash

DEBEZIUM_HOST=${DEBEZIUM_HOST:-debezium}
DEBEZIUM_PORT=${DEBEZIUM_PORT:-8083}
CONNECTOR_NAME=${CONNECTOR_NAME:-iam-outbox-connector}
DB_HOST=${DB_HOST:-iam-db}
DB_PORT=${DB_PORT:-5432}
DB_USER=${DB_USER:-postgres}
DB_PASSWORD=${DB_PASSWORD:-postgres}
DB_NAME=${DB_NAME:-employee_db}
TOPIC_PREFIX=${TOPIC_PREFIX:-vtit}
TABLE_INCLUDE_LIST=${TABLE_INCLUDE_LIST:-public.outbox_events}
ROUTE_TOPIC_REPLACEMENT=${ROUTE_TOPIC_REPLACEMENT:-Iam}

DEBEZIUM_URL="http://${DEBEZIUM_HOST}:${DEBEZIUM_PORT}/connectors"

echo "Đợi Debezium khởi động tại ${DEBEZIUM_URL}..."
while [ $(curl -s -o /dev/null -w %{http_code} ${DEBEZIUM_URL}) -ne 200 ] ; do
  echo -e "\tĐang chờ..."
  sleep 5
done

STATUS_CODE=$(curl -s -o /dev/null -w %{http_code} ${DEBEZIUM_URL}/${CONNECTOR_NAME})

if [ $STATUS_CODE -eq 200 ]; then
  echo "Connector '${CONNECTOR_NAME}' đã tồn tại!"
  exit 0
fi

echo "Đăng ký Connector..."

JSON_PAYLOAD=$(cat <<EOF
{
  "name": "${CONNECTOR_NAME}",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "tasks.max": "1",
    "database.hostname": "${DB_HOST}",
    "database.port": "${DB_PORT}",
    "database.user": "${DB_USER}",
    "database.password": "${DB_PASSWORD}",
    "database.dbname": "${DB_NAME}",
    "topic.prefix": "${TOPIC_PREFIX}",
    "table.include.list": "${TABLE_INCLUDE_LIST}",
    "plugin.name": "pgoutput",
    "tombstones.on.delete": "false",
    "transforms": "outbox",
    "transforms.outbox.type": "io.debezium.transforms.outbox.EventRouter",
    "transforms.outbox.route.topic.replacement": "${ROUTE_TOPIC_REPLACEMENT}",
    "transforms.outbox.route.by.field": "aggregate_type",
    "transforms.outbox.table.field.event.id": "id",
    "transforms.outbox.table.field.event.key": "aggregate_id",
    "transforms.outbox.table.field.event.payload": "payload",
    "transforms.outbox.table.field.event.type": "type",
    "key.converter.schemas.enable": "false",
    "value.converter.schemas.enable": "false",
    "transforms.outbox.table.fields.additional.placement": "type:header:eventType, id:header:messageId, created_at:header:timestamp"
  }
}
EOF
)

curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
  ${DEBEZIUM_URL} -d "$JSON_PAYLOAD"

echo -e "\n Hoàn thành đăng ký Connector"
