package com.mmall.dao;

import com.mmall.domain.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    boolean checkNameExist(String username);

    User selectByNameAndPwd(@Param("username") String username , @Param("password") String password);

    boolean checkEmailExist(String email);

    boolean checkEmailById(@Param("email") String email ,@Param("userId") Integer userId);

    String selectQuestionByUserName(String username);

    boolean checkAnswer(@Param("username") String username , @Param("question") String question , @Param("answer") String answer);

    boolean updatePasswordByUsername(@Param("username") String password , @Param("newPassword") String newPassword);

    boolean checkPassword(@Param("id") Integer id, @Param("password") String passwordOld);
}