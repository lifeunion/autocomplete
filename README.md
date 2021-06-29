# autocomplete

To run:
1. run DiscoveryApplication under "discovery" folder
2. check it is running on localhost:8761
3. run ApiGatewayApplication under "apigateway" folder
4. check under Eureka that apigateway is running (port 8080)
5. run PreprocessorApplication under "txtfileprocessor" folder
6. check under Eureka that preprocessor app is running (port 8901)
7. run RankingResource under "txtfileprocessor" folder
8. check under Eureka that ranking service is running (port 8803)
9. run AutoComplete under "autocomplete" folder
10. check under Eureka that autocomplete service is running (port 8801)
11. browse to, for example, http://localhost:8801/autocomplete/ke

you should expect to see (depending on what was put into topkeywords.txt under "txtfileprocessor" folder):
[kashi, kale, kefir, just egg, justins5]

(topkeywords.txt is emptied out in purpose. User will see empty results if the file is empty)

It is given that there are lots of features not working completely and edge cases not taken care of.

Time spent on this project was: 6 hours