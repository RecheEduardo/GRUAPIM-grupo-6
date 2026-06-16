package com.gruapim.mapper;

import com.gruapim.domain.entity.GitCommitLink;
import com.gruapim.domain.entity.GitRepository;
import com.gruapim.dto.response.GitCommitLinkResponse;
import com.gruapim.dto.response.GitRepoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GitMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "connectedById", source = "connectedBy.id")
    GitRepoResponse toResponse(GitRepository repository);

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "repositoryId", source = "repository.id")
    GitCommitLinkResponse toCommitLinkResponse(GitCommitLink commitLink);
}
