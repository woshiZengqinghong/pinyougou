package com.pinyougou.user.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class Person implements Serializable {

    @Email(message = "邮箱格式不正确")
    @Length(min = 5,max = 10,message="邮箱长度为5-20")
    private String email;

    @NotBlank(message = "手机号不允许为空")
    @Pattern(regexp = "[1][3|4|5|7|8][0-9]{9}",message = "手机号格式不正确！")
    private String mobile;

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4E00-\\u9FA5]+$",message = "用户名只能为数字或者字母!")
    private String username;

    @Range(min = 1L,max = 150L,message = "年龄必须在150岁之间")
    private Integer age;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
