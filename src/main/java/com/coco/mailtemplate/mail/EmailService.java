package com.coco.mailtemplate.mail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.coco.mailtemplate.template.Template;
import com.coco.mailtemplate.template.TemplateService;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private TemplateService templateService;

	public void sendSimpleMessage(Mail mail) throws MessagingException, IOException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
				StandardCharsets.UTF_8.name());

		Template template = templateService.getTemplateByName(mail.getTemplateName());
		if (template == null) {
			return;
		}

		helper.setTo(mail.getTo());
		helper.setText(getTemplate(template.getTemplate(), mail.getParams()), true);
		helper.setSubject(mail.getSubject());

		emailSender.send(message);
	}

	private String getTemplate(String template, Map<String, Object> map) {

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			template = replace(template, entry.getKey(), (String) entry.getValue());
		}

		return template;
	}

	private String replace(String template, String key, String value) {
		return template.replace("${" + key + "}", value);
	}

}
