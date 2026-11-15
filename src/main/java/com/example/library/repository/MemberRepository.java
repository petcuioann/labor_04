package com.example.library.repository;

import com.example.library.model.Member;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository extends InFileRepository<Member> {
    public MemberRepository() {
        super("src/main/resources/data/members.json", Member.class);
    }
}
