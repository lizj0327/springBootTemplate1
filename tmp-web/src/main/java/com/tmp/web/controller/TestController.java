package com.tmp.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tmp.service.Test1Service;
import com.tmp.web.base.SimpleRestController;

@RestController
public class TestController extends SimpleRestController {
	
	@Autowired
	Test1Service test1Service;

	@RequestMapping("/test1")
	public Page test(HttpServletRequest request,@PageableDefault(sort = { "bb" }, direction = Sort.Direction.DESC) Pageable pageRequest) {
		Map<String,Object> searchParams = getSearchParams(request);
		Page page = test1Service.test(searchParams, pageRequest);
		return page;
	}
}
