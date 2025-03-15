package com.todo.service;

import com.todo.entity.Task;
import com.todo.entity.User;

import java.util.List;

public interface TaskService {
    List<Task> listAllTasks(User user);
    Task findById(int taskId);
    Task save(Task task);
    void deleteByTaskId(int taskId);
}
