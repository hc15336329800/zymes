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

 Date: 28/07/2025 13:46:30
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
  `created_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单进度汇总表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order_progress_summary
-- ----------------------------
INSERT INTO `t_order_progress_summary` VALUES (1, '202507012489', '后桥焊接安装架', '临工重机', 10.00, '25160085532', 192.16, 46.70, 24.30, '2025-07-28 12:50:04', NULL, '2025-07-01 09:15:20', NULL);
INSERT INTO `t_order_progress_summary` VALUES (2, '202507015071', '底盘总成配送合码', '临工重机', 8.00, '25160107521', 634.37, 4.10, 0.65, '2025-07-28 12:50:04', NULL, '2025-07-01 09:07:02', NULL);
INSERT INTO `t_order_progress_summary` VALUES (3, '202507015057', '车桥焊接安装架', '临工重机', 20.00, '25160062882', 43.86, 7.06, 16.10, '2025-07-28 12:50:04', NULL, '2025-07-01 09:07:38', NULL);
INSERT INTO `t_order_progress_summary` VALUES (4, '202507013849', '转台焊合', '临工重机', 6.00, '25230034194', 1383.62, 55.61, 4.02, '2025-07-28 12:50:04', NULL, '2025-07-01 09:04:46', NULL);
INSERT INTO `t_order_progress_summary` VALUES (5, '202507175947', '后桥焊接安装架', '临工重机', 2.00, '25160116711', 283.19, 283.19, 100.00, '2025-07-28 12:50:04', NULL, '2025-07-17 10:56:49', NULL);
INSERT INTO `t_order_progress_summary` VALUES (6, '202507173426', '转台配送合码', '临工重机', 10.00, '25230024662', 134.50, 1.53, 1.14, '2025-07-28 12:50:04', NULL, '2025-07-17 10:56:20', NULL);
INSERT INTO `t_order_progress_summary` VALUES (7, '202507266462', '后尾板焊合', '2516004015-1', 2.00, '2516004015-1', 5.47, 5.47, 100.00, '2025-07-28 12:50:04', NULL, '2025-07-26 17:10:26', NULL);

SET FOREIGN_KEY_CHECKS = 1;
