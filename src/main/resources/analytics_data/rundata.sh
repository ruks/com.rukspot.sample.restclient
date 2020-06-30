curl -X GET "http://get" -H "Authorization: Bearer t" -H "X-Forwarded-For: 1.1.177.1" -k
curl -X POST "http://get" -H "Authorization: Bearer t" -H "X-Forwarded-For: 1.1.177.1" -d "String payload" -k
