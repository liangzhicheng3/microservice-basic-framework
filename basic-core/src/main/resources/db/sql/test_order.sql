/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50521
Source Host           : localhost:3306
Source Database       : test-order

Target Server Type    : MYSQL
Target Server Version : 50521
File Encoding         : 65001

Date: 2021-08-13 14:05:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for test_order
-- ----------------------------
DROP TABLE IF EXISTS `test_order`;
CREATE TABLE `test_order` (
  `id` varchar(19) NOT NULL COMMENT '订单id',
  `user_id` varchar(19) NOT NULL COMMENT '用户id',
  `username` varchar(30) NOT NULL COMMENT '用户名称',
  `product_id` varchar(19) NOT NULL COMMENT '商品id',
  `product_name` varchar(30) NOT NULL COMMENT '商品名称',
  `product_price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `buy_num` int(11) NOT NULL COMMENT '购买数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of test_order
-- ----------------------------
INSERT INTO `test_order` VALUES ('113046', '1', 'liangzhicheng', '1', '华为', '3866.00', '1');
INSERT INTO `test_order` VALUES ('134158', '1', 'liangzhicheng', '2', '小米', '3866.00', '1');
