package com.mailtemplate.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TemplateRepository templateRepository;

	@Override
	public Iterable<Template> listAllTemplates() {
		logger.debug("listAllTemplates called");
		return templateRepository.findAll();
	}

	@Override
	public Template getTemplateById(Integer id) {
		logger.debug("getTemplateById called");
		return templateRepository.findById(id).orElse(null);
	}

	@Override
	public Template saveTemplate(Template entity) {
		logger.debug("saveTemplate called");
		return templateRepository.save(entity);
	}

	@Override
	public void deleteTemplate(Integer id) {
		logger.debug("deleteTemplate called");
		templateRepository.deleteById(id);
	}

	@Override
	public Template getTemplateByName(String name) {
		return templateRepository.findByTemplateName(name);
	}
}