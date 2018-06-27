package com.coco.mailtemplate.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/template")
@Api(value = "Template")
public class TemplateController {

	@Autowired
	private TemplateService templateService;

	@ApiOperation(value = "View a list of available template", response = Iterable.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
	public Iterable<Template> list() {
		return templateService.listAllTemplates();
	}

	@ApiOperation(value = "Search a product with an ID", response = Template.class)
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = "application/json")
	public Template getTemplate(@PathVariable Integer id) {
		return templateService.getTemplateById(id);
	}

	@ApiOperation(value = "Add a template")
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Template> saveTemplate(@RequestBody Template template) {
		template = templateService.saveTemplate(template);
		return new ResponseEntity<Template>(template, HttpStatus.OK);
	}

	@ApiOperation(value = "Update a template")
	@RequestMapping(value = "/update/{id}", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<Template> updateTemplate(@PathVariable Integer id, @RequestBody Template template) {

		template = templateService.saveTemplate(template);
		return new ResponseEntity<Template>(template, HttpStatus.OK);
	}

	@ApiOperation(value = "Delete a template")
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		templateService.deleteTemplate(id);
		return new ResponseEntity<String>("Template deleted successfully", HttpStatus.OK);

	}

}