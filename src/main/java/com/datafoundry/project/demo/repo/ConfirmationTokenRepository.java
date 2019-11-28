package com.datafoundry.project.demo.repo;

import com.datafoundry.project.demo.model.ConfirmationToken;
import org.springframework.data.repository.CrudRepository;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, String> {
}