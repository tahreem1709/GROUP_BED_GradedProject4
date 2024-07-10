package org.ems.employee_management_system.controllers;

import jakarta.validation.Valid;
import org.ems.employee_management_system.dto.UserRegistrationDto;
import org.ems.employee_management_system.entities.User;
import org.ems.employee_management_system.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserRegAPIController {

    private final UserService userService;

    public UserRegAPIController(UserService userService) {
        super();
        this.userService = userService;
    }

    @GetMapping("/")
    public List<UserRegistrationDto> listOfUsers() {
        return userService.findAllUsers();
    }

    @PostMapping("/create")
    public String registerUserAccount(@Valid @ModelAttribute("user")UserRegistrationDto registrationDto, BindingResult result, Model model){
        User existingUser = userService.findUserByEmail(registrationDto.getEmail());
        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
            return "There is already an account registered with the same email";
        }

        if(result.hasErrors()){
            return result.getAllErrors().toString();
        }

        userService.save(registrationDto);
        return "User Added successfully!";
    }
}