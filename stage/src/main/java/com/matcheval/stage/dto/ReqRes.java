package com.matcheval.stage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.matcheval.stage.model.Civility;
import com.matcheval.stage.model.Roles;
import com.matcheval.stage.model.Users;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqRes {
    private String email;
    private String firstname;
    private String lastname;
    private Civility civility;
    private Date dnaiss;
    private String phonenumber;
    private String password;
    private int statusCode;
    private String message;
    private String token;
    private String expirationTime;
    private String error;
    private Roles role;
    private Boolean enabled;
    private Boolean locked;
    private Users user;
    private Long managerId;
    private List<Users> listusers;
    private Date createdAt;

}
