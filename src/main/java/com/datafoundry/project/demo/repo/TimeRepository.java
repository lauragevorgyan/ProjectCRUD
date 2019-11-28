package com.datafoundry.project.demo.repo;

import com.datafoundry.project.demo.model.Time;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TimeRepository extends PagingAndSortingRepository<Time, Long>,
        JpaSpecificationExecutor<Time> {
}