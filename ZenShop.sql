/*
 Navicat Premium Data Transfer

 Source Server         : 1.14.94.234
 Source Server Type    : MySQL
 Source Server Version : 50743
 Source Host           : 1.14.94.234:3306
 Source Schema         : ZenShop

 Target Server Type    : MySQL
 Target Server Version : 50743
 File Encoding         : 65001

 Date: 15/07/2026 21:17:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示名称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `role` tinyint(4) NOT NULL DEFAULT 1 COMMENT '角色: 1=普通管理员, 2=超级管理员',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态: 0=禁用, 1=启用',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `is_delete` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否删除: 1=未删, 0=已删',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '管理员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '超级管理员', '13488992233', '123@qq.com', 'https://zxw-web-framework.obs.cn-north-4.myhuaweicloud.com/ZenShop/nulld8ea1e3e-16f9-4d3d-91e4-ed59e208f8c1.png', 2, 1, '2026-07-15 21:05:59', 1, '2026-07-15 17:49:23', '2026-07-15 21:06:13');

-- ----------------------------
-- Table structure for business
-- ----------------------------
DROP TABLE IF EXISTS `business`;
CREATE TABLE `business`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键ID（雪花算法）',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '店铺名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '店铺简介',
  `logo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'logo URL',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `category_id` int(11) NULL DEFAULT NULL COMMENT '店铺分类ID',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态: 0=待提交, 1=待审核, 2=正常, 3=冻结, 4=已关闭, 5=审核未通过, 6=修改审核',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '上次登录时间',
  `is_delete` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否删除: 1=未删, 0=已删',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商家表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of business
-- ----------------------------

-- ----------------------------
-- Table structure for business_address
-- ----------------------------
DROP TABLE IF EXISTS `business_address`;
CREATE TABLE `business_address`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `business_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商家ID',
  `province_id` int(11) NULL DEFAULT NULL COMMENT '省份ID',
  `city_id` int(11) NULL DEFAULT NULL COMMENT '城市ID',
  `district_id` int(11) NULL DEFAULT NULL COMMENT '区县ID',
  `detailed_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '详细地址',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_business_id`(`business_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商家地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of business_address
-- ----------------------------

-- ----------------------------
-- Table structure for business_rating
-- ----------------------------
DROP TABLE IF EXISTS `business_rating`;
CREATE TABLE `business_rating`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `business_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商家ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `score` tinyint(4) NOT NULL COMMENT '评分 1-5',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评价内容',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  `order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_business_id`(`business_id`) USING BTREE,
  INDEX `idx_product_id`(`product_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商家评分表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of business_rating
-- ----------------------------

-- ----------------------------
-- Table structure for business_review_reason
-- ----------------------------
DROP TABLE IF EXISTS `business_review_reason`;
CREATE TABLE `business_review_reason`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `business_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商家ID',
  `admin_id` bigint(20) NOT NULL COMMENT '管理员ID',
  `type` tinyint(4) NOT NULL COMMENT '类型: 1=驳回, 2=冻结',
  `reason` int(11) NOT NULL COMMENT '原因（对应枚举value）',
  `is_delete` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否删除: 1=未删, 0=已删',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_business_id`(`business_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商家审核原因记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of business_review_reason
-- ----------------------------

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '餐饮美食');
INSERT INTO `category` VALUES (2, '服装鞋帽');
INSERT INTO `category` VALUES (3, '数码电器');
INSERT INTO `category` VALUES (4, '美妆护肤');
INSERT INTO `category` VALUES (5, '家居生活');
INSERT INTO `category` VALUES (6, '母婴用品');
INSERT INTO `category` VALUES (7, '运动户外');
INSERT INTO `category` VALUES (8, '图书文具');
INSERT INTO `category` VALUES (9, '珠宝首饰');
INSERT INTO `category` VALUES (10, '生鲜食品');

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type` tinyint(4) NOT NULL COMMENT '类型: 1=满减券, 2=折扣券',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '优惠券名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `valid_from` datetime NOT NULL COMMENT '有效期开始',
  `valid_to` datetime NOT NULL COMMENT '有效期结束',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态: 0=下架, 1=上架',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `total` int(11) NULL DEFAULT NULL COMMENT '数量',
  `is_delete` tinyint(4) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '优惠券总表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of coupon
-- ----------------------------
INSERT INTO `coupon` VALUES (1, 1, '测试', '测试测试测试测试', '2026-07-15 00:00:00', '2026-08-21 00:00:00', 1, '2026-07-15 21:01:22', '2026-07-15 21:01:22', 10, NULL);

-- ----------------------------
-- Table structure for coupon_discount
-- ----------------------------
DROP TABLE IF EXISTS `coupon_discount`;
CREATE TABLE `coupon_discount`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券ID',
  `discount_rate` decimal(3, 2) NOT NULL COMMENT '折扣率（如0.80=8折）',
  `max_discount_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '最高减免金额（null=无上限）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coupon_id`(`coupon_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '折扣券详情表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of coupon_discount
-- ----------------------------

-- ----------------------------
-- Table structure for coupon_full_reduction
-- ----------------------------
DROP TABLE IF EXISTS `coupon_full_reduction`;
CREATE TABLE `coupon_full_reduction`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券ID',
  `min_amount` decimal(10, 2) NOT NULL COMMENT '满X元',
  `discount_amount` decimal(10, 2) NOT NULL COMMENT '减Y元',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coupon_id`(`coupon_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '满减券详情表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of coupon_full_reduction
-- ----------------------------
INSERT INTO `coupon_full_reduction` VALUES (1, 1, 101.00, 100.00);

-- ----------------------------
-- Table structure for order_cancel
-- ----------------------------
DROP TABLE IF EXISTS `order_cancel`;
CREATE TABLE `order_cancel`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单编号',
  `reason_type` tinyint(4) NULL DEFAULT NULL COMMENT '取消原因类型: 1=不想要了, 2=地址填错, 3=商品质量问题, 4=其他',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态: 0=待审核, 1=同意取消, 2=拒绝取消',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单取消表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_cancel
-- ----------------------------

-- ----------------------------
-- Table structure for order_detail
-- ----------------------------
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `price` decimal(10, 2) NOT NULL COMMENT '下单时单价',
  `quantity` int(11) NOT NULL COMMENT '购买数量',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单详情表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_detail
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `business_id` bigint(20) NOT NULL COMMENT '商家ID',
  `user_address_id` int(11) NOT NULL COMMENT '收货地址ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `actual_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '订单状态: 0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消5=取消审核 6=取消失败',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '订单备注',
  `payment_method` tinyint(4) NULL DEFAULT 0 COMMENT '支付方式: 0=未支付, 1=微信, 2=支付宝',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `received_time` datetime NULL DEFAULT NULL COMMENT '收货时间',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否删除: 1=未删, 0=已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_business_id`(`business_id`) USING BTREE,
  INDEX `idx_order_no`(`order_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `business_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商家ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '分类ID',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `stock` int(11) NOT NULL DEFAULT 0 COMMENT '库存',
  `sales` int(11) NOT NULL DEFAULT 0 COMMENT '销量',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态: 0=下架, 1=上架',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_delete` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否删除: 1=未删, 0=已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_business_id`(`business_id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product
-- ----------------------------

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `sort` int(11) NULL DEFAULT 0 COMMENT '排序',
  `business_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商家ID',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否上架: 1=上架, 0=下架',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_business_id`(`business_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_category
-- ----------------------------

-- ----------------------------
-- Table structure for product_image
-- ----------------------------
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `business_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商家ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '图片URL',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品图片表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_image
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态: 0=禁用, 1=启用',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `is_delete` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否删除: 1=未删, 0=已删',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `province_id` int(11) NULL DEFAULT NULL COMMENT '省份ID',
  `city_id` int(11) NULL DEFAULT NULL COMMENT '城市ID',
  `district_id` int(11) NULL DEFAULT NULL COMMENT '区县ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货人姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货人手机号',
  `detailed_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '详细地址',
  `is_delete` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否删除: 1=未删, 0=已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_address
-- ----------------------------

-- ----------------------------
-- Table structure for user_coupon
-- ----------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券ID',
  `order_id` bigint(20) NULL DEFAULT NULL COMMENT '订单ID（使用后记录）',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态: 0=未使用, 1=已使用, 2=已过期',
  `used_time` datetime NULL DEFAULT NULL COMMENT '使用时间',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_coupon_id`(`coupon_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户优惠券表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_coupon
-- ----------------------------

-- ----------------------------
-- Table structure for user_default_address
-- ----------------------------
DROP TABLE IF EXISTS `user_default_address`;
CREATE TABLE `user_default_address`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `address_id` int(11) NOT NULL COMMENT '地址ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户默认地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_default_address
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
