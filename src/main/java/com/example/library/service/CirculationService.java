package com.example.library.service;

import com.example.library.model.Library;
import com.example.library.model.Loan;
import com.example.library.model.Member;
import com.example.library.model.Publication;
import com.example.library.model.ReadableItem;
import com.example.library.model.Reservation;
import com.example.library.repository.CrudRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CirculationService {
    private final CrudRepository<Loan> loans;
    private final CrudRepository<Reservation> reservations;
    private final CrudRepository<Member> members;
    private final CrudRepository<ReadableItem> items;
    private final CrudRepository<Publication> publications;
    private final CrudRepository<Library> libraries;

    public CirculationService(
            CrudRepository<Loan> loans,
            CrudRepository<Reservation> reservations,
            CrudRepository<Member> members,
            CrudRepository<ReadableItem> items,
            CrudRepository<Publication> publications,
            CrudRepository<Library> libraries) {
        this.loans = loans;
        this.reservations = reservations;
        this.members = members;
        this.items = items;
        this.publications = publications;
        this.libraries = libraries;
    }

    private void saveAndSyncItem(ReadableItem item) {
        ReadableItem saved = items.save(item);
        Publication pub = publications.findById(saved.getPublicationId());
        if (pub != null) {
            for (int i = 0; i < pub.getCopies().size(); i++) {
                if (saved.getId().equals(pub.getCopies().get(i).getId())) {
                    pub.getCopies().set(i, saved);
                    publications.save(pub);
                    break;
                }
            }
        }
        for (Library lib : libraries.findAll()) {
            for (int i = 0; i < lib.getReadableItems().size(); i++) {
                if (saved.getId().equals(lib.getReadableItems().get(i).getId())) {
                    lib.getReadableItems().set(i, saved);
                    libraries.save(lib);
                    break;
                }
            }
        }
    }

    private void syncMemberReservation(Member member, Reservation reservation) {
        if (member == null) {
            return;
        }
        member.getReservations().removeIf(r -> reservation.getId().equals(r.getId()));
        member.getReservations().add(reservation);
        members.save(member);
    }

    private void syncMemberLoan(Member member, Loan loan) {
        if (member == null) {
            return;
        }
        member.getLoans().removeIf(l -> loan.getId().equals(l.getId()));
        member.getLoans().add(loan);
        members.save(member);
    }

    public Reservation reserve(String memberId, String itemId) {
        Member member = members.findById(memberId);
        ReadableItem item = items.findById(itemId);
        if (member == null || item == null || !item.isAvailable()) {
            return null;
        }

        item.setStatus(ReadableItem.Status.RESERVED);
        saveAndSyncItem(item);

        Reservation reservation = new Reservation(null, memberId, itemId, LocalDate.now());
        Reservation savedReservation = reservations.save(reservation);
        syncMemberReservation(member, savedReservation);
        return savedReservation;
    }

    public Reservation updateReservation(Reservation updated) {
        if (updated == null || updated.getId() == null) {
            return null;
        }
        Reservation existing = reservations.findById(updated.getId());
        if (existing == null) {
            return null;
        }
        String oldMemberId = existing.getMemberId();
        String oldItemId = existing.getReadableItemId();

        if (updated.getDate() == null) {
            updated.setDate(existing.getDate());
        }
        if (updated.getStatus() == null) {
            updated.setStatus(existing.getStatus());
        }

        if (oldItemId != null && !oldItemId.equals(updated.getReadableItemId())) {
            ReadableItem oldItem = items.findById(oldItemId);
            if (oldItem != null && oldItem.getStatus() == ReadableItem.Status.RESERVED) {
                oldItem.setStatus(ReadableItem.Status.AVAILABLE);
                saveAndSyncItem(oldItem);
            }
        }

        ReadableItem newItem = items.findById(updated.getReadableItemId());
        if (newItem != null) {
            if (updated.getStatus() == Reservation.Status.ACTIVE) {
                newItem.setStatus(ReadableItem.Status.RESERVED);
            } else if (newItem.getStatus() == ReadableItem.Status.RESERVED) {
                newItem.setStatus(ReadableItem.Status.AVAILABLE);
            }
            saveAndSyncItem(newItem);
        }

        Reservation saved = reservations.save(updated);

        if (oldMemberId != null && !oldMemberId.equals(saved.getMemberId())) {
            Member oldMember = members.findById(oldMemberId);
            if (oldMember != null) {
                oldMember.getReservations().removeIf(r -> saved.getId().equals(r.getId()));
                members.save(oldMember);
            }
        }
        syncMemberReservation(members.findById(saved.getMemberId()), saved);
        return saved;
    }

    public Reservation cancelReservation(String id) {
        Reservation reservation = reservations.findById(id);
        if (reservation == null || !reservation.isActive()) {
            return null;
        }

        reservation.setStatus(Reservation.Status.CANCELLED);
        Reservation saved = reservations.save(reservation);

        ReadableItem item = items.findById(saved.getReadableItemId());
        if (item != null) {
            item.setStatus(ReadableItem.Status.AVAILABLE);
            saveAndSyncItem(item);
        }
        syncMemberReservation(members.findById(saved.getMemberId()), saved);
        return saved;
    }

    public boolean deleteReservation(String id) {
        Reservation reservation = reservations.findById(id);
        if (reservation == null) {
            return false;
        }
        if (reservation.isActive()) {
            ReadableItem item = items.findById(reservation.getReadableItemId());
            if (item != null) {
                item.setStatus(ReadableItem.Status.AVAILABLE);
                saveAndSyncItem(item);
            }
        }
        Member member = members.findById(reservation.getMemberId());
        if (member != null) {
            member.getReservations().removeIf(r -> id.equals(r.getId()));
            members.save(member);
        }
        for (Loan loan : loans.findAll()) {
            if (loan.getReservations().removeIf(r -> id.equals(r.getId()))) {
                loans.save(loan);
            }
        }
        return reservations.delete(id);
    }

    public List<Reservation> findAllReservations() {
        return reservations.findAll();
    }

    public Reservation findReservation(String id) {
        return reservations.findById(id);
    }

    public Loan borrow(String memberId, List<String> itemIds, LocalDate dueDate) {
        Member member = members.findById(memberId);
        if (member == null || itemIds == null || itemIds.isEmpty()) {
            return null;
        }

        List<ReadableItem> borrowedItems = new ArrayList<>();
        for (String itemId : itemIds) {
            ReadableItem item = items.findById(itemId);
            if (item != null && item.isAvailable()) {
                item.setStatus(ReadableItem.Status.BORROWED);
                borrowedItems.add(item);
            }
        }

        Loan loan = new Loan(null, memberId, LocalDate.now(), dueDate);
        loan.getItems().addAll(borrowedItems);
        Loan savedLoan = loans.save(loan);
        member.getLoans().add(savedLoan);
        return savedLoan;
    }

    public Loan returnLoan(String id) {
        Loan loan = loans.findById(id);
        if (loan == null || loan.isReturned()) {
            return null;
        }

        loan.setReturnDate(LocalDate.now());
        for (ReadableItem item : loan.getItems()) {
            item.setStatus(ReadableItem.Status.AVAILABLE);
        }
        return loan;
    }

    public boolean deleteLoan(String id) {
        Loan loan = loans.findById(id);
        if (loan == null) {
            return false;
        }
        if (!loan.getReservations().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete loan '" + loan.getId()
                            + "' because it still has linked reservations. Delete child reservations first.");
        }
        if (!loan.isReturned()) {
            for (ReadableItem loanItem : loan.getItems()) {
                ReadableItem item = items.findById(loanItem.getId());
                if (item != null) {
                    item.setStatus(ReadableItem.Status.AVAILABLE);
                    saveAndSyncItem(item);
                }
            }
        }
        Member member = members.findById(loan.getMemberId());
        if (member != null) {
            member.getLoans().removeIf(l -> id.equals(l.getId()));
            members.save(member);
        }
        return loans.delete(id);
    }

    public List<Loan> findAllLoans() {
        return loans.findAll();
    }

    public Loan findLoan(String id) {
        return loans.findById(id);
    }

    public List<Loan> findLoansByMember(String memberId) {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans.findAll()) {
            if (memberId != null && memberId.equals(loan.getMemberId())) {
                result.add(loan);
            }
        }
        return result;
    }

    public List<Loan> findOverdueLoans() {
        LocalDate today = LocalDate.now();
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans.findAll()) {
            if (loan.isOverdue(today)) {
                result.add(loan);
            }
        }
        return result;
    }
}
