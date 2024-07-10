package org.ems.employee_management_system.services;

import org.ems.employee_management_system.entities.User;
import org.ems.employee_management_system.dto.UserRegistrationDto;

import java.util.List;

public interface UserService {

    User save(UserRegistrationDto registrationDto);

    User findUserByEmail(String email);

    List<UserRegistrationDto> findAllUsers();
}