package com.shra1.authmaster.dbmodels;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   @Query(value = "Select * from user u where u.name like :name%", nativeQuery = true)
   List<User> findByNameDistinct(@Param("name") String name);

   User findByUsername(String username);
}
