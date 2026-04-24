"""
Notifications Consumer — lee eventos de message.created desde SQS
y simula el envío de notificaciones push a los destinatarios.

Patrón: MOM consumer con acknowledgment explícito.
  - Lee con long polling (menor uso de recursos).
  - Procesa y borra solo si no hubo error.
  - Si hay error, deja el mensaje para reintento automático de SQS.
"""

import json
import logging
import os
import signal
import sys
import time

import boto3
from botocore.config import Config
from botocore.exceptions import ClientError


# ─── Configuración por variables de entorno ──────────────────────
SQS_ENDPOINT  = os.getenv("SQS_ENDPOINT","")
SQS_QUEUE_URL = os.getenv("SQS_QUEUE_URL", "http://localstack:4566/000000000000/groupsapp-events")
AWS_REGION    = os.getenv("AWS_REGION", "us-east-1")
WAIT_TIME_SEC = int(os.getenv("SQS_WAIT_TIME_SECONDS", "10"))
MAX_MESSAGES  = int(os.getenv("SQS_MAX_MESSAGES", "10"))


# ─── Logging ─────────────────────────────────────────────────────
logging.basicConfig(
    level=os.getenv("LOG_LEVEL", "INFO"),
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
)
log = logging.getLogger("notifications-consumer")


# ─── Flag de control para shutdown limpio ────────────────────────
running = True

def handle_shutdown(signum, frame):
    """Permite que SIGINT/SIGTERM paren el loop de forma limpia."""
    global running
    log.info("Received signal %s, shutting down gracefully...", signum)
    running = False

signal.signal(signal.SIGINT, handle_shutdown)
signal.signal(signal.SIGTERM, handle_shutdown)


# ─── Cliente SQS ─────────────────────────────────────────────────
def create_sqs_client():
    """Crea el cliente SQS. Endpoint se ajusta según ambiente."""
    config = Config(
        region_name=AWS_REGION,
        retries={"max_attempts": 3, "mode": "adaptive"},
    )

    kwargs = {"config": config}

    # Si hay endpoint custom (LocalStack), lo usamos.
    # En AWS real, se omite y el SDK usa el endpoint oficial.
    if SQS_ENDPOINT:
        kwargs["endpoint_url"] = SQS_ENDPOINT

    return boto3.client("sqs", **kwargs)


# ─── Lógica de procesamiento ─────────────────────────────────────
def process_event(event: dict):
    """
    Procesa un evento del dominio. En producción real, esto enviaría
    una notificación push vía Firebase/APNS. Aquí simulamos con logging.

    El consumer es IDEMPOTENTE: procesar dos veces el mismo evento
    no causa daño (solo logs duplicados).
    """
    event_type   = event.get("eventType")
    message_id   = event.get("messageId")
    sender_id    = event.get("senderId")
    sender_name  = event.get("senderUsername")
    receiver_id  = event.get("receiverId")
    channel_id   = event.get("channelId")
    preview      = event.get("contentPreview")

    # Filtramos por tipo de evento; si no es el que nos interesa, ignoramos.
    if event_type != "message.created":
        log.debug("Ignoring event of type=%s", event_type)
        return

    if channel_id is not None:
        log.info(
            "📢 [PUSH SIMULATION] User %s (%d) posted in channel %d: '%s' (msg #%d)",
            sender_name, sender_id, channel_id, preview, message_id,
        )
    elif receiver_id is not None:
        log.info(
            "📢 [PUSH SIMULATION] User %s (%d) sent DM to user %d: '%s' (msg #%d)",
            sender_name, sender_id, receiver_id, preview, message_id,
        )
    else:
        log.warning("Event has no channelId nor receiverId: %s", event)


# ─── Loop principal ──────────────────────────────────────────────
def poll_loop(sqs_client):
    """Loop de long polling. Se detiene cuando running=False."""
    log.info("Starting poll loop. Queue=%s, wait=%ds, maxMsgs=%d",
             SQS_QUEUE_URL, WAIT_TIME_SEC, MAX_MESSAGES)

    while running:
        try:
            response = sqs_client.receive_message(
                QueueUrl=SQS_QUEUE_URL,
                MaxNumberOfMessages=MAX_MESSAGES,
                WaitTimeSeconds=WAIT_TIME_SEC,
                MessageAttributeNames=["All"],
            )
        except ClientError as e:
            log.error("Error polling SQS: %s. Retrying in 5s.", e)
            time.sleep(5)
            continue

        messages = response.get("Messages", [])

        if not messages:
            log.debug("No messages received in this poll.")
            continue

        for msg in messages:
            handle_message(sqs_client, msg)


def handle_message(sqs_client, msg):
    """Procesa un mensaje individual y lo borra si tuvo éxito."""
    body = msg.get("Body", "")
    receipt_handle = msg.get("ReceiptHandle")
    message_id = msg.get("MessageId")

    try:
        event = json.loads(body)
    except json.JSONDecodeError as e:
        log.error("Invalid JSON in message %s: %s", message_id, e)
        # Borramos para no reprocesar un mensaje que nunca parseará.
        delete_message(sqs_client, receipt_handle, message_id)
        return

    try:
        process_event(event)
    except Exception as e:
        # No borramos el mensaje; SQS lo volverá a entregar tras el visibility timeout.
        log.exception("Error processing event %s: %s", message_id, e)
        return

    delete_message(sqs_client, receipt_handle, message_id)


def delete_message(sqs_client, receipt_handle, message_id):
    """Elimina un mensaje de la cola (ack)."""
    try:
        sqs_client.delete_message(
            QueueUrl=SQS_QUEUE_URL,
            ReceiptHandle=receipt_handle,
        )
        log.debug("Message %s deleted (acknowledged)", message_id)
    except ClientError as e:
        log.error("Failed to delete message %s: %s", message_id, e)


# ─── Entry point ─────────────────────────────────────────────────
def main():
    log.info("═══════════════════════════════════════════════")
    log.info("  Notifications Consumer — starting up")
    log.info("  Endpoint: %s", SQS_ENDPOINT or "(AWS default)")
    log.info("  Queue URL: %s", SQS_QUEUE_URL)
    log.info("  Region:   %s", AWS_REGION)
    log.info("═══════════════════════════════════════════════")

    sqs = create_sqs_client()
    try:
        poll_loop(sqs)
    finally:
        log.info("Consumer stopped cleanly.")


if __name__ == "__main__":
    main()
    sys.exit(0)