/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50521
Source Host           : localhost:3306
Source Database       : test-user

Target Server Type    : MYSQL
Target Server Version : 50521
File Encoding         : 65001

Date: 2021-08-13 14:06:19
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for test_user
-- ----------------------------
DROP TABLE IF EXISTS `test_user`;
CREATE TABLE `test_user` (
  `id` varchar(19) NOT NULL COMMENT '用户id(主键)',
  `username` varchar(30) NOT NULL COMMENT '账号名称',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `phone` varchar(11) NOT NULL COMMENT '手机号码',
  `country_id` varchar(10) NOT NULL COMMENT '国家id',
  `country_name` varchar(10) NOT NULL COMMENT '国家名称',
  `province_id` varchar(10) NOT NULL COMMENT '省份id',
  `province_name` varchar(10) NOT NULL COMMENT '省份名称',
  `city_id` varchar(10) NOT NULL COMMENT '城市id',
  `city_name` varchar(10) NOT NULL COMMENT '城市名称',
  `open_id` varchar(100) NOT NULL COMMENT '微信授权唯一凭证',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of test_user
-- ----------------------------
INSERT INTO `test_user` VALUES ('1', 'liangzhicheng', '123456', '15800000000', '101', '中国', '10119', '广东', '1011917', '东莞', '');
