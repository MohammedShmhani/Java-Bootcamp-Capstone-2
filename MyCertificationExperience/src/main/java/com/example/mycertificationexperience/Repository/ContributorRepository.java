package com.example.mycertificationexperience.Repository;

import com.example.mycertificationexperience.Model.Contributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContributorRepository extends JpaRepository<Contributor, Integer> {

    Contributor findContributorById(Integer contributorId);
}
