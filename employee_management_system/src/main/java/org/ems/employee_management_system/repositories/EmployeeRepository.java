package org.ems.employee_management_system.repositories;

import org.ems.employee_management_system.entities.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
}