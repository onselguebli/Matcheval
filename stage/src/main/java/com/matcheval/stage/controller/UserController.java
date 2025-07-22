package com.matcheval.stage.controller;

import com.matcheval.stage.interfaces.IUserService;
import com.matcheval.stage.dto.ReqRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("recruiter")
public class UserController {

    @Autowired
    IUserService userService;
//    @PostMapping("/login")
//    public String login(@RequestBody Users user) {  // Use a DTO instead of Users
//        return userService.login(user);
//    }

    @PostMapping("/login")
    public ReqRes login(@RequestBody ReqRes reqRes) {  // ✅ Use DTO
        return userService.login(reqRes);
    }



}
