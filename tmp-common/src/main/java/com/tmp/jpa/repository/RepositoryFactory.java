package com.tmp.jpa.repository;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

public class RepositoryFactory extends JpaRepositoryFactory {

    public RepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {

        return CustomJpaRepositoryImpl.class;
    }

}
