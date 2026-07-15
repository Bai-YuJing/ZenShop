# 基于SpringBoot+Vue的商城系统

## 联系方式

zxw030406@outlook.com

1522993472@qq.com

## 后端

基于 Spring Boot + MyBatis-Plus 的电商平台，支持多商家入驻、商品管理、订单交易、评价评分等功能。

### 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.x |
| ORM | MyBatis-Plus | 3.5.7 |
| 数据库 | MySQL | 8.x |
| 缓存 | Redis | - |
| 接口文档 | Knife4j (Swagger) | - |
| 构建工具 | Maven | - |
| 云存储 | 华为云 OBS | - |
| 工具库 | Hutool | - |
| 数据库连接池 | HikariCP | - |

### 模块说明

| 模块 | 说明 |
|------|------|
| `common` | 公共工具类、常量、异常定义、云存储 OBS 工具 |
| `model` | 实体类、DTO、VO、枚举 |
| `server` | 业务层：Controller、Service、Mapper、配置 |

### 快速启动

#### 1. 数据库

执行 `sql/zenshop.sql` 初始化数据库表结构。

#### 2. 配置

修改 `server/src/main/resources/application.yml` 中的数据库和 Redis 连接信息。

#### 3. 编译运行

```bash
mvn clean install -DskipTests
mvn spring-boot:run -pl server
```

启动后访问：http://localhost:8888

### 接口列表

#### 用户端 (`/user/*`)

| 接口 | 方法 | 说明 |
|------|------|------|
| `/user/login` | POST | 用户登录 |
| `/user/register` | POST | 用户注册 |
| `/user/logout` | GET | 用户退出 |
| `/user/password` | PUT | 修改密码 |
| `/user/update` | PUT | 修改个人信息 |
| `/user/upload/avatar` | POST | 上传头像 |
| `/user/index/category` | GET | 获取商品分类 |
| `/user/index/top10` | GET | 销量TOP10商品 |
| `/user/business/detail/{id}` | GET | 商家详情（含分类） |
| `/user/business/page` | GET | 商家分页查询 |
| `/user/product/page` | POST | 商品分页查询 |
| `/user/product/{id}` | GET | 商品详情 |
| `/user/product/unrated` | GET | 未评价商品列表 |
| `/user/order/preview` | POST | 订单预览 |
| `/user/order/create` | POST | 创建订单 |
| `/user/order/pay` | POST | 支付（模拟） |
| `/user/order/list` | GET | 用户订单列表 |
| `/user/order/statusCounts` | GET | 各状态订单数量 |
| `/user/order/detail/{orderId}` | GET | 订单详情 |
| `/user/order/complete/{id}` | PUT | 确认收货 |
| `/user/order/cancel/{orderId}` | PUT | 取消订单 |
| `/user/order/unreviewed` | GET | 未评价订单列表 |
| `/user/order/rate` | POST | 评价订单商品 |
| `/user/order/ratings/{orderId}` | GET | 订单评价列表 |
| `/user/cart/list` | GET | 购物车列表 |
| `/user/cart/add` | POST | 添加到购物车 |
| `/user/cart/update` | PUT | 修改购物车数量 |
| `/user/cart/del/{productId}` | DELETE | 删除购物车商品 |
| `/user/cart/clear` | DELETE | 清空购物车 |
| `/user/address/list` | GET | 收货地址列表 |
| `/user/address/add` | POST | 新增收货地址 |
| `/user/address/update` | PUT | 修改收货地址 |
| `/user/address/del/{id}` | DELETE | 删除收货地址 |
| `/user/coupon/list` | GET | 优惠券列表 |
| `/user/coupon/claim/{id}` | POST | 领取优惠券 |

#### 商家端 (`/business/*`)

| 接口 | 方法 | 说明 |
|------|------|------|
| `/business/business/getCaptcha` | GET | 获取验证码 |
| `/business/business/getCategory` | GET | 获取分类 |
| `/business/business/register` | POST | 商家注册 |
| `/business/business/login` | POST | 商家登录 |
| `/business/shop/getStatus` | GET | 获取店铺状态 |
| `/business/shop/getShop` | GET | 获取店铺信息 |
| `/business/shop/getReason/{id}` | GET | 获取冻结/驳回原因 |
| `/business/shop/logo` | POST | 上传店铺Logo |
| `/business/shop/logout` | GET | 退出登录 |
| `/business/shop/open` | PUT | 开店 |
| `/business/shop/close` | PUT | 关店 |
| `/business/shop/review` | PUT | 提交入驻审核 |
| `/business/shop/updateBusiness` | PUT | 提交修改审核 |
| `/business/shop/deliver/{id}` | PUT | 商家发货 |
| `/business/shop/statistics/order` | GET | 每日订单统计 |
| `/business/shop/statistics/rating` | GET | 每日评价统计 |
| `/business/shop/orders/completed` | GET | 已完成订单分页 |
| `/business/shop/orders/unfinished` | GET | 未完成订单分页 |
| `/business/shop/order/deliver/{orderId}` | PUT | 按订单发货 |
| `/business/shop/order/reviewCancel/{orderId}` | PUT | 审核取消订单 |
| `/business/shop/order/cancelList` | GET | 取消订单列表分页 |
| `/business/shop/product/addProduct` | POST | 添加商品 |
| `/business/shop/product/page` | POST | 商品分页查询 |
| `/business/shop/product/update` | PUT | 更新商品信息 |
| `/business/shop/product/updateStatus/{id}` | PUT | 商品上下架 |
| `/business/shop/product/img` | POST | 上传商品图片 |
| `/business/shop/product/ratings` | POST | 商品评价列表 |
| `/business/shop/category/addCategory` | POST | 添加店铺分类 |
| `/business/shop/category/getAll` | GET | 获取店铺分类 |
| `/business/shop/category/updateCategory` | PUT | 更新店铺分类 |
| `/business/shop/category/updateStatus/{id}` | PUT | 修改分类状态 |

#### 管理端 (`/admin/*`)

| 接口 | 方法 | 说明 |
|------|------|------|
| `/admin/login` | POST | 管理员登录 |
| `/admin/login/getCaptcha` | GET | 获取验证码 |
| `/admin/addAdminUser` | POST | 添加管理员 |
| `/admin/user/page` | GET | 用户分页列表 |
| `/admin/user/{id}` | GET | 用户详情 |
| `/admin/user/status/{id}` | PATCH | 启用/禁用用户 |
| `/admin/user/{id}` | DELETE | 删除用户 |
| `/admin/user/pendingAvatar` | GET | 待审核头像列表 |
| `/admin/user/avatar/{id}` | PUT | 审核头像 |
| `/admin/user/ratings/{userId}` | GET | 用户评价列表 |
| `/admin/business/page` | POST | 商家分页查询 |
| `/admin/business/get` | GET | 商家详情 |
| `/admin/business/updateStatus/{id}` | PUT | 更新商家状态 |
| `/admin/business/updateBusiness` | PUT | 审核通过修改 |
| `/admin/business/getUpdateInfo` | GET | 获取修改信息 |
| `/admin/business/getReason/{id}` | GET | 获取审核原因 |
| `/admin/business/getCategory` | GET | 获取分类 |
| `/admin/coupon/page` | POST | 优惠券分页 |
| `/admin/coupon/add` | POST | 添加优惠券 |
| `/admin/coupon/update` | PUT | 修改优惠券 |
| `/admin/coupon/updateStatus/{id}` | PUT | 上下架优惠券 |
| `/admin/dashboard/statistics` | GET | 仪表盘统计 |

### 数据库设计

#### `user` — 用户表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| username | varchar(32) | 用户名 |
| password | varchar(64) | 密码(MD5) |
| phone | varchar(11) | 手机号 |
| sex | varchar(2) | 性别 |
| id_number | varchar(18) | 身份证号 |
| avatar | varchar(255) | 头像URL |
| avatar_status | tinyint | 头像审核状态 |
| status | tinyint | 状态 0=禁用 1=启用 |
| is_delete | tinyint | 逻辑删除 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### `business` — 商家表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(32) | 主键ID |
| name | varchar(64) | 店铺名称 |
| phone | varchar(11) | 手机号 |
| password | varchar(64) | 密码(MD5) |
| email | varchar(64) | 邮箱 |
| description | text | 店铺描述 |
| logo | varchar(255) | 店铺Logo |
| category_id | int | 店铺分类ID |
| status | tinyint | 状态 0=待提交 1=待审核 2=正常 3=冻结 4=已关闭 5=驳回 6=修改审核 |
| is_delete | tinyint | 逻辑删除 |
| last_login_time | datetime | 最后登录时间 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### `business_address` — 商家地址表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 主键ID |
| business_id | varchar(32) | 商家ID |
| province_id | int | 省份ID |
| city_id | int | 城市ID |
| district_id | int | 区县ID |
| detailed_address | varchar(255) | 详细地址 |

#### `business_review_reason` — 商家审核原因表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| business_id | varchar(32) | 商家ID |
| admin_id | bigint | 管理员ID |
| type | tinyint | 类型 1=驳回 2=冻结 |
| reason | int | 原因枚举值 |
| is_delete | tinyint | 逻辑删除 |
| create_time | datetime | 创建时间 |

#### `admin` — 管理员表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| username | varchar(32) | 用户名 |
| password | varchar(64) | 密码(MD5) |
| nickname | varchar(32) | 昵称 |
| role | tinyint | 角色 1=普通 2=超级管理员 |
| status | tinyint | 状态 |
| avatar | varchar(255) | 头像 |
| is_delete | tinyint | 逻辑删除 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### `category` — 商品分类表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 主键ID |
| name | varchar(32) | 分类名称 |
| pid | int | 父分类ID |
| sort | int | 排序 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### `product` — 商品表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| name | varchar(64) | 商品名称 |
| description | text | 商品描述 |
| price | decimal(10,2) | 单价 |
| stock | int | 库存 |
| sales | int | 销量 |
| status | tinyint | 状态 0=下架 1=上架 |
| business_id | varchar(32) | 商家ID |
| category_id | int | 分类ID |
| is_delete | tinyint | 逻辑删除 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### `product_image` — 商品图片表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| product_id | bigint | 商品ID |
| url | varchar(255) | 图片URL |
| sort | int | 排序 |

#### `product_category` — 店铺分类表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| name | varchar(64) | 分类名称 |
| status | tinyint | 状态 |
| business_id | varchar(32) | 商家ID |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### `orders` — 订单表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| order_no | varchar(32) | 订单编号 |
| user_id | bigint | 用户ID |
| business_id | bigint | 商家ID |
| user_address_id | int | 收货地址ID |
| total_amount | decimal(10,2) | 订单总金额 |
| actual_amount | decimal(10,2) | 实付金额 |
| status | tinyint | 状态 0=待付款 1=待发货 2=待收货 3=已完成 4=已取消 5=取消审核 6=取消失败 |
| remark | varchar(255) | 订单备注 |
| payment_method | tinyint | 支付方式 0=未支付 1=微信 2=支付宝 |
| payment_time | datetime | 支付时间 |
| delivery_time | datetime | 发货时间 |
| received_time | datetime | 收货时间 |
| is_delete | tinyint | 逻辑删除 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### `order_detail` — 订单详情表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| order_id | bigint | 订单ID |
| product_id | bigint | 商品ID |
| price | decimal(10,2) | 单价 |
| quantity | int | 数量 |

#### `order_cancel` — 订单取消记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| order_id | bigint | 订单ID |
| order_no | varchar(32) | 订单编号 |
| reason_type | tinyint | 取消原因类型 1=不想要了 2=地址填错 3=质量问题 4=其他 |
| reason | varchar(255) | 拒绝原因（商家填写） |
| status | tinyint | 状态 0=待审核 1=同意取消 2=拒绝取消 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### `business_rating` — 商品评价表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| business_id | varchar(32) | 商家ID |
| product_id | varchar(32) | 商品ID |
| user_id | varchar(32) | 用户ID |
| order_no | varchar(32) | 订单编号 |
| score | int | 评分 1-5 |
| content | varchar(500) | 评价内容 |
| is_delete | tinyint | 逻辑删除 |
| create_time | datetime | 创建时间 |

#### `coupon` — 优惠券表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| name | varchar(64) | 优惠券名称 |
| type | tinyint | 类型 1=满减 2=折扣 |
| total | int | 发行总量 |
| stock | int | 剩余库存 |
| status | tinyint | 状态 0=下架 1=上架 |
| is_delete | tinyint | 逻辑删除 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### `coupon_full_reduction` — 满减券规则表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| coupon_id | bigint | 优惠券ID |
| min_amount | decimal(10,2) | 最低消费金额 |
| discount_amount | decimal(10,2) | 减免金额 |

#### `coupon_discount` — 折扣券规则表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| coupon_id | bigint | 优惠券ID |
| discount_rate | decimal(3,2) | 折扣率 |
| max_discount_amount | decimal(10,2) | 最大减免金额 |

#### `user_coupon` — 用户优惠券关联表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| user_id | bigint | 用户ID |
| coupon_id | bigint | 优惠券ID |
| order_id | bigint | 使用订单ID |
| status | tinyint | 状态 0=未使用 1=已使用 |
| used_time | datetime | 使用时间 |
| create_time | datetime | 领取时间 |

#### `user_address` — 用户收货地址表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 主键ID |
| user_id | bigint | 用户ID |
| name | varchar(32) | 收货人姓名 |
| phone | varchar(11) | 联系电话 |
| province_id | int | 省份ID |
| city_id | int | 城市ID |
| district_id | int | 区县ID |
| address | varchar(255) | 详细地址 |
| is_default | tinyint | 是否默认 0=否 1=是 |
| is_delete | tinyint | 逻辑删除 |
| create_time | datetime | 创建时间 |

#### `user_default_address` — 用户默认地址表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 主键ID |
| user_id | bigint | 用户ID |
| address_id | int | 地址ID |

### 接口列表

#### 用户端 (`/user/*`)

| 接口 | 方法 | 说明 |
|------|------|------|
| `/user/login` | POST | 用户登录 |
| `/user/register` | POST | 用户注册 |
| `/user/index/category` | GET | 获取商品分类 |
| `/user/index/top10` | GET | 销量TOP10 |
| `/user/business/detail/{id}` | GET | 商家详情 |
| `/user/product/page` | POST | 商品分页查询 |
| `/user/product/{id}` | GET | 商品详情 |
| `/user/product/unrated` | GET | 未评价商品 |
| `/user/order/preview` | POST | 订单预览 |
| `/user/order/create` | POST | 创建订单 |
| `/user/order/pay` | POST | 支付（模拟） |
| `/user/order/list` | GET | 订单列表 |
| `/user/order/detail/{id}` | GET | 订单详情 |
| `/user/order/cancel/{id}` | PUT | 取消订单 |
| `/user/order/complete/{id}` | PUT | 确认收货 |
| `/user/order/unreviewed` | GET | 未评价订单 |
| `/user/order/rate` | POST | 评价订单 |
| `/user/order/ratings/{orderId}` | GET | 订单评价列表 |
| `/user/order/statusCounts` | GET | 各状态订单数 |
| `/user/address/list` | GET | 收货地址列表 |
| `/user/address/add` | POST | 新增地址 |
| `/user/address/update` | PUT | 修改地址 |
| `/user/address/del/{id}` | DELETE | 删除地址 |
| `/user/coupon/list` | GET | 优惠券列表 |
| `/user/coupon/claim/{id}` | POST | 领取优惠券 |

#### 商家端 (`/business/*`)

| 接口 | 方法 | 说明 |
|------|------|------|
| `/business/business/register` | POST | 商家注册 |
| `/business/business/login` | POST | 商家登录 |
| `/business/shop/getStatus` | GET | 获取店铺状态 |
| `/business/shop/getShop` | GET | 获取店铺信息 |
| `/business/shop/open` | PUT | 开店 |
| `/business/shop/close` | PUT | 关店 |
| `/business/shop/review` | PUT | 提交审核 |
| `/business/shop/updateBusiness` | PUT | 提交修改审核 |
| `/business/shop/logo` | POST | 上传Logo |
| `/business/shop/product/addProduct` | POST | 添加商品 |
| `/business/shop/product/page` | POST | 商品分页 |
| `/business/shop/product/update` | PUT | 更新商品 |
| `/business/shop/product/updateStatus/{id}` | PUT | 上下架 |
| `/business/shop/product/img` | POST | 上传商品图片 |
| `/business/shop/product/ratings` | POST | 商品评价列表 |
| `/business/shop/category/addCategory` | POST | 添加分类 |
| `/business/shop/category/getAll` | GET | 获取分类 |
| `/business/shop/category/updateCategory` | PUT | 更新分类 |
| `/business/shop/category/updateStatus/{id}` | PUT | 修改分类状态 |
| `/business/shop/orders/completed` | GET | 已完成订单 |
| `/business/shop/orders/unfinished` | GET | 未完成订单 |
| `/business/shop/order/deliver/{id}` | PUT | 发货 |
| `/business/shop/order/reviewCancel/{id}` | PUT | 审核取消订单 |
| `/business/shop/order/cancelList` | GET | 取消订单列表 |
| `/business/shop/statistics/order` | GET | 订单统计 |
| `/business/shop/statistics/rating` | GET | 评价统计 |

#### 管理端 (`/admin/*`)

| 接口 | 方法 | 说明 |
|------|------|------|
| `/admin/login` | POST | 管理员登录 |
| `/admin/login/getCaptcha` | GET | 获取验证码 |
| `/admin/user/page` | GET | 用户分页 |
| `/admin/user/{id}` | GET | 用户详情 |
| `/admin/user/status/{id}` | PATCH | 启用/禁用用户 |
| `/admin/user/{id}` | DELETE | 删除用户 |
| `/admin/business/page` | POST | 商家分页 |
| `/admin/business/get` | GET | 商家详情 |
| `/admin/business/updateStatus/{id}` | PUT | 更新商家状态 |
| `/admin/business/updateBusiness` | PUT | 审核通过修改 |
| `/admin/coupon/page` | POST | 优惠券分页 |
| `/admin/coupon/add` | POST | 添加优惠券 |
| `/admin/coupon/update` | PUT | 修改优惠券 |
| `/admin/coupon/updateStatus/{id}` | PUT | 上下架优惠券 |

### 数据库设计

#### 核心表

| 表名 | 说明 | 核心字段 |
|------|------|----------|
| `user` | 用户 | id, username, phone, password, avatar, status |
| `business` | 商家 | id, name, phone, status(0-6), logo, category_id |
| `business_address` | 商家地址 | business_id, province_id, city_id, district_id, address |
| `business_review_reason` | 审核原因 | business_id, admin_id, type(1驳回/2冻结), reason |
| `admin` | 管理员 | id, username, password, role, status |
| `category` | 商品分类 | id, name, pid, sort |
| `product` | 商品 | id, name, price, stock, sales, status, business_id, category_id |
| `product_image` | 商品图片 | product_id, url, sort |
| `product_category` | 店铺分类 | id, name, status, business_id |
| `orders` | 订单 | id, order_no, user_id, business_id, total_amount, actual_amount, status(0-6) |
| `order_detail` | 订单详情 | order_id, product_id, price, quantity |
| `order_cancel` | 取消记录 | order_id, order_no, reason_type, reason(拒绝原因), status(0待审/1同意/2拒绝) |
| `business_rating` | 评价 | order_no, product_id, user_id, score, content |
| `coupon` | 优惠券 | id, name, type(1满减/2折扣), total, status |
| `user_coupon` | 用户优惠券 | user_id, coupon_id, status(0未用/1已用) |
| `user_address` | 用户地址 | user_id, name, phone, province, city, district, address, is_default |

### 核心功能

#### 用户端
- 商品浏览、分类筛选、销量排行
- 购物车管理、下单结算
- 订单管理（查看、取消、确认收货）
- 商品评价评分
- 优惠券领取使用

#### 商家端
- 店铺管理（开/关店、信息修改审核）
- 商品管理（CRUD、上下架）
- 订单管理（发货、取消审核）
- 数据统计（订单、评价）
- 商品分类管理

#### 管理端
- 商家审核（入驻审核、冻结/驳回）
- 用户管理（启用/禁用）
- 优惠券管理
- 头像审核

### 订单状态

| 值 | 说明 |
|----|------|
| 0 | 待付款 |
| 1 | 待发货 |
| 2 | 待收货 |
| 3 | 已完成 |
| 4 | 已取消 |
| 5 | 取消审核 |
| 6 | 取消失败 |

### 取消订单流程

1. **未发货订单**（status 0/1）：用户直接取消，自动同意
2. **配送中订单**（status 2）：用户提交取消申请 → 订单变更为"取消审核"(5) → 商家审核同意/拒绝
3. **审核同意**：订单变更为"已取消"(4)
4. **审核拒绝**：订单变更为"取消失败"(6)，记录拒绝原因

### 商家状态

| 值 | 说明 |
|----|------|
| 0 | 待提交 |
| 1 | 待审核 |
| 2 | 正常 |
| 3 | 冻结 |
| 4 | 已关闭 |
| 5 | 审核未通过 |
| 6 | 修改审核 |

## 前端（管理端）

ZenShop 电商平台后台前端（管理端）项目，基于 Vue 3 + TypeScript + Element Plus 构建。

### 技术栈

- **框架**：Vue 3 + TypeScript
- **构建**：Vite 8
- **UI 组件**：Element Plus 2
- **状态管理**：Pinia
- **路由**：Vue Router 5
- **HTTP 请求**：Axios
- **图表**：ECharts 5

### 项目结构

```
src/
├── api/
│   └── index.js           # 所有接口定义（adminApi, userApi, businessApi, couponApi, statsApi）
├── router/
│   └── index.ts           # 路由配置（登录页/管理后台子路由）
├── utils/
│   └── request.js         # Axios 封装（请求/响应拦截器、token 注入）
├── views/
│   ├── AdminViews/
│   │   ├── Admin.vue          # 管理员管理
│   │   ├── AvatarReview.vue   # 头像审核
│   │   ├── Business.vue       # 商家管理
│   │   ├── Coupon.vue         # 优惠券管理
│   │   ├── Statistics.vue     # 统计报表
│   │   └── User.vue           # 用户管理
│   └── utilsView/
│       ├── AdminHome.vue      # 主页布局（侧边栏菜单 + 路由出口）
│       ├── AdminInfo.vue      # 个人信息
│       └── TopNav.vue         # 顶栏（用户头像、下拉菜单）
├── App.vue
├── main.ts                # 入口文件
└── style.css
```

### 页面介绍

#### 用户管理

展示前台注册用户列表，支持按用户名、手机号、状态筛选搜索。可对用户进行启用/禁用、删除操作。点击「查看评价」可查看该用户的所有历史评价，包括订单号、商品、评分、评价内容等信息，评价列表支持分页浏览。

#### 商家管理

管理平台入驻商家，支持按名称、分类、状态、省市区、评分区间等多维度筛选。表格中的评分列按分数段分色显示（0-2 红、2-4 橙、4-5 绿），并支持点击评分列头排序。点击「查看」可查看商家详情，根据商家状态可进行审核通过/驳回、冻结等操作，冻结和驳回需选择原因。状态为「修改审核」的商家有独立的审核流程。

#### 头像审核

展示用户待审核的新头像列表，管理员可逐条通过或驳回。列表固定返回最新待审核数据。

#### 优惠券管理

管理平台优惠券，支持满减和折扣两种类型。可新增、编辑、删除优惠券，设置名称、面值、发放总量、有效期等。列表支持按剩余数量排序。

#### 统计报表

分用户统计和商家统计两个标签页，通过 ECharts 图表展示数据趋势。

- **用户统计**：支持近7天/30天/本周/本月时间切换，展示用户总数、新增用户数、活跃用户数，折线图同时绘制用户总数、活跃用户和每日新增三条曲线。
- **商家统计**：支持相同的时间范围切换，展示商家总数、新增数量以及待审核/正常/冻结状态分布，包含趋势折线图和状态分布环形图。

#### 管理员管理

管理后台管理员账号，支持新增、编辑、删除。仅超级管理员可见此菜单。

#### 个人信息

当前登录管理员可修改头像、昵称、手机号、邮箱等信息。

### 快速开始

```bash
npm install
npm run dev        # 启动开发服务器 http://127.0.0.1:1234
npm run build      # 构建
```

## 前端（用户端）

ZenShop 电商平台前端（用户端）项目，基于 Vue 3 + TypeScript + Element Plus 构建。

### 技术栈

- **框架**：Vue 3 + TypeScript
- **构建**：Vite 8
- **UI 组件**：Element Plus 2
- **状态管理**：Pinia
- **路由**：Vue Router 5
- **HTTP 请求**：Axios
- **图表**：ECharts 5

### 项目结构

```
src/
├── api/
│   └── index.ts            # 所有接口定义（userApi、businessApi、orderApi、couponApi 等）
├── data/
│   └── region-data.ts      # 省市区级联数据
├── router/
│   └── index.ts            # 路由配置（用户端 + 商家端）
├── stores/
│   └── business.ts         # Pinia 商家状态
├── utils/
│   └── request.ts          # Axios 封装（请求/响应拦截器、token 注入）
├── views/
│   ├── Business/           # ── 商家端 ──
│   │   ├── BusinessLogin.vue      # 登录/注册
│   │   ├── BusinessCenter.vue     # 主页布局（顶栏 + 侧边栏 + 路由出口）
│   │   ├── utilView/
│   │   │   ├── TopBar.vue         # 顶栏（店铺名称、退出登录）
│   │   │   └── SideBar.vue        # 侧边栏菜单
│   │   ├── BusinessViews/
│   │   │   ├── Register.vue       # 提交审核
│   │   │   ├── Review.vue         # 审核状态
│   │   │   └── Thaw.vue           # 账号冻结/解冻
│   │   └── views/
│   │       ├── Profile.vue        # 个人信息
│   │       ├── EditProfile.vue    # 编辑资料
│   │       ├── EmployeeManage.vue # 员工管理
│   │       ├── CategoryManage.vue # 分类管理
│   │       ├── ProductManage.vue  # 商品管理
│   │       ├── ReviewManage.vue   # 商品评价
│   │       ├── OrderDone.vue      # 已完成订单
│   │       ├── OrderPending.vue   # 未完成订单
│   │       ├── CancelManage.vue   # 取消管理
│   │       └── Statistics.vue     # 统计报表
│   └── User/               # ── 用户端 ──
│       ├── index.vue           # 用户首页布局（底部导航 Tab）
│       ├── UserLogin.vue       # 登录/注册
│       └── views/
│           ├── Home.vue            # 首页
│           ├── Cart.vue            # 购物车
│           ├── Profile.vue         # 个人中心
│           ├── UserInfo.vue        # 个人信息编辑
│           ├── AddressManage.vue   # 地址管理
│           ├── ChangePassword.vue  # 修改密码
│           ├── SearchResult.vue    # 搜索结果
│           ├── ShopList.vue        # 商家列表
│           ├── BusinessDetail.vue  # 商家详情
│           ├── ProductDetail.vue   # 商品详情
│           ├── OrderList.vue       # 我的订单
│           ├── OrderDetail.vue     # 订单详情
│           ├── OrderConfirm.vue    # 确认订单
│           ├── Payment.vue         # 支付
│           ├── SubmitReview.vue    # 商品评价
│           └── CouponGrab.vue      # 领券中心
├── App.vue
└── main.ts
```

### 页面介绍

#### 商家端

**登录注册** — 商家通过手机号验证码登录或注册，登录后进入管理后台。

**审核流程** — 新注册商家需提交审核信息（名称、分类、地区、Logo 等），管理员审核通过后方可正常使用。审核驳回会展示驳回原因，修改审核状态可重新提交。

**账号冻结/解冻** — 账号被冻结时弹窗提示，可查看冻结原因并申请解冻。

**个人信息** — 展示店铺信息（名称、评分、分类、地区等），支持编辑资料、上传 Logo。店铺正常运营时可关店，关闭后可按需重新开店。

**商品管理** — 管理店铺商品，支持新增、编辑、删除、上架/下架。支持按分类/状态筛选、按销量/金额排序。商品可上传多张图片。

**分类管理** — 管理店铺商品分类，支持新增、编辑、上架/下架。分类数量上限 20 个，达到上限后添加按钮禁用。

**订单管理** — 分已完成订单和未完成订单两个页面。未完成订单支持发货操作；已完成订单支持按商品名称、评分范围、评价状态筛选，金额可排序。

**取消管理** — 展示用户申请取消的订单，可查看取消原因，选择同意取消或拒绝取消（需选择拒绝原因）。

**商品评价** — 按商品查看用户评价，支持按时间/评分排序切换。

**统计报表** — 通过 ECharts 折线图展示订单趋势（订单数 + 金额双轴）和评论趋势（好评/中评/差评三条曲线），支持近7天/30天/本周/本月时间切换。

#### 用户端

**登录注册** — 支持密码登录和验证码登录两种方式，注册需填写用户名、手机号、验证码、密码。

**首页** — 展示商家分类入口（10个分类，两排五列）和销量 TOP10 商品排行榜。搜索框支持输入关键词搜索商品。

**商品详情** — 展示商品图片轮播、商家信息（名称、Logo、评分）、价格、销量、库存、描述。底部有「加入购物车」和「立即购买」按钮。商品评价区域展示用户评价列表。

**搜索结果** — 分商品和商家两个 Tab。商品支持价格区间滑动筛选和按销量排序，搜索结果懒加载滚动翻页。

**购物车** — 按商家分组展示商品，支持选择商品结算、修改数量。数量减至 0 自动移除。

**确认订单** — 选择收货地址、优惠券，查看商品清单和实付金额。库存不足时自动返回购物车。

**支付** — 支持微信支付和支付宝两种方式。

**订单管理** — 按全部/待付款/待发货/待收货/已完成/待评价等状态分类展示，支持取消订单（选择取消原因）、确认收货。

**商品评价** — 已完成订单可对商品进行评分和评价，已评价的商品可查看评价内容。

**地址管理** — 管理收货地址，支持新增、编辑、删除、设为默认地址。地址数量上限 20 个。

**领券中心** — 展示可领取的优惠券，支持满减和折扣两种类型，点击领取。

**个人中心** — 展示头像和昵称，提供订单快捷入口（待付款、待收货）、个人信息编辑、地址管理、领券中心、修改密码等功能。

### 快速开始

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 类型检查
npm run type-check

# 构建
npm run build
```

### 订单状态

| 值   | 说明     |
| ---- | -------- |
| 0    | 待付款   |
| 1    | 待发货   |
| 2    | 待收货   |
| 3    | 已完成   |
| 4    | 已取消   |
| 5    | 取消审核 |
| 6    | 取消失败 |
