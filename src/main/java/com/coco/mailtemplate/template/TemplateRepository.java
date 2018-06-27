package com.coco.mailtemplate.template;

import org.springframework.data.repository.CrudRepository;

public interface TemplateRepository extends CrudRepository<Template, Integer> {

	Template findByTemplateName(String templateName);

}