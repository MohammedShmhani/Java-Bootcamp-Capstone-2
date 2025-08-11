package com.example.mycertificationexperience.Service;

import com.example.mycertificationexperience.Model.Reader;
import com.example.mycertificationexperience.Repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReaderService {
    private final ReaderRepository readerRepository;

    public void addReader(Reader reader) {
        readerRepository.save(reader);
    }
}
