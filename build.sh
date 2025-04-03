#!/bin/bash
# 打jar包
./gradlew buildTodoCoderJar
# 构建docker镜像
docker build -t huangxiaofenglogin/todocoder-gradle:v1.0.0 .
# 运行镜像
docker run --name=huangxiaofenglogin-gradle -d -p 8799:8799 huangxiaofenglogin/todocoder-gradle:v1.0.0
