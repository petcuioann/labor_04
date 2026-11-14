package com.example.library.service;

import com.example.library.model.Library;
import com.example.library.model.Loan;
import com.example.library.model.Member;
import com.example.library.model.Reservation;
import com.example.library.repository.CrudRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LibraryService {
    private final CrudRepository<Library> libraries;
    private final CrudRepository<Member> members;
    private final CrudRepository<Loan> loans;
    private final CrudRepository<Reservation> reservations;

    public LibraryService(
            CrudRepository<Library> libraries,
            CrudRepository<Member> members,
            CrudRepository<Loan> loans,
            CrudRepository<Reservation> reservations) {
        this.libraries = libraries;
        this.members = members;
        this.loans = loans;
        this.reservations = reservations;
    }

    public Library saveLibrary(Library library) {
        if (library.getId() != null && !library.getId().isBlank()) {
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
        Library library = libraries.findById(id);
        if (library == null) {
            return false;
        }
        boolean hasMembers = !library.getMembers().isEmpty()
                || !findMembersByLibrary(id).isEmpty();
        boolean hasItems = !library.getReadableItems().isEmpty();
        if (hasMembers || hasItems) {
            throw new IllegalStateException(
                    "Cannot delete library '" + library.getName()
                            + "' because it still has linked members or readable items. Delete child entities first.");
        }
        return libraries.delete(id);
    }

    public Member registerMember(Member member) {
        String oldLibraryId = null;
        if (member.getId() != null && !member.getId().isBlank()) {
            Member existing = members.findById(member.getId());
            if (existing != null && existing != member) {
                oldLibraryId = existing.getLibraryId();
                if (member.getLoans().isEmpty()) {
                    member.getLoans().addAll(existing.getLoans());
                }
                if (member.getReservations().isEmpty()) {
                    member.getReservations().addAll(existing.getReservations());
                }
            }
        }
        Member savedMember = members.save(member);

        if (oldLibraryId != null && !oldLibraryId.equals(savedMember.getLibraryId())) {
            Library oldLibrary = libraries.findById(oldLibraryId);
            if (oldLibrary != null) {
                oldLibrary.getMembers().removeIf(m -> savedMember.getId().equals(m.getId()));
                libraries.save(oldLibrary);
            }
        }

        Library library = libraries.findById(savedMember.getLibraryId());
        if (library != null) {
            library.getMembers().removeIf(m -> savedMember.getId().equals(m.getId()));
            library.getMembers().add(savedMember);
            libraries.save(library);
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
        boolean hasLoans = !member.getLoans().isEmpty()
                || loans.findAll().stream().anyMatch(l -> id.equals(l.getMemberId()));
        boolean hasReservations = !member.getReservations().isEmpty()
                || reservations.findAll().stream().anyMatch(r -> id.equals(r.getMemberId()));
        if (hasLoans || hasReservations) {
            throw new IllegalStateException(
                    "Cannot delete member '" + member.getName()
                            + "' because they still have loans or reservations. Delete child entities first.");
        }

        Library library = libraries.findById(member.getLibraryId());
        if (library != null) {
            library.getMembers().removeIf(m -> id.equals(m.getId()));
            libraries.save(library);
        }
        return members.delete(id);
    }
}
