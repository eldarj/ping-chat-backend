#! /bin/bash

dbname="ping_db"
dbuser="ping"
dbpw="ping"

dl="\n-------------------------------------------------------------------------------------------\n"

printf $dl

echo "MYSQL -u root (no pass) - creating db="$dbname", user="$dbuser":"$dbpw"..."

mysql -u "root" -p << EOMYSQL
    create database if not exists $dbname;
    create user if not exists '$dbuser'@'%' identified by '$dbpw';
    grant all privileges on $dbname.* to '$dbuser'@'%';
    delete from mysql.user where user='' AND host='localhost';
    flush privileges;

EOMYSQL

printf "\nFinished! If there are no errors, everything is fine (:\nOtherwise make sure your mysql server is runnin with the "root" user (without a password)."
printf $dl
