package com.example.library.service;

import com.example.library.model.Library;
import com.example.library.model.Member;
import com.example.library.repository.CrudRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class LibraryService {
    private final CrudRepository<Library> libraries;
    private final CrudRepository<Member> members;

    public LibraryService(CrudRepository<Library> libraries, CrudRepository<Member> members) { this.libraries = libraries; this.members = members; }

    public Library saveLibrary(Library library) {
        library.getMembers().clear();
        library.getReadableItems().clear();
        return libraries.save(library);
    }
    public List<Library> findAllLibraries() { return libraries.findAll(); }
    public Optional<Library> findLibrary(String id) { return libraries.findById(id); }
    public boolean deleteLibrary(String id) {
        libraries.findById(id).ifPresent(l -> { if (!l.getMembers().isEmpty() || !l.getReadableItems().isEmpty()) throw new IllegalStateException("library still has members or items"); });
        return libraries.delete(id);
    }

    public Member registerMember(Member member) {
        Library library = libraries.findById(member.getLibraryId()).orElseThrow(() -> new NoSuchElementException("library not found: " + member.getLibraryId()));
        if (member.getId() != null && members.findById(member.getId()).isPresent()) throw new IllegalStateException("member already exists: " + member.getId());
        member.getLoans().clear();
        member.getReservations().clear();
        library.getMembers().add(members.save(member));
        return member;
    }
    public List<Member> findAllMembers() { return members.findAll(); }
    public Optional<Member> findMember(String id) { return members.findById(id); }
    public List<Member> findMembersByLibrary(String libraryId) { return members.findBy(m -> libraryId.equals(m.getLibraryId())); }
    public boolean deleteMember(String id) {
        Optional<Member> member = members.findById(id);
        if (member.isEmpty()) return false;
        if (member.get().hasOpenLoans()) throw new IllegalStateException("member has open loans");
        libraries.findById(member.get().getLibraryId()).ifPresent(l -> l.getMembers().removeIf(m -> m.getId().equals(id)));
        return members.delete(id);
    }
}
