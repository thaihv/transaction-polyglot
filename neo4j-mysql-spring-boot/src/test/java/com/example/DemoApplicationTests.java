package com.example;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.relational.domain.Person;
import com.example.relational.service.PersonService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= DemoApplication.class)
public class DemoApplicationTests {

	@Autowired
	PersonService pss;

	
	@Test
	public void contextLoads() {
	}
	
	@Test
	public void TestTransactional() {
		Person ps = new Person();
		ps.setName("zzl");
		pss.create(ps);
	}

	
}
