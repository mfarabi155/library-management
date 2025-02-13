package com.example.library.services;

import com.example.library.models.Library;
import com.example.library.repositories.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    @Autowired
    private LibraryRepository libraryRepository;

    public List<Library> getAllLibraries() {
        return libraryRepository.findAll();
    }

    public Optional<Library> getLibraryById(Long id) {
        return libraryRepository.findById(id);
    }

    public Library createLibrary(Library library) {
        return libraryRepository.save(library);
    }

    public Library updateLibrary(Long id, Library libraryDetails) {
        return libraryRepository.findById(id).map(library -> {
            library.setTitle(libraryDetails.getTitle());
            library.setContent(libraryDetails.getContent());
            return libraryRepository.save(library);
        }).orElseThrow(() -> new RuntimeException("Library not found"));
    }

    public void deleteLibrary(Long id) {
        libraryRepository.deleteById(id);
    }
}
