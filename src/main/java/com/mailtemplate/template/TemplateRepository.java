package com.mailtemplate.template;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface TemplateRepository extends CrudRepository<Template, Integer> {

	Template findByTemplateName(String templateName);

}