package com.gruapim.repository;

import com.gruapim.domain.entity.GitCommitLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GitCommitLinkRepository extends JpaRepository<GitCommitLink, UUID> {

    List<GitCommitLink> findByTaskId(UUID taskId);

    List<GitCommitLink> findByRepositoryId(UUID repositoryId);

    Optional<GitCommitLink> findByTaskIdAndCommitHash(UUID taskId, String commitHash);

    boolean existsByTaskIdAndCommitHash(UUID taskId, String commitHash);
}
