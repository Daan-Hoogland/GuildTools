# Guild Tools Discord Bot

## Docker image
```
docker build \
   -t hooglandio/guild_tools \
   --build-arg JAR_FIlE=target/{jar} \
   --no-cache .
```

### Env variables
#### SPRING_DATASOURCE_URL
Sets the datasource target for the application. Default: `localhost:5432/guild_tools`
#### SPRING_DATASOURCE_USERNAME
Sets the datasource username. Default: `postgres`
#### SPRING_DATASOURCE_PASSWORD
Sets the datasource password
#### PREFIX
Sets the prefix used for the application. Default: `!`
#### TOKENS_DISCORD
Sets the secret Discord token for the bot.
#### TOKENS_OWNER
Sets the owner ID
#### TOKENS_WARCRAFTLOGS
Sets the WarcraftLogs API public key
