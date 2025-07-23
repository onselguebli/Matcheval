package com.matcheval.stage.controller;

import com.matcheval.stage.dto.ReqRes;
import com.matcheval.stage.interfaces.IUserService;
import com.matcheval.stage.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("admin")
public class AdminController {
    @Autowired
    IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<ReqRes>  register(@RequestBody ReqRes registerRes){
        return  ResponseEntity.ok(userService.register(registerRes));
    }


    @GetMapping("/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());

    }

    @GetMapping("/list-managers")
    public ResponseEntity<ReqRes> list_managers(){
        return ResponseEntity.ok(userService.getManagers());

    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Long id, @RequestBody ReqRes updatedUser) {
        ReqRes response = userService.updateUser(id, updatedUser);

        if (response.getStatusCode() == 200) {
            return ResponseEntity.ok(response);
        } else if (response.getStatusCode() == 404) {
            return ResponseEntity.status(404).body(response);
        } else {
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/get-user/{userId}")
    public ResponseEntity<ReqRes> getUSerByID(@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUsersById(userId));

    }
    @PostMapping ("/block/{userId}")
    public ResponseEntity<ReqRes> blockUSer(@PathVariable Long userId){
        return ResponseEntity.ok(userService.blockUser(userId));
    }

    @GetMapping("/recruteurs-par-manager/{managerId}")
    public ResponseEntity<List<Users>> getRecruteursParManager(@PathVariable Long managerId) {
        List<Users> recruteurs = userService.findRecruteursByManagerId(managerId);
        return ResponseEntity.ok(recruteurs);
    }

    @GetMapping("/stats/users-by-role")
    public ResponseEntity<Map<String, Long>> getUserCountByRole() {
        Map<String, Long> stats = userService.countUsersByRole();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/users-per-year")
    public ResponseEntity<Map<Integer, Long>> getUsersPerYear() {
        Map<Integer, Long> stats = userService.countUsersByYear();
        return ResponseEntity.ok(stats);
    }

}
