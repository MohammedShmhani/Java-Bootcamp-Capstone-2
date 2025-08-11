package com.example.mycertificationexperience.Service;

import com.example.mycertificationexperience.Model.Contributor;
import com.example.mycertificationexperience.Repository.ContributorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContributerService {

    private final ContributorRepository contributorRepository;

    public void addContributor(Contributor contributor) {
        contributorRepository.save(contributor);
    }
}
