# 图片分享平台 - 后端

## 技术栈
- Spring Boot 3.2.0
- Spring Security
- Spring Data MongoDB
- JWT
- Azure Blob Storage

## 配置要求

### MongoDB
确保MongoDB已安装并运行在 `localhost:27017`

### Azure Storage
在 `application.yml` 中配置Azure存储连接字符串和容器名称：
- `azure.storage.connection-string`: Azure存储连接字符串
- `azure.storage.container-name`: 容器名称

或者通过环境变量设置：
- `AZURE_STORAGE_CONNECTION_STRING`
- `AZURE_STORAGE_CONTAINER_NAME`

### JWT Secret
在 `application.yml` 中配置JWT密钥，或通过环境变量 `JWT_SECRET` 设置。

## 运行
```bash
mvn spring-boot:run
```

或者先构建再运行：
```bash
mvn clean package
java -jar target/imageshare-backend-1.0.0.jar
```

## API端点

### 认证
- `POST /api/auth/register` - 注册
- `POST /api/auth/login` - 登录

### 图片管理
- `GET /api/images` - 获取所有图片（公开）
- `GET /api/images/my` - 获取我的图片（需要认证）
- `POST /api/images/upload` - 上传图片（需要认证）
- `GET /api/images/{id}/download` - 下载图片（公开）
- `PUT /api/images/{id}` - 修改图片名称（需要认证）
- `DELETE /api/images/{id}` - 删除图片（需要认证）




