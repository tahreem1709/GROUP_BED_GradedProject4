package org.ems.employee_management_system.controllers;

import jakarta.validation.Valid;
import org.ems.employee_management_system.entities.User;
import org.ems.employee_management_system.services.UserService;
import org.ems.employee_management_system.dto.UserRegistrationDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserRegistrationController {

    private final UserService userService;

    public UserRegistrationController(UserService userService) {
        super();
        this.userService = userService;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto(){
        return new UserRegistrationDto();
    }

    @GetMapping("/")
    public String listOfUsers(Model model) {
        List<UserRegistrationDto> userRegistrationDtoList =  userService.findAllUsers();
        model.addAttribute("users", userRegistrationDtoList);
        return "users";
    }

    @GetMapping("/create")
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping("/create")
    public String registerUserAccount(@Valid @ModelAttribute("user")UserRegistrationDto registrationDto, BindingResult result, Model model){
        User existingUser = userService.findUserByEmail(registrationDto.getEmail());
        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
            result.rejectValue("email", null,
                    "There is already an account registered with the same email");
        }

        if(result.hasErrors()){
            model.addAttribute("user", registrationDto);
            return "registration";
        }

        userService.save(registrationDto);
        return "redirect:/users/create?success";
    }
}