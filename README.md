# mds-example-webhook
An example of how to receive push messages from Paxport MDS


# PREREQ - Install & Configure Local Database for dev/tests

* Install Docker & Docker Compose (for local db container)
* e.g https://docs.docker.com/docker-for-mac/
* run in terminal in project dir: *docker-compose up*


# To connect to local db running in docker container

* connect to container: *docker exec -i -t $(docker ps -q) /bin/bash*
* connect to db: *mysql -u mds -p*
* password: *mds*
* create db: *create database mds*