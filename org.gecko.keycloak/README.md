# Keycloak Client Config File

The keycloak client configuration file can be set to use environmental variables as well.

The syntax is `${env.ENV_VARIABLE_NAME}`

Here is a complete example:

```json
{
  "realm": "MA",
  "auth-server-url" : "http://localhost:9080/auth",
  "resource" : "uma-client",
  "credentials": {
    "secret": "${env.CLIENT_SECRET}",
    "username": "${env.USERNAME}",
    "password": "${env.PASSWORD}"
  }
}
```

In MA the Keycloak config file can be provided like this:

```
"KeycloakAuthService": {
		"configurationFilePath": "$[env:MA_KEYCLOAK_CONFIG_FILE;default=$[prop:keycloak.config.file]]"
}
```

The filepath to the config json can be provided via environment variable **MA_KEYCLOAK_CONFIG_FILE**

Alternatively it also can be provided as Java system property **keycloak.config.file**
