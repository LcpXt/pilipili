package com.colin.bh.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * {@code @Info}
 *
 * @author 777
 * {@code @date} 2024-03-26
 * {@code @time} 15:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * 用户主键id
     */
    private Integer id;

    /**
     * 用户名
     */
    @Pattern(regexp = "^[a-zA-Z0-9]{3,12}$", message = "用户名不符合规则")
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮件
     */
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不符合规则")
    private String email;

    /**
     * 注册时间
     */
    private Timestamp registerTime;

    /**
     * 更新时间
     */
    private Timestamp updateTime;

    /**
     * 最后一次登录时间
     */
    private Timestamp lastLoginTime;

    /**
     * 用户类别
     * 0: 普通用户
     * 1: VIP用户
     * 2: SVIP用户
     * 3: 创作者
     * 4: VIP + 创作者
     * 5: SVIP + 创作者
     */
    private Integer userType;

    /**
     * 年龄
     */
    @Min(value = 18, message = "年龄最小值不能小于18")
    @Max(value = 60, message = "年龄最大值不能大于60")
    private Integer age;

    /**
     * 性别
     */
    @NotNull
    private String sex;

    /**
     * VIP / SVIP 过期时间
     */
    private Date expirationDate;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 电话号码
     */
    private String phoneNumber;
}
