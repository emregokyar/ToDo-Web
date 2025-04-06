package com.todo.controller;

import com.todo.entity.Task;
import com.todo.entity.User;
import com.todo.service.TaskService;
import com.todo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/home")
public class MainController {
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public MainController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping("/tasks")
    public String listTasks(Principal principal, Model model) {

        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        List<Task> tasks = user.getTasks();
        model.addAttribute("tasks", tasks);

        return "main/tasks";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model) {
        Task task = new Task();
        model.addAttribute("task", task);

        return "main/task-form";
    }

    @PostMapping("/save")
    public String saveTask(@ModelAttribute("task") Task task, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        task.setUser(user);
        taskService.save(task);
        return "redirect:/home/tasks";
    }


    @GetMapping("/showFormToUpdate")
    public String showFormToUpdate(@RequestParam("taskId") int requiredTaskId, Model model) {
        Task task = taskService.findById(requiredTaskId);
        if (task == null) {
            throw new RuntimeException("Task with Id: " + requiredTaskId + " can not be found");
        }
        model.addAttribute("task", task);
        return "main/task-form";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("taskId") int requiredTaskId, Model model) {
        if (taskService.findById(requiredTaskId) == null) {
            throw new RuntimeException("Task not found with Id: " + requiredTaskId);
        }
        taskService.deleteByTaskId(requiredTaskId);
        return "redirect:/home/tasks";
    }
}