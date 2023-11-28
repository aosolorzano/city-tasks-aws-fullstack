package com.hiperium.city.tasks.api.repositories;

import com.hiperium.city.tasks.api.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Task findByJobId(String jobId);
}
