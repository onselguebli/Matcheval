package com.matcheval.stage.interfaces;

import com.matcheval.stage.dto.MonthlyDashboardDTO;
import com.matcheval.stage.dto.ReqRes;
import com.matcheval.stage.model.Users;

import java.util.List;
import java.util.Map;

public interface IUserService {


    ReqRes login(ReqRes reqRes);
    ReqRes register(ReqRes registrationRequest);

    ReqRes getAllUsers();
    ReqRes getUsersById(Long id);
    ReqRes updateUser(Long id, ReqRes updatedUser);

     ReqRes blockUser(Long userId);
     ReqRes getManagers();


}
