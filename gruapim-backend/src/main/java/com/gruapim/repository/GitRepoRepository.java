package com.gruapim.repository;

import com.gruapim.domain.entity.GitRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GitRepoRepository extends JpaRepository<GitRepository, UUID> {

    List<GitRepository> findByProjectId(UUID projectId);

    Optional<GitRepository> findByProjectIdAndRepositoryUrl(UUID projectId, String repositoryUrl);

    boolean existsByProjectIdAndRepositoryUrl(UUID projectId, String repositoryUrl);
}
