# playlist-microservice
This is the playlist microservice application.

The playlist microservice is responsible for returning all playlists from the database.

In order to use the playlist microservice container, we will need to configure the following environment variables:
- ENVIRONMENT = DEBUG (For now the only option).
- REDIS_HOST = IP address or domain name of Redis database. In our case, we will use Azure Cache for Redis.
- REDIS_PORT = In our case, we will use 6379 (NON TLS PORT for Redis).
- PASSWORD = Redis database password.
- VIDEOS_API_HOST = Video microservice IP or hostname.
- VIDEOS_API_PORT = Video microservice port.
- JAEGER_ENDPOINT = JAEGER endpoint (For traces).

This microservice will expose metrics at the port 8000.

We have defined some custom metrics for better observability like: 
- Number of playlists. 
- Number of videos.
- Number of videos per playlist.

We expose the port 10010 for serving our web application. 

We can get playlists by browsing /{id} routes. 

Additionally, we can take a look at the health of the application by checking /healthz route.