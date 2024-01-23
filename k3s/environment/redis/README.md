# 如何在 k3s 上部署 redis

使用helm安装redis

1. 创建`values.yaml`文件, 自定义配置. 文件内容参考[官方配置](https://artifacthub.io/packages/helm/bitnami/redis).
2. `helm repo add bitnami https://charts.bitnami.com/bitnami`
3. `helm install my-redis bitnami/redis -f values.yaml`
