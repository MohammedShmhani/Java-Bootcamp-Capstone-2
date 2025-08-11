package com.example.mycertificationexperience.Repository;

import com.example.mycertificationexperience.Model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReaderRepository extends JpaRepository<Reader, Integer> {

    Reader findReaderById(Integer id);
}
