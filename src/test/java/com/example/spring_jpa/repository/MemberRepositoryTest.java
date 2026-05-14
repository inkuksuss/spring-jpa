package com.example.spring_jpa.repository;

import com.example.spring_jpa.dto.MemberDto;
import com.example.spring_jpa.entity.Member;
import com.example.spring_jpa.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;
    @PersistenceContext
    private EntityManager em;

    @Test
    public void basicCrud() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member find1 = memberRepository.findById(member1.getId()).get();
        Member find2 = memberRepository.findById(member2.getId()).get();
        assertThat(find1).isEqualTo(member1);
        assertThat(find2).isEqualTo(member2);

        List<Member> result = memberRepository.findAll();
        assertThat(result.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void NamedQuery() {
        Member member1 = new Member("member1");
        memberRepository.save(member1);

        List<Member> members = memberRepository.findByUsername("member1");
        assertThat(members).hasSize(1);
        assertThat(members.get(0).getUsername()).isEqualTo("member1");
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 10);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
    }

    @Test
    public void findUsernameList() {
        Member aaa = new Member("AAA", 10);
        Member bbb = new Member("BBB", 10);

        memberRepository.save(aaa);
        memberRepository.save(bbb);

        List<String> result = memberRepository.findUsernameList();
        assertThat(result).containsExactly("AAA", "BBB");
    }

    @Test
    public void findDtoList() {
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member aaa = new Member("AAA", 10);
        aaa.changeTeam(teamA);
        memberRepository.save(aaa);

        List<MemberDto> memberDtoList = memberRepository.findMemberDtoList();
        assertThat(memberDtoList).hasSize(1);
    }

    @Test
    public void findByNames() {
        Member aaa = new Member("AAA", 10);
        Member bbb = new Member("BBB", 10);

        memberRepository.save(aaa);
        memberRepository.save(bbb);

        List<Member> byNames = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        assertThat(byNames).hasSize(2);
        assertThat(byNames)
                .extracting("username")
                .containsExactly("AAA", "BBB");
    }

    @Test
    public void paging() {
        memberRepository.save(new Member("1", 10));
        memberRepository.save(new Member("2", 10));
        memberRepository.save(new Member("3", 10));
        memberRepository.save(new Member("4", 10));
        memberRepository.save(new Member("5", 10));
        memberRepository.save(new Member("6", 10));
        memberRepository.save(new Member("7", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");
        Page<Member> page = memberRepository.findPageByAge(10, pageRequest);

        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(7);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void slicing() {
        memberRepository.save(new Member("1", 10));
        memberRepository.save(new Member("2", 10));
        memberRepository.save(new Member("3", 10));
        memberRepository.save(new Member("4", 10));
        memberRepository.save(new Member("5", 10));
        memberRepository.save(new Member("6", 10));
        memberRepository.save(new Member("7", 10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");
        Slice<Member> page = memberRepository.findSliceByAge(10, pageRequest);

        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("1", 10));
        memberRepository.save(new Member("2", 20));
        memberRepository.save(new Member("3", 20));
        memberRepository.save(new Member("4", 20));
        memberRepository.save(new Member("5", 20));

        memberRepository.bulkAgePlus(20);
//        em.flush();
//        em.clear();

        List<Member> members = memberRepository.findByNames(Arrays.asList("2", "3", "4", "5"));
        assertThat(members).extracting("age")
            .containsExactly(21, 21, 21, 21);
    }

    @Test
    public void findMemberLazy() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.saveAll(Arrays.asList(teamA, teamB));

        Member a = new Member("a", 10, teamA);
        Member b = new Member("b", 20, teamB);
        memberRepository.saveAll(Arrays.asList(a, b));

        em.flush();
        em.clear();

//        List<Member> members1 = memberRepository.findAll();
//        List<Member> members2 = memberRepository.findMemberEntityGraph();
//        List<Member> members3 = memberRepository.findMemberEntityGraphByUsername("a");
//        List<Member> members4 = memberRepository.findMemberEntityGraphByTeamName("teamA");
        List<Member> members5 = memberRepository.findNamedEntityGraphByUsername("a");

//        members1.stream().map(Member::getTeam).forEach(System.out::println);
//        members2.stream().map(Member::getTeam).forEach(System.out::println);
//        members3.stream().map(Member::getTeam).forEach(System.out::println);
//        members4.stream().map(Member::getTeam).forEach(System.out::println);
    }

    @Test
    public void queryHint() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

//        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername());
//        findMember.setUsername("member2");

        List<Member> lockByUsername = memberRepository.findLockByUsername(member1.getUsername());
    }

    @Test
    public void log() {
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 10);
        memberRepository.save(member1);

        log.info("hello");

//        memberRepository.findByUsername("member1");

//        List<Member> member2 = memberRepository.findUser("member1", 10);
        memberRepository.save(member2);

        log.info("hello2");
    }

    @Test
    public void queryAge() {
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 10);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByAge(10);
        assertThat(members.size()).isEqualTo(2);
    }

    @Test
    public void specBasic() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        Member members = memberRepository.findAll(spec).get(0);

        assertThat(members.getUsername()).isEqualTo("m1");
        assertThat(members.getTeam().getName()).isEqualTo("teamA");
    }

    @Test
    public void queryByExample() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");
        Example<Member> example = Example.of(member, matcher);

        List<Member> members = memberRepository.findAll(example);

        assertThat(members.size()).isEqualTo(1);
        assertThat(members.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void projections() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

//        List<UsernameOnly> find = memberRepository.findProjectionsByUsername("m1");
        List<NestedClosedProjections> find = memberRepository.findProjectionsClassByUsername("m1", NestedClosedProjections.class);
        for (NestedClosedProjections clazz : find) {
            System.out.println("usernameOnly = " + clazz.getUsername());
            System.out.println("usernameOnly = " + clazz.getTeam());
        }
    }

    @Test
    public void nativeQuery() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

//        Member result = memberRepository.findNativeQuery("m1").get();
        Page<MemberProjection> result = memberRepository.findNativeProjection(PageRequest.of(0, 10));
        for (MemberProjection memberProjection : result.getContent()) {
            System.out.println("username = " + memberProjection.getUsername());
            System.out.println("teamName = " + memberProjection.getTeamName());
        }
    }
}