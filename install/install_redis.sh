#!/bin/bash

# Deploy a Redis Broker

#sysctl vm.overcommit_memory=1

cd /opt
if [ ! -L redis/conf ]; then
	echo 'Download and install Redis 2.8.9'
	apt-add-repository -y ppa:chris-lea/redis-server
	apt-get update
	apt-get install -y redis-server
	mkdir -p /opt/redis
	ln -sf /etc/redis /opt/redis/conf
	cp -f /vagrant/broker/redis/redis/redis.conf /etc/redis/redis.conf
	cp -f /etc/redis/redis.conf /opt/redis
        echo "Installing python modules"
	apt-get install python-pip
        pip install redis requests simplejson

	echo "redis-server stop"
	service redis-server stop
fi

echo "Starting Redis"

IPADDR=`bash /vagrant/utils/getip.sh`
PORT=$1
MAXMEMORY=$2
TYPE=$3

cd /opt/redis

if [ $TYPE = "master" ]; then
    echo "Configuring Redis Server (MASTER) @$IPADDR:$PORT"
    sed "s/port 6379/port $PORT/" redis.conf > conf/redis$PORT.conf
    #sed -i "s/stop-writes-on-bgsave-error yes/stop-writes-on-bgsave-error no/" conf/redis$PORT.conf
    sed -i "s/dump.rdb/dump$PORT.rdb/" conf/redis$PORT.conf
    sed -i "s/pidfile \/var\/run\/redis.pid/pidfile \/var\/run\/redis$PORT.pid/" conf/redis$PORT.conf
    sed -i "s/# maxmemory <bytes>/maxmemory $MAXMEMORY/" conf/redis$PORT.conf
    sed -i "s/# maxmemory-policy volatile-lru/maxmemory-policy volatile-ttl/" conf/redis$PORT.conf
    /usr/bin/redis-server conf/redis$PORT.conf 1> /var/log/cloudsight/redis$PORT.log 2> /var/log/cloudsight/redis$PORT.log &
    echo "$IPADDR:$PORT" >> /opt/redis_cluster_m
    echo "$IPADDR:$PORT:None" >> /opt/nutcracker_cluster
    ## wait until all Redis instances start up
    bash /vagrant/utils/testports.sh /opt/redis_cluster_m
elif [ $TYPE = "slaveof" ]; then
    echo "Configuring Redis Server (SLAVE) @$IPADDR:$PORT (slaveof $4)"
    MASTER_IP=`echo $4 | cut -d: -f1`
    MASTER_PORT=`echo $4 | cut -d: -f2`
    sed "s/port 6379/port $PORT/" redis.conf > conf/redis$PORT.conf
    #sed -i "s/stop-writes-on-bgsave-error yes/stop-writes-on-bgsave-error no/" conf/redis$PORT.conf
    sed -i "s/dump.rdb/dump$PORT.rdb/" conf/redis$PORT.conf
    sed -i "s/pidfile \/var\/run\/redis.pid/pidfile \/var\/run\/redis$PORT.pid/" conf/redis$PORT.conf
    sed -i "s/# slaveof <masterip> <masterport>/slaveof $MASTER_IP $MASTER_PORT/" conf/redis$PORT.conf
    /usr/bin/redis-server conf/redis$PORT.conf 1> /var/log/cloudsight/redis$PORT.log 2> /var/log/cloudsight/redis$PORT.log &
    echo "$IPADDR:$PORT" >> /opt/redis_cluster_s
    echo "$IPADDR:$PORT:None" >> /opt/nutcracker_cluster
    ## wait until all Redis instances start up
    bash /vagrant/utils/testports.sh /opt/redis_cluster_s
else
    echo "invalid redis server type: $TYPE"
    exit -1
fi

