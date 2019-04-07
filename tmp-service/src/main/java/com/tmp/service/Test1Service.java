package com.tmp.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.tmp.jpa.data.LinkType;
import com.tmp.jpa.data.SpecificationUtil;
import com.tmp.jpa.repository.CustomJpaRepository;
import com.tmp.jpa.service.GeneralService;
import com.tmp.jpa.service.SimpleGeneralService;
import com.tmp.model.Test1;
import com.tmp.repository.Test1Repository;

@Service
public class Test1Service extends SimpleGeneralService<Test1, String> implements GeneralService<Test1, String>  {
	
	@Autowired
	Test1Repository test1Repository;

	@Override
	public CustomJpaRepository<Test1, String> getRepository() {
		return test1Repository;
	}

	@Override
	public Specification<Test1> buildSpecification(Class<Test1> classz, Map<String, Object> searchParams,
			LinkType linkType) {
		return SpecificationUtil.buildSpecification(Test1.class, searchParams, linkType);
	}
	
	public Page<Test1> test(Map<String,Object> searchParams,Pageable pageable) {
		Page<Test1> page = this.findPage(searchParams, pageable);
		return page;
	}

}
