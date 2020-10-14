```
mvn clean package
httpClient.setConnectTimeout(200, TimeUnit.SECONDS);
httpClient.setReadTimeout(200, TimeUnit.SECONDS);
```

1. Change "resourcePath" in the deployment.yaml pointing to the "data" dir absolute path
2. Change "amHome" in the deployment.yaml pointing to the APIM HOME dir absolute path
3. execute $ ./run.sh to generate APIs and traffic
4. rundata.sh file generated in "resourcePath" with curl command to invoke same APIs. Since same access token is used, make sure to increase the password grant type validity period to a higher value.
    ```
    [oauth]
    token_validation.app_access_token_validity = 864000
    ```
5. To execute rundata.sh backend should be up and running. For that execute ./backend.sh to start backend services
