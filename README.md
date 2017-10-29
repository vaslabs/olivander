# Olivander
An akka application to deliver customer shopping data really really fast.

## Steps to get it working
```bash
export AWS_CBOR_DISABLE=1
```
Start the needed mocked aws services
```bash
docker-compose up
```
Create the two kinesis streams
```bash
aws --region="us-west-1" --endpoint-url=http://localhost:4568 kinesis create-stream --stream-name olivander-order-stream --shard-count 1
aws --region="us-west-1" --endpoint-url=http://localhost:4568 kinesis create-stream --stream-name dunhumby-orders-stream --shard-count 1
```
and
Start the 3 main apps under live streaming

DunhabbyMain needs to be started given a path as a parameter of a json file with flattened data.
Unzip one of the files in test resources for an easy start.
