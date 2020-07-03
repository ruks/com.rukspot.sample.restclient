echo "ex: http://localhost:9090/inventory/customers/{code}"
#java -jar ./org.wso2.carbon.apimgt.demo-1.0-SNAPSHOT.jar

java -Dhome=$PWD \
-Dconfig=$PWD/deployment.yaml \
-Dresources=$PWD/data \
-jar com.rukspot.sample.restclient-1.0-SNAPSHOT.jar
#java -Dconfig=/Users/rukshan/wso2/apim/backend/deployment.yaml -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y -jar com.rukspot.sample.restclient-1.0-SNAPSHOT.jar
