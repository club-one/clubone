docker build -f ./training-container -t training-database .
docker run --name training-database -p 3306:3306 -e MYSQL_ROOT_PASSWORD=damager training-database
docker run -it training-database bash

docker stop training-database && docker rm training-database

Database details:
mysql -h maria-db-1.ciqht1uo2oca.us-east-1.rds.amazonaws.com -u admin -pDq57AXegdlQcXtw0H4lA
# port 3306
