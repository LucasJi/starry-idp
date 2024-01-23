# Starry

## Introduction

Starry

## Install postgres using docker

```shell
docker pull postgres
docker run --name postgresql -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -v /data:/root/data/postgresql -d postgres
```
