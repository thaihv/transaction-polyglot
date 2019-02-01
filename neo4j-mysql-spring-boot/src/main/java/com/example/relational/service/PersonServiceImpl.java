package com.example.relational.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.graph.domain.GraphPerson;
import com.example.graph.repository.GraphPersonRepository;
import com.example.relational.domain.Person;
import com.example.relational.repository.PersonRepository;

@Service
@Transactional(rollbackFor=Exception.class)
public class PersonServiceImpl implements PersonService {

  @Autowired
  private PersonRepository personRepository;
  
  @Autowired
  private GraphPersonRepository graphPersonRepository;

  @Override
  @Transactional(rollbackFor=Exception.class)
  public Person create(Person person) {
	
	System.out.println("---------------------"+ person.getName());
	GraphPerson newgps = new GraphPerson();
	newgps.setName("g" + person.getName());
	
	Person ps 		= personRepository.save(person);
	GraphPerson gps = graphPersonRepository.save(newgps);
	
	System.out.println("---------------------"+ personRepository.findOne(ps.getId()).getName() + " and " + graphPersonRepository.findOne(gps.getId()).getName());
    return ps;
  }

  @Override
  public Person findOne(Long id) {
    return personRepository.findOne(id);
  }
}
