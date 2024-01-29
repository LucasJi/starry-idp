# IDP

## How to generate JKS using KeyTool

`keytool -export -alias ${alias} -keystore ${keystore}.jks -file ${filename}.cer`

Example: `keytool -export -alias starry -keystore starry.jks -file pub.cer`

## Build docker image

`docker build --platform linux/amd64 --rm -t starry-idp:latest .`

## Push to image registry

## Run image

`docker run starry-idp -d -p 8000:8080`

## K3S pull latest image

`crictl pull registry.cn-shanghai.aliyuncs.com/lucasji/starry-idp:latest`

## PROD env variable config

Use k8s secret to store sensitive configs such as database password. When deploying the application
to the prod environment, k8s reads the config from the secret and pass it to the
Dockerfile as arguments through Development.yml.

The entire path:  
secret -> deployment -> Dockerfile -> application.yml
