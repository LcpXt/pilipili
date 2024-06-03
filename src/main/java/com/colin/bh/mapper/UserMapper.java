package com.colin.bh.mapper;

import com.colin.bh.bean.User;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

/**
 * 2024年03月23日16:58
 */
@Repository
public interface UserMapper {

    Integer selectIdByUsername(String username);

    Boolean insertUser(User user);

    Boolean updatePasswordByEmail(String email, String finalPassword);

    User selectUserByUsernameAndPassword(String username, String finalPassword);

    void updateLastLoginTime(Timestamp currentTime, String username);
}
