package com.example.spring_jpa.entity;

import com.example.spring_jpa.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("1", 10, teamA);
        Member member2 = new Member("2", 10, teamA);
        Member member3 = new Member("3", 10, teamB);
        Member member4 = new Member("4", 10, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("SELECT M FROM Member M", Member.class)
                .getResultList();
        members.stream().forEach(member -> {
            System.out.println(member);
            System.out.println(member.getTeam());
        });
    }

    @Test
    public void JpaEventBaseEntity() throws InterruptedException {
        Member member = new Member("member1");
        memberRepository.save(member);

        Thread.sleep(100);
        member.setUsername("member2");

        em.flush();
        em.clear();

        Member find = memberRepository.findById(member.getId()).get();
        log.info("create = {}", find.getCreatedDate());
        log.info("update = {}", find.getLastModifiedDate());
        log.info("createBy = {}", find.getCreateBy());
        log.info("lastModifiedBy = {}", find.getLastModifiedBy());
    }
}