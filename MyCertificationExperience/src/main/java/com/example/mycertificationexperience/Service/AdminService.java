package com.example.mycertificationexperience.Service;

import com.example.mycertificationexperience.Model.Admin;
import com.example.mycertificationexperience.Repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public void AddAdmin(Admin admin) {
        adminRepository.save(admin);
    }


}
