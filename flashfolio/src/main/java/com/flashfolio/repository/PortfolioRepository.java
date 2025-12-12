package com.flashfolio.repository;

import com.flashfolio.entity.Portfolio;
import com.flashfolio.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findByGithubUrl(String githubUrl);
    List<Portfolio> findByUser(User user); // 사용자별 목록 조회
}