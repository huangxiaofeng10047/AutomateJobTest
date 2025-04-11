#!/bin/bash
# 打jar包
./gradlew buildTodoCoderJar
# 构建docker镜像
docker build -t $DEVHARBOR/kuboard/todocoder-gradle:v1.0.0 .
docker push $DEVHARBOR/kuboard/todocoder-gradle:v1.0.0
# 运行镜像

cat <<EOF | kubectl apply -f -
# 创建命名空间
apiVersion: v1
kind: Namespace
metadata:
  name: todocoder
---
# deployment 构建
apiVersion: apps/v1
kind: Deployment
metadata:
  name: todocoder-gradle
  namespace: todocoder
spec:
  replicas: 1
  selector:
    matchLabels:
      app: todocoder-gradle
  template:
    metadata:
      labels:
        app: todocoder-gradle
    spec:
      containers:
        - image: www.harbor.mobi/kuboard/todocoder-gradle:v1.0.0
          imagePullPolicy: Always
          name: todocoder-gradle
          ports:
            - containerPort: 8799
              name: http
              protocol: TCP
          resources:
            limits:
              cpu: "2"
              memory: 4Gi
            requests:
              cpu: "1"
              memory: 2Gi
---
# service
apiVersion: v1
kind: Service
metadata:
  name: todocoder-gradle
  namespace: todocoder
spec:
  type: ClusterIP
  ports:
    - name: http
      port: 8799
      targetPort: 8799
  selector:
    app: todocoder-gradle
---
# node service
apiVersion: v1
kind: Service
metadata:
  name: todocoder-gradle-nodeport
  namespace: todocoder
spec:
  type: NodePort
  ports:
    - name: http
      port: 8799
      targetPort: 8799
      nodePort: 38799
  selector:
    app: todocoder-gradle
EOF
