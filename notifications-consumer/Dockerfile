FROM python:3.13-slim
WORKDIR /app

RUN groupadd -r app && useradd -r -g app app

COPY notifications-consumer/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY notifications-consumer/consumer.py .

USER app
ENTRYPOINT ["python", "-u", "consumer.py"]