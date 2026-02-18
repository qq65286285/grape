package com.grape.grape.repository.neo4j;

import com.grape.grape.entity.neo4j.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends Neo4jRepository<Person, Long> {
    
    // 根据名称查找Person
    List<Person> findByName(String name);
    
    // 根据年龄范围查找Person
    List<Person> findByAgeBetween(Integer minAge, Integer maxAge);
    
    // 使用Cypher查询查找有朋友的Person
    @Query("MATCH (p:Person)-[:KNOWS]->(friend) RETURN p")
    List<Person> findPersonsWithFriends();
    
    // 使用Cypher查询查找两个Person之间的关系
    @Query("MATCH (p1:Person {name: $name1})-[r:KNOWS]->(p2:Person {name: $name2}) RETURN r")
    Object findRelationshipBetweenPersons(String name1, String name2);
}
