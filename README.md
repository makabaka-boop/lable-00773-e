# 金蝶财务管理系统

一个仿照金蝶的财务软件，采用 Spring Boot + MyBatis + Vue 技术栈开发。

## How to Run

### 使用 Docker Compose（推荐）

确保已安装 Docker 和 Docker Compose。

```bash
# 构建并启动所有服务（支持 ARM 和 X86 架构）
docker-compose up --build -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend

# 停止服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v
```

启动后访问：
- 前端：http://localhost:8081
- 后端 API：http://localhost:8080

**注意**：首次启动可能需要几分钟时间进行构建和初始化。

### 本地开发

#### 后端启动

```bash
cd backend
./mvnw spring-boot:run
```

后端服务将在 http://localhost:8080 启动

#### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

## Services

### Backend Service
- **端口**: 8080
- **技术栈**: Spring Boot 3.2.0 + MyBatis 3.0.3 + H2 Database
- **健康检查**: http://localhost:8080/api/auth/current
- **数据存储**: Docker volume `backend-data`

### Frontend Service
- **端口**: 8081 (映射到容器内 80)
- **技术栈**: Vue 3.4 + Element Plus + Vite
- **Web服务器**: Nginx
- **代理**: `/api` 请求自动代理到后端

## 测试账号

### 管理员账号
- **用户名**: `admin`
- **密码**: `admin123`
- **权限**: 超级管理员，拥有所有功能权限

### 普通用户账号
- **用户名**: `user`
- **密码**: `user123`
- **权限**: 普通用户，可进行日常凭证操作

## 题目内容

我需要仿造金蝶写一个财务软件，后端用springboot和mybatis，前端用vue，你需要写接口文档

## 功能模块

- **会计科目管理**: 科目的增删改查，支持多级科目
- **凭证管理**: 凭证录入、编辑、过账、作废
- **账簿查询**: 明细账、科目余额表

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- MyBatis 3.0.3
- H2 Database (开发环境)

### 前端
- Vue 3.4
- Vue Router 4
- Element Plus 2.4
- Axios
- Vite 5

## 快速开始

### 1. 启动后端

```bash
cd backend
./mvnw spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

## 项目结构

```
├── backend/                    # 后端项目
│   ├── src/main/java/com/finance/
│   │   ├── FinanceApplication.java
│   │   ├── common/            # 通用类
│   │   ├── controller/        # 控制器
│   │   ├── entity/            # 实体类
│   │   ├── mapper/            # MyBatis Mapper
│   │   └── service/           # 服务层
│   └── src/main/resources/
│       ├── application.yml    # 配置文件
│       ├── schema.sql         # 建表语句
│       ├── data.sql           # 初始数据
│       └── mapper/            # Mapper XML
│
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── router/            # 路由
│   │   ├── views/             # 页面组件
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── API.md                      # 接口文档
└── README.md
```

## 数据库设计

### 概述
本系统使用 H2 数据库（开发环境），支持切换到 MySQL（生产环境）。

### 表结构

#### 0. sys_user - 用户表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(255) | 密码（SHA-256加密） |
| name | VARCHAR(100) | 姓名 |
| role | VARCHAR(50) | 角色：ADMIN/USER |
| is_enabled | BOOLEAN | 是否启用 |
| create_time | TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 更新时间 |

#### 1. account - 会计科目表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| code | VARCHAR(20) | 科目编码（唯一） |
| name | VARCHAR(100) | 科目名称 |
| parent_code | VARCHAR(20) | 父级科目编码 |
| level | INT | 科目级次 |
| direction | VARCHAR(10) | 余额方向：DEBIT/CREDIT |
| is_enabled | BOOLEAN | 是否启用 |
| create_time | TIMESTAMP | 创建时间 |

#### 2. voucher - 凭证主表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| voucher_no | VARCHAR(30) | 凭证号（唯一） |
| voucher_date | DATE | 凭证日期 |
| period | VARCHAR(7) | 会计期间 yyyy-MM |
| attachment_count | INT | 附件张数 |
| status | VARCHAR(20) | 状态：DRAFT/POSTED/VOID |
| preparer | VARCHAR(50) | 制单人 |
| reviewer | VARCHAR(50) | 审核人 |
| create_time | TIMESTAMP | 创建时间 |
| update_time | TIMESTAMP | 更新时间 |

#### 3. voucher_entry - 凭证分录表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| voucher_id | BIGINT | 凭证ID（外键） |
| account_code | VARCHAR(20) | 科目编码 |
| account_name | VARCHAR(100) | 科目名称 |
| summary | VARCHAR(200) | 摘要 |
| debit_amount | DECIMAL(18,2) | 借方金额 |
| credit_amount | DECIMAL(18,2) | 贷方金额 |
| seq | INT | 序号 |

#### 4. account_balance - 科目余额表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| account_code | VARCHAR(20) | 科目编码 |
| period | VARCHAR(7) | 会计期间 |
| opening_debit | DECIMAL(18,2) | 期初借方 |
| opening_credit | DECIMAL(18,2) | 期初贷方 |
| current_debit | DECIMAL(18,2) | 本期借方 |
| current_credit | DECIMAL(18,2) | 本期贷方 |
| closing_debit | DECIMAL(18,2) | 期末借方 |
| closing_credit | DECIMAL(18,2) | 期末贷方 |

### ER 关系图
```
account 1 ──── N voucher_entry
    │
    └──── N account_balance

voucher 1 ──── N voucher_entry
```

### 数据库访问

#### H2 数据库（开发环境）
项目默认使用 H2 内存数据库，数据存储在 `backend/data/` 目录下。

访问 H2 控制台: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/finance`
- 用户名: `sa`
- 密码: (空)

#### 切换到 MySQL（生产环境）
修改 `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/finance?useSSL=false&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password
```

### 索引设计
- `idx_voucher_period` - 凭证期间索引，加速按期间查询
- `idx_voucher_status` - 凭证状态索引
- `idx_voucher_date` - 凭证日期索引
- `idx_entry_voucher_id` - 分录凭证ID索引
- `idx_entry_account_code` - 分录科目编码索引
- `idx_balance_period` - 余额期间索引
- `idx_balance_account` - 余额科目索引
- `idx_account_parent` - 科目父级索引

### 安全措施
1. MyBatis 使用 `#{}` 参数绑定，防止 SQL 注入
2. 密码使用 BCrypt 加密存储
3. 敏感操作记录日志

## 生产环境部署

### 1. 构建后端
```bash
cd backend
./mvnw clean package -DskipTests
# 生成 target/finance-system-1.0.0.jar
```

### 2. 构建前端
```bash
cd frontend
npm run build
# 生成 dist 目录
```

### 3. 部署配置

#### 后端配置 (application-prod.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/finance?useSSL=false&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    
logging:
  level:
    com.finance: INFO
  file:
    name: /var/log/finance/app.log
```

#### 启动命令
```bash
java -jar finance-system-1.0.0.jar --spring.profiles.active=prod
```

### 4. Nginx 配置
```nginx
server {
    listen 80;
    server_name finance.example.com;

    # 前端静态文件
    location / {
        root /var/www/finance/dist;
        try_files $uri $uri/ /index.html;
    }

    # API 代理
    location /api {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 环境变量
| 变量 | 说明 | 默认值 |
|------|------|--------|
| DB_USERNAME | 数据库用户名 | sa |
| DB_PASSWORD | 数据库密码 | - |
| SERVER_PORT | 服务端口 | 8080 |

### 健康检查
```bash
curl http://localhost:8080/api/auth/current
```

### 日志位置
- 开发环境：控制台输出
- 生产环境：/var/log/finance/app.log

## 预置科目

系统预置了标准企业会计科目，包括：
- 资产类：库存现金、银行存款、应收账款、固定资产等
- 负债类：短期借款、应付账款、应交税费等
- 所有者权益类：实收资本、本年利润等
- 成本类：生产成本、制造费用等
- 损益类：主营业务收入、管理费用等

## License

MIT
