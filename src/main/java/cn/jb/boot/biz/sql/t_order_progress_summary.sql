/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : 127.0.0.1:3306
 Source Schema         : mes

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 30/07/2025 13:40:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_order_progress_summary
-- ----------------------------
DROP TABLE IF EXISTS `t_order_progress_summary`;
CREATE TABLE `t_order_progress_summary`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单号',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物料名称',
  `cust_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户名称',
  `need_num` decimal(18, 2) NULL DEFAULT NULL COMMENT '需求数量',
  `bom_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'BOM编号',
  `total_hours` decimal(18, 2) NULL DEFAULT NULL COMMENT '总工时',
  `done_hours` decimal(18, 2) NULL DEFAULT NULL COMMENT '已完成工时',
  `progress_percent` decimal(5, 2) NULL DEFAULT NULL COMMENT '进度百分比',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deliver_time` datetime NULL DEFAULT NULL COMMENT '承诺交期',
  `created_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单进度汇总表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order_progress_summary
-- ----------------------------
INSERT INTO `t_order_progress_summary` VALUES (1, '202507285844', '后尾板焊合', '2516004015-1', 3.00, '2516004015-1', 5.47, 5.47, 100.00, '2025-07-30 11:39:34', '2025-07-29 00:00:00', NULL, '2025-07-28 17:37:17', NULL);

SET FOREIGN_KEY_CHECKS = 1;
