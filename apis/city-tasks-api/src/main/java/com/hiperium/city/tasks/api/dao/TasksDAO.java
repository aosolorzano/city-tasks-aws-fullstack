package com.hiperium.city.tasks.api.dao;

import com.hiperium.city.tasks.api.dto.TaskCriteriaDTO;
import com.hiperium.city.tasks.api.dto.TaskDTO;
import com.hiperium.city.tasks.api.exceptions.ValidationException;
import com.hiperium.city.tasks.api.models.Task;
import com.hiperium.city.tasks.api.utils.enums.EnumValidationError;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class TasksDAO {

    private final EntityManager entityManager;

    public TasksDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<TaskDTO> find(final TaskCriteriaDTO criteriaDto) {
        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<TaskDTO> cq = cb.createQuery(TaskDTO.class);
        Root<Task> taskRoot = cq.from(Task.class);
        cq.select(getTaskDtoConstruct(cb, taskRoot));

        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(criteriaDto.getId()) && criteriaDto.getId() >= 0L) {
            predicates.add(cb.equal(taskRoot.get("id"), criteriaDto.getId()));
        }
        if (Objects.nonNull(criteriaDto.getName()) && !criteriaDto.getName().isBlank()) {
            predicates.add(cb.like(taskRoot.get("name"), "%" + criteriaDto.getName() + "%"));
        }
        if (Objects.nonNull(criteriaDto.getStatus())) {
            predicates.add(cb.equal(taskRoot.get("status"), criteriaDto.getStatus()));
        }
        if (Objects.nonNull(criteriaDto.getDeviceId()) && !criteriaDto.getDeviceId().isBlank()) {
            predicates.add(cb.equal(taskRoot.get("deviceId"), criteriaDto.getDeviceId()));
        }
        if (Objects.nonNull(criteriaDto.getDeviceOperation())) {
            predicates.add(cb.equal(taskRoot.get("deviceOperation"), criteriaDto.getDeviceOperation()));
        }
        if (Objects.nonNull(criteriaDto.getExecutionDay()) && !criteriaDto.getExecutionDay().isBlank()) {
            predicates.add(cb.like(taskRoot.get("executionDays"), "%" + criteriaDto.getExecutionDay() + "%"));
        }
        if (Objects.nonNull(criteriaDto.getHour()) && criteriaDto.getHour() >= 0 && criteriaDto.getHour() <= 23) {
            predicates.add(cb.equal(taskRoot.get("hour"), criteriaDto.getHour()));
        }
        if (Objects.nonNull(criteriaDto.getMinute()) && criteriaDto.getMinute() >= 0 && criteriaDto.getMinute() <= 59) {
            predicates.add(cb.equal(taskRoot.get("minute"), criteriaDto.getMinute()));
        }
        assignQueryPredicates(cq, predicates);
        return this.entityManager.createQuery(cq).getResultList();
    }

    private static void assignQueryPredicates(final CriteriaQuery<TaskDTO> cq, final List<Predicate> predicates) {
        if (predicates.isEmpty()) {
            throw new ValidationException(EnumValidationError.NO_CRITERIA_FOUND);
        } else if (predicates.size() == 1) {
            cq.where(predicates.get(0));
        } else {
            cq.where(predicates.toArray(new Predicate[0]));
        }
    }

    private static CompoundSelection<TaskDTO> getTaskDtoConstruct(final CriteriaBuilder cb, final Root<Task> taskRoot) {
        return cb.construct(TaskDTO.class, taskRoot.get("id"), taskRoot.get("name"),
                taskRoot.get("description"), taskRoot.get("status"), taskRoot.get("deviceId"),
                taskRoot.get("deviceOperation"), taskRoot.get("hour"), taskRoot.get("minute"),
                taskRoot.get("executionDays"), taskRoot.get("executeUntil"));
    }
}
