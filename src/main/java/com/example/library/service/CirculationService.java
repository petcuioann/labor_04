package com.example.library.service;

import com.example.library.model.Loan;
import com.example.library.model.Member;
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

    public CirculationService(
            CrudRepository<Loan> loans,
            CrudRepository<Reservation> reservations,
            CrudRepository<Member> members,
            CrudRepository<ReadableItem> items) {
        this.loans = loans;
        this.reservations = reservations;
        this.members = members;
        this.items = items;
    }

    public Reservation reserve(String memberId, String itemId) {
        Member member = members.findById(memberId);
        ReadableItem item = items.findById(itemId);
        if (member == null || item == null || !item.isAvailable()) {
            return null;
        }

        item.setStatus(ReadableItem.Status.RESERVED);
        Reservation reservation = new Reservation(null, memberId, itemId, LocalDate.now());
        Reservation savedReservation = reservations.save(reservation);
        member.getReservations().add(savedReservation);
        return savedReservation;
    }

    public Reservation cancelReservation(String id) {
        Reservation reservation = reservations.findById(id);
        if (reservation == null || !reservation.isActive()) {
            return null;
        }

        reservation.setStatus(Reservation.Status.CANCELLED);
        ReadableItem item = items.findById(reservation.getReadableItemId());
        if (item != null) {
            item.setStatus(ReadableItem.Status.AVAILABLE);
        }
        return reservation;
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
            }
        }
        Member member = members.findById(reservation.getMemberId());
        if (member != null) {
            member.getReservations().remove(reservation);
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
        if (!loan.isReturned()) {
            for (ReadableItem item : loan.getItems()) {
                item.setStatus(ReadableItem.Status.AVAILABLE);
            }
        }
        Member member = members.findById(loan.getMemberId());
        if (member != null) {
            member.getLoans().remove(loan);
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
