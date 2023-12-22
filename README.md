# playlist-microservice
This is the playlist microservice application.

The playlist microservice is responsble of returning all the playlists from the database.

In order to use the playlist-microservice container mainly we will need to configure the folowing environment variables :
- ENVIRONMENT = DEBUG (For now the only option)
- REDIS_HOST = Ip address or domain name of Redis database. In our case we will use Azure cache Redis
- REDIS_PORT = in our case we will use 6379 (NON TLS PORT dor Redis)
- PASSWORD = Redis database password
- VIDEOS_API_HOST = Video microservice ip or hostname
- VIDEOS_API_PORT = Video microservice port
- JAEGER_ENDPOINT = JAEKER Enpoint (For traces)
This microservice will expose metrics at the port 8000


We have defined some custom metrics for better observability like 
* Number of playlists 
* Number of videos
* Number of videos per playlist

We expose the port 10010 for serving our web application. You can get playlists by browsing /{id} routes. Additionally you can tage a look athe health of the application by check /healthz route


The following repository is inspired by this amazing work [link](https://github.com/kubees/playlist-microservice/) 