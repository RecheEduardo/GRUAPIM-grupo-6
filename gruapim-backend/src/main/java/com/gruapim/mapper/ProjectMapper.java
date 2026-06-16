package com.gruapim.mapper;

import com.gruapim.domain.entity.Project;
import com.gruapim.domain.entity.ProjectMember;
import com.gruapim.dto.response.ProjectMemberResponse;
import com.gruapim.dto.response.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.name")
    ProjectResponse toResponse(Project project);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "userEmail", source = "user.email")
    ProjectMemberResponse toMemberResponse(ProjectMember member);
}
