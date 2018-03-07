package com.mmall.domain;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
public class User {
    private Integer id;

    @NotBlank
    private String username;

    @NotBlank
    @Length(min = 6 , message = "密码长度至少为6")
    @JsonIgnore
    private String password;

    @Email
    private String email;

    @NotBlank
    private String phone;

    private String question;

    @JsonIgnore
    private String answer;

    @JsonIgnore
    private Integer role = 0;

    private Date createTime;

    @JsonIgnore
    private Date updateTime;

    public User() {
    }

}