# mds-example-webhook

<a href="https://travis-ci.org/paxport/mds-example-webhook" target="_blank"><img src="https://api.travis-ci.org/paxport/mds-example-webhook.svg?branch=master"/></a>


An example of how to receive push messages from Paxport MDS

### What will this webhook do?

This repo can setup a local MySQL instance and contains code to listen on a port (8181) and
receive incoming HTTP POST messages to url /orders that contain JSON representations of Orders being 
made inside Paxport MDS. The included example webhook will process each incoming order,
pull out the supplier transactions and each transaction item and persist it to the local
MySQL Database. Each request checks that the incoming request uri contains a shared secret like ?token=sharedsecr37

### Get up and running with a demo system in 5 minutes

* Clone this repo
* Install [Docker](https://www.docker.com/products/docker) locally (for local db container)
* cd into the *demo* directory and run: *docker-compose up*
* Now you have a running mysql db and a server listening on localhost:8181
* Punch a hole in your firewall to forward traffic to the local host on port 8181
* You could set up a system like https://ngrok.com or Ultrahook to forward traffic through your firewall instead
* Ask someone at Paxport to set up a forwarding push receiver for your Agent
and give them the publically addressable URL for the new service 
* The full url will be something like https://mdswebhook.yourdomain.com/orders?token=oursharedsecr37
* Once MDS is configured on the Paxport side then any Orders made for your agent will get forwarded out and should arrive at this webhook and the transaction and items will get persisted to the local mysql server.


### Example Flow

1. Customer has booked an Easyjet Flight via the Agencies website and receives an email informing them that they can choose seats on the flight
2. Custom clicks on link and through the PaxShop backed website and allocates the seats and pays for the booking amendment
3. Paxport MDS posts the corresponding Order out to the Agencies webhook which is listening at: https://mdswebhook.bongotravel.com/orders?token=oursharedsecr37
4. You can see an example [Order JSON Message here](example-order.md)
5. The SparkServer will receive the incoming request and check that the request uri contains token=oursharedsecr37 (You can change the secret in the docker-compose.yml but you will need to make sure paxport has the same token in the URL that they register)
6. Orders will get passed to the *PersistTransactionsHandler* which will parse the JSON, pull out the supplier transaction and child items
7. Each supplier transaction will result in an insert or update of the *mds.supplier_transactions* table
8. Each transaction item will result in an insert or update of the *mds.transaction_items* table
9. You can see the structure of the database tables at the bottom of this page

## Dev/Test

### Inital Setup of Local Database

* Clone this repo
* Install Docker Locally (for local db container)
* Run in terminal in project dir: *docker-compose up*

### Build Using Maven

```
mvn clean install
```

### Build Docker Image containing server

```
mvn clean package docker:build
```

### Push to docker hub

```
docker push paxportmds/mds-example-webhook
```


### To connect to local db running in docker container

* connect to container: *docker exec -i -t $(docker ps -q) /bin/bash*
* connect to db: *mysql -u mds -p*
* password: *mds*

```
$ docker ps
CONTAINER ID        IMAGE                                   COMMAND                  CREATED             STATUS              PORTS                    NAMES
cce66cdc0f70        paxportmds/mds-example-webhook:latest   "java -jar /mds-examp"   24 seconds ago      Up 22 seconds       0.0.0.0:8181->8181/tcp   mds_webhook_demo_server
ca622d27de71        mariadb:10.1.14                         "docker-entrypoint.sh"   2 hours ago         Up 23 seconds       0.0.0.0:3306->3306/tcp   mds_webhook_demo_mysql
$ docker exec -it ca622d27de71 /bin/bash
root@ca622d27de71:/# mysql -u mds -p                                                                                                                                                          
Enter password: 
Welcome to the MariaDB monitor.  Commands end with ; or \g.
Your MariaDB connection id is 13
Server version: 10.1.14-MariaDB-1~jessie mariadb.org binary distribution

MariaDB [(none)]> use mds;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
MariaDB [mds]> show tables;
+-----------------------+
| Tables_in_mds         |
+-----------------------+
| schema_level          |
| supplier_transactions |
| transaction_items     |
+-----------------------+
3 rows in set (0.00 sec)

MariaDB [mds]> describe supplier_transactions;
+----------------------------+---------------+------+-----+-------------------+-------+
| Field                      | Type          | Null | Key | Default           | Extra |
+----------------------------+---------------+------+-----+-------------------+-------+
| txn_id                     | varchar(24)   | NO   | PRI | NULL              |       |
| created_at                 | datetime      | NO   |     | CURRENT_TIMESTAMP |       |
| target                     | varchar(12)   | NO   |     | NULL              |       |
| agent_id                   | varchar(45)   | YES  |     | NULL              |       |
| supplier                   | varchar(45)   | NO   |     | NULL              |       |
| system                     | varchar(45)   | NO   |     | NULL              |       |
| type                       | varchar(24)   | NO   |     | NULL              |       |
| status                     | varchar(24)   | NO   |     | NULL              |       |
| most_relevant_date         | date          | NO   |     | NULL              |       |
| cost_amount                | decimal(19,4) | NO   |     | NULL              |       |
| cost_currency              | varchar(3)    | NO   |     | NULL              |       |
| order_id                   | varchar(45)   | NO   |     | NULL              |       |
| booking_reference          | varchar(255)  | YES  |     | NULL              |       |
| pax_name                   | varchar(255)  | YES  |     | NULL              |       |
| updated_at                 | datetime      | YES  |     | NULL              |       |
| failure_reason             | varchar(1024) | YES  |     | NULL              |       |
| price_amount               | decimal(19,4) | YES  |     | NULL              |       |
| price_currency             | varchar(3)    | YES  |     | NULL              |       |
| request_id                 | varchar(45)   | YES  |     | NULL              |       |
| tracing_id                 | varchar(45)   | YES  |     | NULL              |       |
| logical_session_id         | varchar(128)  | YES  |     | NULL              |       |
| external_payment_reference | varchar(255)  | YES  |     | NULL              |       |
+----------------------------+---------------+------+-----+-------------------+-------+

MariaDB [mds]> describe transaction_items;    
+-----------------------+---------------+------+-----+---------+-------+
| Field                 | Type          | Null | Key | Default | Extra |
+-----------------------+---------------+------+-----+---------+-------+
| item_id               | varchar(24)   | NO   | PRI | NULL    |       |
| txn_id                | varchar(24)   | NO   | MUL | NULL    |       |
| service               | varchar(128)  | NO   |     | NULL    |       |
| relevant_date         | date          | NO   |     | NULL    |       |
| description           | varchar(255)  | NO   |     | NULL    |       |
| base_cost_amount      | decimal(19,4) | NO   |     | NULL    |       |
| cost_amount_with_fees | decimal(19,4) | NO   |     | NULL    |       |
| cost_currency         | varchar(3)    | NO   |     | NULL    |       |
| price_amount          | decimal(19,4) | YES  |     | NULL    |       |
| price_currency        | varchar(3)    | YES  |     | NULL    |       |
| idx                   | int(11)       | YES  |     | 99      |       |
+-----------------------+---------------+------+-----+---------+-------+
```


