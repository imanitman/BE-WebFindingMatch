package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.Match;
import com.example.demo.domain.User;

import java.util.List;


@Repository
public interface MatchRepository extends JpaRepository<Match, Long>, JpaSpecificationExecutor<Match>{
    Match save(Match match);
    Match findById(long id);
    void  deleteById(long id);
    List<Match> findAll();
    Page<Match> findByType(String type, Pageable pageable);
    List<Match> findByUser(User user);
}
