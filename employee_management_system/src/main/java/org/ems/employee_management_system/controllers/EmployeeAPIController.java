package org.ems.employee_management_system.controllers;

import jakarta.validation.Valid;
import org.ems.employee_management_system.entities.Employee;
import org.ems.employee_management_system.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/employees")
public class EmployeeAPIController {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeAPIController(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/")
    public List<Employee> getEmployeeList(){
        return employeeRepository.findAll();
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Employee createEmployee(@Valid Employee employee, BindingResult result, Model model) throws Exception {
        if(result.hasErrors()){
            throw new Exception(result.getAllErrors().toString());
        }
        employee.setNew(true);
        return employeeRepository.save(employee);
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Employee updateEmployee(@PathVariable("id") long id, @Valid Employee employee, BindingResult result, Model model) throws Exception {
        if(result.hasErrors()){
            employee.setId(id);
            throw new Exception(result.getAllErrors().toString());
        }
        employee.setNew(false);
        return employeeRepository.save(employee);
    }

    @GetMapping("/search")
    public List<Employee> search(Model model, @RequestParam("query") String query){
        return employeeRepository.findByFirstName(query);
    }

    @GetMapping("/sort")
    public List<Employee> sort(Model model, @RequestParam("order") String query){
        Sort.Order order = new Sort.Order(query.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "fname");
        return employeeRepository.findAll(Sort.by(order));

    }

    @GetMapping("/view/{id}")
    public Employee viewStudent(@PathVariable("id") long id, Model model){
        return employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid employee id: " + id));

    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteEmployee(@PathVariable("id") long id, Model model){
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid employee id: " + id));
        employeeRepository.delete(employee);
        return "Deleted Employee with id - " + employee.getId();
    }
}