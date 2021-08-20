/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50521
Source Host           : localhost:3306
Source Database       : test-product

Target Server Type    : MYSQL
Target Server Version : 50521
File Encoding         : 65001

Date: 2021-08-13 14:06:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for test_product
-- ----------------------------
DROP TABLE IF EXISTS `test_product`;
CREATE TABLE `test_product` (
  `id` varchar(19) NOT NULL COMMENT '商品id',
  `name` varchar(30) NOT NULL COMMENT '商品名称',
  `price` decimal(10,2) NOT NULL COMMENT '商品价格',
  `stock` int(11) NOT NULL COMMENT '库存',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of test_product
-- ----------------------------
INSERT INTO `test_product` VALUES ('1', '华为', '6999.00', '668');
INSERT INTO `test_product` VALUES ('2', '小米', '3866.00', '1120');
INSERT INTO `test_product` VALUES ('3', '魅族', '2888.00', '339');
