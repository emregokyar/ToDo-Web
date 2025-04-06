package com.todo.service;

import com.todo.dao.TaskRepository;
import com.todo.entity.Task;
import com.todo.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> listAllTasks(User user) {
        return taskRepository.findByUser(user);
    }

    @Override
    public Task findById(int taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Cannot find task with ID: " + taskId));
    }

    @Override
    @Transactional
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public void deleteByTaskId(int taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("Task not found with ID: " + taskId);
        }
        taskRepository.deleteById(taskId);
    }
}
