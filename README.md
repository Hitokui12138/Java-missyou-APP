# Java-missyou-APP
基于 Java 技术栈开发的移动端后端应用，提供完整的商品、用户、订单、购物车等核心电商业务接口，适配移动端 APP 交互场景。

## 项目介绍
Java-missyou-APP 是一套面向移动端的电商后端系统，采用分层架构设计，覆盖电商核心业务流程：用户注册登录、商品分类与检索、购物车管理、订单生成与支付回调等。项目聚焦于移动端接口的设计与实现，适配高并发、高可用的移动端业务场景，是学习 Java 电商后端开发的优质实践项目。

### 核心特性
- 完整的用户体系：支持手机号验证码登录、JWT 令牌认证、用户信息管理
- 商品模块：分类树形结构、商品规格/sku、搜索与筛选
- 购物车：实时计算价格、选中状态管理、合并购物车
- 订单模块：订单生成、状态流转、支付回调处理
- 接口规范：RESTful 风格设计，统一响应格式，异常全局处理

## 技术栈
### 后端核心
- 开发语言：Java 8+
- 框架：Spring Boot、Spring MVC、MyBatis/MyBatis-Plus
- 数据库：MySQL（支持读写分离/分库分表扩展）
- 认证：JWT（JSON Web Token）
- 构建工具：Maven/Gradle
- 其他：Redis（缓存）、Lombok（简化代码）、Swagger（接口文档）、FastJSON（JSON 处理）

### 可选扩展
- 消息队列：RabbitMQ（订单异步处理、短信发送）
- 分布式锁：Redisson
- 支付对接：微信/支付宝支付 SDK

## 快速开始
### 环境要求
- JDK 8 或更高版本
- MySQL 5.7+/8.0+
- Redis 5.0+（可选，缓存功能依赖）
- Maven 3.6+ 或 Gradle 7.0+

### 部署步骤
1. **克隆项目**
```bash
git clone https://github.com/Hitokui12138/Java-missyou-APP.git
cd Java-missyou-APP
```

2. **配置数据库**
- 创建 MySQL 数据库（示例：`missyou_app`）
- 修改 `application.yml`（或 `application.properties`）中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/missyou_app?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_mysql_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  # Redis 配置（可选）
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
    database: 0
```

3. **初始化数据**
- 执行项目 `sql` 目录下的初始化脚本（如 `missyou_init.sql`），创建表结构和基础数据（商品分类、默认配置等）。

4. **构建并启动**
```bash
# Maven
mvn clean package -DskipTests
java -jar target/missyou-app-1.0.0.jar

# Gradle（若使用）
gradle clean build
java -jar build/libs/missyou-app-1.0.0.jar
```

5. **访问接口文档**
启动后访问 `http://localhost:8080/swagger-ui.html`（Swagger 地址，需确认配置），查看所有接口定义。

## 目录结构
```
Java-missyou-APP/
├── src/main/java/com/hitokui/missyou/
│   ├── controller/       # 接口层：用户、商品、订单等控制器
│   ├── service/          # 业务层：核心业务逻辑实现
│   ├── mapper/           # 数据层：MyBatis Mapper 接口
│   ├── model/            # 数据模型：实体类、DTO、VO
│   ├── config/           # 配置类：Redis、MyBatis、Swagger 等
│   ├── exception/        # 异常处理：全局异常拦截、自定义异常
│   ├── util/             # 工具类：JWT、加密、日期处理等
│   └── MissyouApplication.java  # 启动类
├── src/main/resources/
│   ├── application.yml   # 全局配置
│   ├── mybatis/          # MyBatis 映射文件
│   └── sql/              # 数据库初始化脚本
└── pom.xml               # Maven 依赖
```

## 核心接口示例
| 接口路径                | 方法 | 描述               |
|-------------------------|------|--------------------|
| `/api/user/login`       | POST | 用户手机号登录     |
| `/api/goods/list`       | GET  | 商品列表查询       |
| `/api/cart/add`         | POST | 添加商品到购物车   |
| `/api/order/create`     | POST | 生成订单           |

## 开发与贡献
1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/xxx`)
3. 提交代码 (`git commit -am 'add xxx feature'`)
4. 推送到分支 (`git push origin feature/xxx`)
5. 发起 Pull Request

### 开发规范
- 代码遵循阿里巴巴 Java 开发手册
- 接口返回统一格式：`{ "code": 200, "msg": "success", "data": {} }`
- 异常需捕获并转换为统一的错误码返回

## 常见问题
1. **启动报错：数据库连接失败**  
   检查 `application.yml` 中的数据库地址、用户名、密码是否正确，确保 MySQL 服务已启动。
2. **Redis 相关报错**  
   若不需要缓存功能，可注释掉 Redis 相关配置；若需要，确保 Redis 服务正常运行且配置正确。
3. **接口返回 401**  
   检查请求头是否携带 JWT Token，Token 是否过期，认证逻辑是否正确。

## 许可证
本项目基于 MIT 许可证开源，详情见 [LICENSE](LICENSE) 文件（若项目未提供，可补充：`本项目仅供学习使用，商用请联系作者`）。

## 致谢
- 感谢 Spring Boot、MyBatis 等开源框架提供的技术支撑
- 感谢电商业务场景相关的开源案例与技术文档

---
**注**：以上 README 基于项目命名及通用电商后端场景梳理，若项目有特殊业务/技术细节（如仅实现了部分核心功能、有专属的业务逻辑等），可补充说明：
- 若需调整“核心特性/接口示例”，可提供具体的业务功能/接口路径，我会进一步精准修正；
- 若目录结构与实际不符，也可补充项目真实目录，我会同步调整。
