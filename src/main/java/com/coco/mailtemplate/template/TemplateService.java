package com.coco.mailtemplate.template;

public interface TemplateService {

	Iterable<Template> listAllTemplates();

	Template getTemplateById(Integer id);

	Template saveTemplate(Template entity);

	void deleteTemplate(Integer id);

	Template getTemplateByName(String name);
}