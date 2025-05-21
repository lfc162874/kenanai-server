# KenanAI 微服务平台技术文档

## 1. 系统概述

KenanAI是一个基于Spring Boot和Spring Cloud的微服务架构系统，主要提供AI聊天服务以及相关功能。系统采用模块化设计，包含以下主要服务：

- **网关服务 (kenanai-gateway)**: 统一入口，负责路由和请求转发
- **认证服务 (kenanai-auth)**: 处理用户登录和认证
- **用户服务 (kenanai-user)**: 管理用户数据和账户操作
- **聊天服务 (kenanai-chat)**: 提供AI聊天功能
- **公共模块 (kenanai-common)**: 提供各服务共用的工具和实体类

## 2. 技术栈

- **JDK**: 17
- **框架**: Spring Boot 3.0.5, Spring Cloud 2022.0.1
- **数据库**: MySQL 8.0
- **ORM**: MyBatis Plus 3.5.3.1
- **认证**: JWT (JSON Web Token)
- **其他工具**: Lombok, Hutool

## 3. 系统架构

### 3.1 整体架构图

```
┌─────────────┐
│    客户端    │
└──────┬──────┘
       │
┌──────▼──────┐
│  网关服务   │ 统一入口、请求路由、认证过滤
└──────┬──────┘
       │
       ├─────────────┬─────────────┐
       │             │             │
┌──────▼──────┐┌─────▼─────┐┌──────▼──────┐
│  认证服务   ││  用户服务  ││  聊天服务   │
└─────────────┘└─────┬─────┘└──────┬──────┘
                     │             │
              ┌──────▼──────┐      │
              │   数据库    │◄─────┘
              └─────────────┘
```

### 3.2 服务功能说明

#### 网关服务 (kenanai-gateway)
- 提供统一的API入口
- 基于路径的请求路由
- 请求过滤与权限验证
- 解决跨域问题

#### 认证服务 (kenanai-auth)
- 用户登录认证
- 生成JWT令牌
- 验证令牌有效性
- 与用户服务交互获取用户信息

#### 用户服务 (kenanai-user)
- 用户注册、账户管理
- 用户资料更新
- 余额查询与管理
- API密钥管理

#### 聊天服务 (kenanai-chat)
- 提供AI聊天功能
- 连接OpenAI或其他AI接口
- 记录聊天历史
- 统计token用量

#### 公共模块 (kenanai-common)
- 提供共用的工具类
- 定义通用常量
- 提供统一响应对象
- 提供基础数据模型

## 4. 数据库设计

系统使用MySQL数据库，主要包含以下表：

### 4.1 用户表 (sys_user)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 主键ID |
| username | varchar | 用户名 |
| password | varchar | 密码(加密存储) |
| nickname | varchar | 昵称 |
| phone | varchar | 手机号 |
| email | varchar | 邮箱 |
| avatar | varchar | 头像URL |
| dept_id | bigint | 部门ID |
| balance | decimal | 账户余额 |
| api_key | varchar | API密钥 |
| status | tinyint | 状态(0-正常,1-锁定) |
| last_login_time | datetime | 最后登录时间 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| deleted | tinyint | 是否删除(0-未删除,1-已删除) |

### 4.2 聊天记录表 (chat_record)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 主键ID |
| user_id | bigint | 用户ID |
| message | text | 用户消息 |
| response | text | 系统响应 |
| model | varchar | 模型名称 |
| tokens | int | 消耗的token数量 |
| create_time | datetime | 创建时间 |

### 4.3 充值记录表 (recharge_record)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | bigint | 主键ID |
| user_id | bigint | 用户ID |
| amount | decimal | 充值金额 |
| payment_method | varchar | 支付方式 |
| transaction_id | varchar | 交易ID |
| status | tinyint | 状态(0-未支付,1-已支付,2-已取消) |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

## 5. 接口说明

### 5.1 认证接口

#### 5.1.1 用户登录

- **请求URL**: `/auth/login`
- **请求方式**: POST
- **请求参数**:
  ```json
  {
    "username": "用户名",
    "password": "密码"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 0,
    "msg": "操作成功",
    "data": {
      "userId": 1,
      "username": "admin",
      "nickname": "系统管理员",
      "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
      "expiresIn": 86400
    }
  }
  ```

#### 5.1.2 Token验证

- **请求URL**: `/auth/validate`
- **请求方式**: GET
- **请求头**: Authorization: Bearer {token}
- **响应示例**:
  ```json
  {
    "code": 0,
    "msg": "操作成功",
    "data": true
  }
  ```

### 5.2 用户接口

#### 5.2.1 获取用户信息

- **请求URL**: `/user/info`
- **请求方式**: GET
- **请求头**: Authorization: Bearer {token}
- **响应示例**:
  ```json
  {
    "code": 0,
    "msg": "操作成功",
    "data": {
      "id": 1,
      "username": "admin",
      "nickname": "系统管理员",
      "phone": "13800138000",
      "email": "admin@example.com",
      "avatar": "https://example.com/avatar.jpg",
      "balance": 100.00,
      "lastLoginTime": "2023-07-01 12:00:00",
      "createTime": "2023-01-01 00:00:00"
    }
  }
  ```

#### 5.2.2 更新用户资料

- **请求URL**: `/user/profile`
- **请求方式**: PUT
- **请求头**: Authorization: Bearer {token}
- **请求参数**:
  ```json
  {
    "nickname": "新昵称",
    "phone": "13900139000",
    "email": "new@example.com",
    "avatar": "https://example.com/new-avatar.jpg"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 0,
    "msg": "操作成功",
    "data": {
      "id": 1,
      "username": "admin",
      "nickname": "新昵称",
      "phone": "13900139000",
      "email": "new@example.com",
      "avatar": "https://example.com/new-avatar.jpg",
      "balance": 100.00,
      "lastLoginTime": "2023-07-01 12:00:00",
      "createTime": "2023-01-01 00:00:00"
    }
  }
  ```

#### 5.2.3 修改密码

- **请求URL**: `/user/password`
- **请求方式**: PUT
- **请求头**: Authorization: Bearer {token}
- **请求参数**:
  ```json
  {
    "oldPassword": "旧密码",
    "newPassword": "新密码"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 0,
    "msg": "密码修改成功",
    "data": true
  }
  ```

#### 5.2.4 获取用户余额

- **请求URL**: `/user/balance`
- **请求方式**: GET
- **请求头**: Authorization: Bearer {token}
- **响应示例**:
  ```json
  {
    "code": 0,
    "msg": "操作成功",
    "data": 100.00
  }
  ```

#### 5.2.5 重置API密钥

- **请求URL**: `/user/reset-api-key`
- **请求方式**: PUT
- **请求头**: Authorization: Bearer {token}
- **响应示例**:
  ```json
  {
    "code": 0,
    "msg": "API密钥重置成功",
    "data": "kn_a1b2c3d4e5f6g7h8i9j0"
  }
  ```

### 5.3 聊天接口

#### 5.3.1 发送消息

- **请求URL**: `/chat`
- **请求方式**: POST
- **请求头**: Authorization: Bearer {token}
- **请求参数**:
  ```json
  {
    "message": "你好，请介绍一下你自己",
    "model": "gpt-3.5-turbo"
  }
  ```
- **响应示例**:
  ```json
  {
    "code": 0,
    "msg": "操作成功",
    "data": {
      "response": "你好！我是KenanAI，一个基于人工智能的聊天助手...",
      "model": "gpt-3.5-turbo"
    }
  }
  ```

#### 5.3.2 获取聊天历史

- **请求URL**: `/chat/history`
- **请求方式**: GET
- **请求头**: Authorization: Bearer {token}
- **响应示例**:
  ```json
  {
    "code": 0,
    "msg": "操作成功",
    "data": {
      "records": [
        {
          "id": 1,
          "message": "你好，请介绍一下你自己",
          "response": "你好！我是KenanAI，一个基于人工智能的聊天助手...",
          "model": "gpt-3.5-turbo",
          "tokens": 150,
          "createTime": "2023-07-01 15:30:00"
        }
      ]
    }
  }
  ```

## 6. 认证流程

系统使用JWT (JSON Web Token) 进行身份认证，认证流程如下：

1. 用户通过`/auth/login`接口提交用户名和密码
2. 认证服务验证用户名和密码
3. 验证通过后，生成JWT令牌，包含用户ID和用户名信息
4. 返回JWT令牌给客户端
5. 客户端存储令牌，后续请求在请求头中携带令牌
6. 网关服务验证令牌的有效性
7. 验证通过后，将用户信息传递给后续的微服务

## 7. 系统部署

### 7.1 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+
- Redis 6.0+ (可选，用于缓存)

### 7.2 部署步骤

1. **准备数据库**
   - 创建MySQL数据库: `kenanai`
   - 执行初始化SQL脚本: `doc/sql/init.sql`

2. **配置文件**
   - 修改各服务的`application.yml`文件，配置数据库连接信息
   - 根据实际情况修改服务端口、JWT密钥等配置

3. **编译打包**
   ```bash
   cd kenanai-server
   mvn clean package -DskipTests
   ```

4. **启动服务**
   按以下顺序启动服务:
   ```bash
   # 启动用户服务
   java -jar kenanai-user/target/kenanai-user-1.0-SNAPSHOT.jar
   
   # 启动认证服务
   java -jar kenanai-auth/target/kenanai-auth-1.0-SNAPSHOT.jar
   
   # 启动聊天服务
   java -jar kenanai-chat/target/kenanai-chat-1.0-SNAPSHOT.jar
   
   # 启动网关服务
   java -jar kenanai-gateway/target/kenanai-gateway-1.0-SNAPSHOT.jar
   ```

5. **验证部署**
   - 访问: `http://localhost:9999/auth/login` 测试登录接口
   - 默认管理员账号: `admin` / 密码: `admin123`

### 7.3 Docker部署 (可选)

系统提供了Docker部署支持，详细步骤如下:
1. 构建Docker镜像
2. 使用Docker Compose启动所有服务
3. 配置网络和数据卷

## 8. 开发指南

### 8.1 开发环境搭建

1. 克隆项目代码
2. 导入IDE (推荐IntelliJ IDEA)
3. 使用Maven安装依赖
4. 启动本地MySQL和Redis服务
5. 运行各个服务的主类进行调试

### 8.2 代码规范

- 遵循阿里巴巴Java开发手册规范
- 使用Lombok简化代码
- 统一使用R对象封装响应数据
- 使用MyBatis Plus简化数据库操作
- 统一异常处理

### 8.3 扩展新服务

1. 在根目录创建新的服务模块
2. 配置pom.xml，添加必要依赖
3. 创建启动类、配置文件
4. 实现服务功能
5. 在网关服务中添加路由配置

## 9. 系统安全

### 9.1 密码安全

- 密码使用BCrypt算法加密存储
- 密码传输过程中使用HTTPS保护
- 密码策略：最少6位，包含数字和字母

### 9.2 API安全

- 使用JWT进行身份认证
- 令牌有效期控制
- 敏感API限制访问频率
- API密钥用于第三方集成

### 9.3 数据安全

- 数据库敏感字段加密存储
- 定期数据备份
- 数据访问权限控制

## 10. 扩展与维护

### 10.1 性能优化

- 引入Redis缓存热点数据
- 数据库索引优化
- JVM参数调优

### 10.2 监控与告警

- 接入Prometheus监控系统性能
- 配置Grafana可视化监控数据
- 设置关键指标告警

### 10.3 日志管理

- 使用ELK (Elasticsearch, Logstash, Kibana) 统一日志管理
- 按服务和级别分类日志
- 关键操作审计日志

## 附录

### A. 错误码说明

| 错误码 | 描述 |
|-------|------|
| 0 | 成功 |
| 1 | 一般错误 |
| 1001 | 参数验证失败 |
| 1002 | 用户名或密码错误 |
| 1003 | 无效的令牌 |
| 1004 | 余额不足 |
| 1005 | 请求过于频繁 |

### B. 常见问题解答

1. **Q: 服务启动失败怎么办？**
   A: 检查配置文件、数据库连接、端口占用情况。

2. **Q: 如何扩展新的AI模型？**
   A: 在聊天服务中添加新的模型接口适配器。

3. **Q: 如何提高系统并发能力？**
   A: 增加服务实例、引入负载均衡、优化数据库访问。

### C. 更新日志

**v1.0.0 (2023-07-15)**
- 初始版本发布
- 基本用户管理功能
- AI聊天功能
- JWT认证

---

© 2023 KenanAI. All Rights Reserved. 