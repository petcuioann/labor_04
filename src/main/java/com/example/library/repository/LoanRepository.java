package com.example.library.repository;

import com.example.library.model.Loan;
import org.springframework.stereotype.Repository;

@Repository
public class LoanRepository extends InFileRepository<Loan> {
    public LoanRepository() {
        super("src/main/resources/data/loans.json", Loan.class);
    }
}
