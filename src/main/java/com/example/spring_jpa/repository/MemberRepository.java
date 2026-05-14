package com.example.spring_jpa.repository;

import com.example.spring_jpa.dto.MemberDto;
import com.example.spring_jpa.entity.Member;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository, JpaSpecificationExecutor<Member> {

//    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("SELECT m FROM Member m WHERE m.username = :username AND m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("SELECT m.username FROM Member m")
    List<String> findUsernameList();

    @Query("SELECT new com.example.spring_jpa.dto.MemberDto(m.id, m.username, t.name) FROM Member m JOIN m.team t")
    List<MemberDto> findMemberDtoList();

    @Query("SELECT m FROM Member m WHERE m.username IN :names")
    List<Member> findByNames(@Param("names") List<String> names);

//    @Query(value = "SELECT m FROM Member m WHERE m.age = :age",
//            countQuery = "SELECT COUNT(*) FROM Member m WHERE m.age = :age")
    Page<Member> findPageByAge(int age, Pageable pageable);

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Member m SET m.age = m.age + 1 WHERE m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.team")
    List<Member> findMemberFetchJoin();

    @Override @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("SELECT m FROM Member m")
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findMemberEntityGraphByUsername(@Param("username") String username);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.team WHERE m.team.name = :name")
    List<Member> findMemberEntityGraphByTeamName(@Param("name") String teamName);

    @EntityGraph("Member.all")
    List<Member> findNamedEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(@Param("username") String username);


    @Lock(LockModeType.PESSIMISTIC_READ)
    List<Member> findLockByUsername(@Param("username") String username);

    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);

    <T> List<T> findProjectionsClassByUsername(@Param("username") String username, Class<T> clazz);

    @Query(value = "SELECT * FROM Member WHERE username = ?", nativeQuery = true)
    Optional<Member> findNativeQuery(String username);

    @Query(value = "SELECT m.member_id as id, m.username, t.name as teamName FROM MEMBER m LEFT JOIN TEAM t ON m.team_id = t.team_id",
            nativeQuery = true,
    countQuery = "SELECT COUNT(*) FROM MEMBER")
    Page<MemberProjection> findNativeProjection(Pageable pageable);
}
