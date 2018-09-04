package com.shra1.authmaster.dbmodels;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SBUserRepository extends JpaRepository<SBUser, Long> {
   SBUser findByEmail(String email);
}
