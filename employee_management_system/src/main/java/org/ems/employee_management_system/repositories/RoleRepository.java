package org.ems.employee_management_system.repositories;

import org.ems.employee_management_system.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}