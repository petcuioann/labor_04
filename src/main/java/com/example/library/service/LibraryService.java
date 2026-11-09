package com.example.library.service;

import com.example.library.model.Library;
import com.example.library.model.Member;
import com.example.library.repository.CrudRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LibraryService {
    private final CrudRepository<Library> libraries;
    private final CrudRepository<Member> members;

    public LibraryService(CrudRepository<Library> libraries, CrudRepository<Member> members) {
        this.libraries = libraries;
        this.members = members;
    }

    public Library saveLibrary(Library library) {
        if (library.getId() != null) {
            Library existing = libraries.findById(library.getId());
            if (existing != null && existing != library) {
                if (library.getMembers().isEmpty()) {
                    library.getMembers().addAll(existing.getMembers());
                }
                if (library.getReadableItems().isEmpty()) {
                    library.getReadableItems().addAll(existing.getReadableItems());
                }
            }
        }
        return libraries.save(library);
    }

    public List<Library> findAllLibraries() {
        return libraries.findAll();
    }

    public Library findLibrary(String id) {
        return libraries.findById(id);
    }

    public boolean deleteLibrary(String id) {
        return libraries.delete(id);
    }

    public Member registerMember(Member member) {
        if (member.getId() != null) {
            Member existing = members.findById(member.getId());
            if (existing != null && existing != member) {
                if (member.getLoans().isEmpty()) {
                    member.getLoans().addAll(existing.getLoans());
                }
                if (member.getReservations().isEmpty()) {
                    member.getReservations().addAll(existing.getReservations());
                }
            }
        }
        Member savedMember = members.save(member);
        Library library = libraries.findById(member.getLibraryId());
        if (library != null && !library.getMembers().contains(savedMember)) {
            library.getMembers().add(savedMember);
        }
        return savedMember;
    }

    public List<Member> findAllMembers() {
        return members.findAll();
    }

    public Member findMember(String id) {
        return members.findById(id);
    }

    public List<Member> findMembersByLibrary(String libraryId) {
        List<Member> result = new ArrayList<>();
        for (Member member : members.findAll()) {
            if (libraryId != null && libraryId.equals(member.getLibraryId())) {
                result.add(member);
            }
        }
        return result;
    }

    public boolean deleteMember(String id) {
        Member member = members.findById(id);
        if (member == null) {
            return false;
        }
        Library library = libraries.findById(member.getLibraryId());
        if (library != null) {
            library.getMembers().remove(member);
        }
        return members.delete(id);
    }
}
