package com.grape.grape.service;

import com.grape.grape.entity.neo4j.Person;

import java.util.List;

public interface Neo4jService {
    
    // 创建Person节点
    Person createPerson(Person person);
    
    // 根据ID查找Person
    Person findPersonById(Long id);
    
    // 根据名称查找Person
    List<Person> findPersonsByName(String name);
    
    // 根据年龄范围查找Person
    List<Person> findPersonsByAgeRange(Integer minAge, Integer maxAge);
    
    // 查找所有Person
    List<Person> findAllPersons();
    
    // 更新Person
    Person updatePerson(Person person);
    
    // 删除Person
    void deletePerson(Long id);
    
    // 查找有朋友的Person
    List<Person> findPersonsWithFriends();
    
    // 建立两个Person之间的朋友关系
    void createFriendship(Long personId1, Long personId2);
    
    // 查找两个Person之间的关系
    Object findRelationshipBetweenPersons(String name1, String name2);
}
