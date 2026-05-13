package com.example.spring_jpa.repository;

import com.example.spring_jpa.entity.Member;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJPARepositoryTest {

    @Autowired
    private MemberJPARepository memberJPARepository;

    @Test
    public void basicCrud() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJPARepository.save(member1);
        memberJPARepository.save(member2);

        Member find1 = memberJPARepository.findOptById(member1.getId()).get();
        Member find2 = memberJPARepository.findOptById(member2.getId()).get();
        assertThat(find1).isEqualTo(member1);
        assertThat(find2).isEqualTo(member2);

        List<Member> result = memberJPARepository.findAll();
        assertThat(result.size()).isEqualTo(2);

        long count = memberJPARepository.count();
        assertThat(count).isEqualTo(2);

        memberJPARepository.delete(member1);
        memberJPARepository.delete(member2);

        assertThat(memberJPARepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void NamedQuery() {
        Member member1 = new Member("member1");
        memberJPARepository.save(member1);

        List<Member> members = memberJPARepository.findByUsername("member1");
        assertThat(members).hasSize(1);
        assertThat(members.get(0).getUsername()).isEqualTo("member1");
    }

    @Test
    public void paging() {
        memberJPARepository.save(new Member("1", 10));
        memberJPARepository.save(new Member("2", 10));
        memberJPARepository.save(new Member("3", 10));
        memberJPARepository.save(new Member("4", 10));
        memberJPARepository.save(new Member("5", 10));
        memberJPARepository.save(new Member("6", 10));
        memberJPARepository.save(new Member("7", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        List<Member> members = memberJPARepository.findByPage(age, offset, limit);
        long cnt = memberJPARepository.totalCount(age);

        assertThat(members).hasSize(3);
        assertThat(cnt).isEqualTo(7);
    }

    @Test
    public void bulkUpdate() {
        memberJPARepository.save(new Member("1", 10));
        memberJPARepository.save(new Member("2", 20));
        memberJPARepository.save(new Member("3", 20));
        memberJPARepository.save(new Member("4", 20));
        memberJPARepository.save(new Member("5", 20));

        int result = memberJPARepository.bulkAgePlus(20);

        assertThat(result).isEqualTo(4);
    }
}