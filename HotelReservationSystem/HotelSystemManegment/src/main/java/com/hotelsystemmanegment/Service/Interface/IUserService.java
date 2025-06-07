package com.hotelsystemmanegment.Service.Interface;

import com.hotelsystemmanegment.DTO.LoginRequest;
import com.hotelsystemmanegment.DTO.Response;
import com.hotelsystemmanegment.Entity.User;

public interface IUserService {

    Response register(User user);

    Response login(LoginRequest loginRequest);

    Response getAllUsers();

    Response getUserBookingHistory(String userId);

    Response deleteUser(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);



}
