package com.coco.mailtemplate.mail;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/mail")
@Api(value = "Mail")
public class MailController {

	@Autowired
	private EmailService emailSerivce;

	@ApiOperation(value = "send email")
	@RequestMapping(value = "send", method = RequestMethod.POST, produces = "application/json")
	public void getTemplate(@RequestBody Mail mail) {
		try {
			emailSerivce.sendSimpleMessage(mail);
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
		}
	}
}
