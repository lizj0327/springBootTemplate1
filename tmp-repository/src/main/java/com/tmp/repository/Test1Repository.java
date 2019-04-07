package com.tmp.repository;

import org.springframework.stereotype.Repository;

import com.tmp.jpa.repository.CustomJpaRepository;
import com.tmp.model.Test1;

@Repository
public interface Test1Repository extends CustomJpaRepository<Test1, String>  {

}
