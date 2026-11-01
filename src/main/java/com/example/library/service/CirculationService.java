package com.example.library.service;

import static com.example.library.service.CatalogService.require;

import com.example.library.model.*;
import com.example.library.repository.CrudRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CirculationService {
    private final CrudRepository<Loan> loans;
    private final CrudRepository<Reservation> reservations;
    private final CrudRepository<Member> members;
    private final CrudRepository<ReadableItem> items;
    private final Clock clock = Clock.systemDefaultZone();

    public CirculationService(CrudRepository<Loan> loans, CrudRepository<Reservation> reservations, CrudRepository<Member> members, CrudRepository<ReadableItem> items) {
        this.loans = loans; this.reservations = reservations; this.members = members; this.items = items;
    }

    public synchronized Reservation reserve(String memberId, String itemId) {
        Member member = require(members, memberId, "member");
        ReadableItem item = require(items, itemId, "item");
        if (!item.isAvailable()) throw new IllegalStateException("item is " + item.getStatus());
        item.setStatus(ReadableItem.Status.RESERVED);
        Reservation reservation = reservations.save(new Reservation(null, memberId, itemId, LocalDate.now(clock)));
        member.getReservations().add(reservation);
        return reservation;
    }
    public synchronized Reservation cancelReservation(String id) {
        Reservation reservation = require(reservations, id, "reservation");
        if (!reservation.isActive()) throw new IllegalStateException("reservation is " + reservation.getStatus());
        reservation.setStatus(Reservation.Status.CANCELLED);
        items.findById(reservation.getReadableItemId()).ifPresent(i -> i.setStatus(ReadableItem.Status.AVAILABLE));
        return reservation;
    }
    public List<Reservation> findAllReservations() { return reservations.findAll(); }
    public Optional<Reservation> findReservation(String id) { return reservations.findById(id); }

    public synchronized Loan borrow(String memberId, List<String> itemIds, LocalDate dueDate) {
        Member member = require(members, memberId, "member");
        LocalDate today = LocalDate.now(clock);
        if (itemIds == null || itemIds.isEmpty()) throw new IllegalArgumentException("no items given");
        if (dueDate == null || !dueDate.isAfter(today)) throw new IllegalArgumentException("due date must be in the future");
        List<ReadableItem> borrowed = itemIds.stream().distinct().map(id -> require(items, id, "item")).toList();
        List<Reservation> fulfilled = borrowed.stream().filter(i -> !i.isAvailable()).map(i -> activeReservation(memberId, i)).toList();
        Loan loan = new Loan(null, memberId, today, dueDate);
        borrowed.forEach(i -> i.setStatus(ReadableItem.Status.BORROWED));
        fulfilled.forEach(r -> r.setStatus(Reservation.Status.COMPLETED));
        loan.getItems().addAll(borrowed);
        loan.getReservations().addAll(fulfilled);
        member.getLoans().add(loans.save(loan));
        return loan;
    }
    public synchronized Loan returnLoan(String id) {
        Loan loan = require(loans, id, "loan");
        if (loan.isReturned()) throw new IllegalStateException("loan already returned");
        loan.setReturnDate(LocalDate.now(clock));
        loan.getItems().forEach(i -> i.setStatus(ReadableItem.Status.AVAILABLE));
        return loan;
    }
    public List<Loan> findAllLoans() { return loans.findAll(); }
    public Optional<Loan> findLoan(String id) { return loans.findById(id); }
    public List<Loan> findLoansByMember(String memberId) { return loans.findBy(l -> memberId.equals(l.getMemberId())); }
    public List<Loan> findOverdueLoans() { LocalDate today = LocalDate.now(clock); return loans.findBy(l -> l.isOverdue(today)); }

    private Reservation activeReservation(String memberId, ReadableItem item) {
        if (item.getStatus() != ReadableItem.Status.RESERVED) throw new IllegalStateException("item " + item.getId() + " is " + item.getStatus());
        return reservations.findBy(r -> r.isActive() && r.getReadableItemId().equals(item.getId()) && r.getMemberId().equals(memberId)).stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("item " + item.getId() + " is reserved by another member"));
    }
}
