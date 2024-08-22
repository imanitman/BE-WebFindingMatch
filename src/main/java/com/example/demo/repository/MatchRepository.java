package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.Match;
import java.util.List;


@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    Match save(Match match);
    Match findById(long id);
    void  deleteById(long id);
}
