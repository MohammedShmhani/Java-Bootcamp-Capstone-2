package com.example.mycertificationexperience.Repository;

import com.example.mycertificationexperience.Model.CertificationExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationExperienceRepository extends JpaRepository<CertificationExperience, Integer> {

    CertificationExperience findCertificationExperienceById(Integer Id);

    // NEW: fetch all experiences by category id
    List<CertificationExperience> findAllByCategoryId(Integer categoryId);

    List<CertificationExperience> findByScoreAchievedGreaterThan(Double score);

    // NEW: كل التجارب التي أضافها مساهم معيّن
    List<CertificationExperience> findAllByContributorId(Integer contributorId);

    List<CertificationExperience> findAllByProviderIgnoreCase(String provider);

    // NEW
    List<CertificationExperience> findAllByPriceBetween(Double min, Double max);

    // NEW
    List<CertificationExperience> findAllByQuestionCountBetween(Integer min, Integer max);


}

