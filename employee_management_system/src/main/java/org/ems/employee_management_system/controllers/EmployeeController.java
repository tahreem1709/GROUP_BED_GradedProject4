package org.ems.employee_management_system.controllers;

import jakarta.validation.Valid;
import org.ems.employee_management_system.entities.Employee;
import org.ems.employee_management_system.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeController(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/")
    public String getEmployeeList(Model model){
        List<Employee> employeeList = employeeRepository.findAll();
        if(!employeeList.isEmpty()){
            model.addAttribute("employees", employeeList);
        }
        return "index";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/create")
    public String createEmployee(Employee employee){
        return "create-employee";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String createEmployee(@Valid Employee employee, BindingResult result, Model model) {
        if(result.hasErrors()){
            return "create-employee";
        }
        employee.setNew(true);
        employeeRepository.save(employee);
        return "redirect:/employees/";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editEmployee(@PathVariable("id") long id, Model model){
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid employee id: " + id));

        model.addAttribute("employee", employee);
        return "edit-employee";
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateEmployee(@PathVariable("id") long id, @Valid Employee employee, BindingResult result, Model model){
        if(result.hasErrors()){
            employee.setId(id);
            return "edit-employee";
        }
        employee.setNew(false);
        employeeRepository.save(employee);
        return "redirect:/employees/";
    }

    @GetMapping("/search")
    public String search(Model model, @RequestParam("query") Optional<String> query){
        if(query.isEmpty()){
            return "redirect:/employees/";
        }
        List<Employee> employeeList = employeeRepository.findByFirstName(query.get());
        model.addAttribute("employees", employeeList);
        model.addAttribute("query", query.get());
        return "index";
    }

    @GetMapping("/sort")
    public String sort(Model model, @RequestParam("order") Optional<String> query){
        if(query.isEmpty()){
            return "redirect:/employees/";
        }
        if(!query.get().equals("asc") && !query.get().equals("desc")){
            return "redirect:/employees/";
        }
        Sort.Order order = new Sort.Order(query.get().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "fname");
        List<Employee> employeeList = employeeRepository.findAll(Sort.by(order));

        model.addAttribute("employees", employeeList);
        model.addAttribute("order", query.get());
        return "index";
    }

    @GetMapping("/view/{id}")
    public String viewStudent(@PathVariable("id") long id, Model model){
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid employee id: " + id));
        model.addAttribute("employee", employee);
        return "view";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteEmployee(@PathVariable("id") long id, Model model){
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid employee id: " + id));
        employeeRepository.delete(employee);
        model.addAttribute("employees", employeeRepository.findAll());
        return "redirect:/employees/";
    }
}