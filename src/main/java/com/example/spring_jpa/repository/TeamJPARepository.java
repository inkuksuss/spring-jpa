package com.example.spring_jpa.repository;

import com.example.spring_jpa.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TeamJPARepository {

    @PersistenceContext
    private EntityManager em;

    public Team save(Team team) {
        em.persist(team);
        return team;
    }

    public void delete(Team team) {
        em.remove(team);
    }

    public List<Team> findAll() {
        return em.createQuery("SELECT t FROM Team t", Team.class)
                .getResultList();
    }

    public long count() {
        return em.createQuery("SELECT COUNT(t) FROM Team t", Long.class)
                .getSingleResult();
    }

    public Optional<Team> findOptById(Long id) {
        return Optional.ofNullable(em.find(Team.class, id));
    }

    public Team findById(Long id) {
        return em.find(Team.class, id);
    }
}
