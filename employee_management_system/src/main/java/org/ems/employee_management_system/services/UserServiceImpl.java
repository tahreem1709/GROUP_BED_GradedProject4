package org.ems.employee_management_system.services;

import org.ems.employee_management_system.dto.UserRegistrationDto;
import org.ems.employee_management_system.entities.Role;
import org.ems.employee_management_system.entities.User;
import org.ems.employee_management_system.repositories.RoleRepository;
import org.ems.employee_management_system.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements  UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(UserRegistrationDto userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName(userDto.getRole());
        if(role == null){
            role = checkRoleExist(userDto.getRole());
        }
        user.setRoles(List.of(role));
        userRepository.save(user);
        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserRegistrationDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    private UserRegistrationDto mapToUserDto(User user){
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRoles().get(0).getName());
        return userDto;
    }

    private Role checkRoleExist(String strRole){
        Role role = new Role();
        role.setName(strRole);
        return roleRepository.save(role);
    }
}