package com.example.spring_jpa.repository;

import com.example.spring_jpa.entity.Member;
import jakarta.persistence.EntityManager;

import java.util.List;

public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final EntityManager em;

    public MemberQueryRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Member> findByAge(int age) {
        return em.createQuery("SELECT m FROM Member m WHERE m.age = :age", Member.class)
                .setParameter("age", age)
                .getResultList();
    }
}
