#!/bin/bash

echo "Creating SQS queue: groupsapp-events"

awslocal sqs create-queue \
  --queue-name groupsapp-events \
  --region us-east-1

echo "Queue created. List of queues:"
awslocal sqs list-queues --region us-east-1