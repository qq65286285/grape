package com.grape.grape.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用例执行表 实体类。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("test_case_executions")
public class CaseExecutions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 执行记录唯一ID
     */
    @Id(keyType = KeyType.Auto)
    private Integer id;

    /**
     * 关联的测试用例ID
     */
    private Integer testCaseId;

    /**
     * 测试环境ID（关联测试环境配置表）
     */
    private Integer environmentId;

    /**
     * 实际执行结果
     */
    private String actualResult;

    /**
     * 执行人ID
     */
    private Integer executedBy;

    /**
     * 执行时间（毫秒级时间戳）
     */
    private Long executedAt;

    /**
     * 执行状态
     */
    private String executionStatus;

    /**
     * 执行备注
     */
    private String remark;

    /**
     * 创建人ID
     */
    private Integer createdBy;

    /**
     * 更新人ID
     */
    private Integer updatedBy;

    /**
     * 创建时间（毫秒级时间戳）
     */
    private Long createdAt;

    /**
     * 更新时间（毫秒级时间戳）
     */
    private Long updatedAt;

    /**
     * 逻辑删除标记（0: 未删除, 1: 已删除）
     */
    private Integer isDeleted;

    /**
     * 乐观锁版本号（默认值为1）
     */
    private Integer revision;

}
