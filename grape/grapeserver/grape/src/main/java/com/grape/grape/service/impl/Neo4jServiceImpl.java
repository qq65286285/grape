package com.grape.grape.service.impl;

import com.grape.grape.entity.neo4j.Person;
import com.grape.grape.repository.neo4j.PersonRepository;
import com.grape.grape.service.Neo4jService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Neo4jServiceImpl implements Neo4jService {
    
    @Autowired(required = false)
    private PersonRepository personRepository;
    
    @Override
    public Person createPerson(Person person) {
        if (personRepository == null) return null;
        return personRepository.save(person);
    }
    
    @Override
    public Person findPersonById(Long id) {
        if (personRepository == null) return null;
        return personRepository.findById(id).orElse(null);
    }
    
    @Override
    public List<Person> findPersonsByName(String name) {
        if (personRepository == null) return null;
        return personRepository.findByName(name);
    }
    
    @Override
    public List<Person> findPersonsByAgeRange(Integer minAge, Integer maxAge) {
        if (personRepository == null) return null;
        return personRepository.findByAgeBetween(minAge, maxAge);
    }
    
    @Override
    public List<Person> findAllPersons() {
        if (personRepository == null) return null;
        return personRepository.findAll();
    }
    
    @Override
    public Person updatePerson(Person person) {
        if (personRepository == null) return null;
        return personRepository.save(person);
    }
    
    @Override
    public void deletePerson(Long id) {
        if (personRepository != null) {
            personRepository.deleteById(id);
        }
    }
    
    @Override
    public List<Person> findPersonsWithFriends() {
        if (personRepository == null) return null;
        return personRepository.findPersonsWithFriends();
    }
    
    @Override
    public void createFriendship(Long personId1, Long personId2) {
        if (personRepository == null) return;
        Person person1 = personRepository.findById(personId1).orElse(null);
        Person person2 = personRepository.findById(personId2).orElse(null);
        
        if (person1 != null && person2 != null) {
            // 这里简化处理，实际应该处理集合的添加
            person1.getFriends().add(person2);
            personRepository.save(person1);
        }
    }
    
    @Override
    public Object findRelationshipBetweenPersons(String name1, String name2) {
        if (personRepository == null) return null;
        return personRepository.findRelationshipBetweenPersons(name1, name2);
    }
}
