# Light Bikes

## Run the application locally
1. `./gradlew bootRun`

## Deploy the application
1. Load your inseng credentials
   `vaulted shell <inseng>`
1. Log in to Starlord (https://starlord.inscloudgate.net/)
   `docker login -u <username> -p ... starlord.inscloudgate.net`
1. Build (tag should be the sha of master)
   `cg build -e edge -t <tag>`
1. Deploy
   `cg deploy -e edge -t <tag> -u <username>`

## Deploy the application after CloudGate upgrade
1. Provision the application
   `cg provision -e edge`
