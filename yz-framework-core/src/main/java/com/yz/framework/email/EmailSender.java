package com.yz.framework.email;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

import com.yz.framework.logging.Logger;

public class EmailSender {
	private static final Logger LOGGER = Logger.getLogger(EmailSender.class);
	private static final String EMAIL_ADDRESS_DILIMETER = ";";

	private String userName;
	private String password;
	private String smtpHost;
	private int port;
	private boolean useSSL;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isUseSSL() {
		return useSSL;
	}

	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}

	public void sendEmail(String from, String to, String subject, String body, List<String> attachments) throws Exception {
		sendEmail(from, to, null, null, subject, body, true, attachments, null);
	}

	public void sendEmail(String from, String to, String subject, String body) throws Exception {
		sendEmail(from, to, null, null, subject, body);
	}

	public void sendEmail(String from, String to, String cc, String bcc, String subject, String body) throws Exception {
		sendEmail(from, to, cc, bcc, subject, body, false, null, null);
	}

	public void sendEmail(String from, String to, String cc, String bcc, String subject, String body, boolean isHtml, List<String> attachements, List<String> inlines) throws Exception {

		doSend(from, to, cc, bcc, subject, body, isHtml, attachements, inlines);

	}

	ExecutorService executorService = Executors.newFixedThreadPool(20);

	public void asynSendEmail(final String from, final String to, final String cc, final String bcc, final String subject, final String body, final boolean isHtml, final List<String> attachements,
			final List<String> inlines) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					doSend(from, to, cc, bcc, subject, body, isHtml, attachements, inlines);
				} catch (MessagingException e) {
					LOGGER.error("asynSendEmail", "发送邮件失败", e);
				}
			}
		});
	}

	private void doSend(String from, String to, String cc, String bcc, String subject, String body, boolean isHtml, List<String> attachements, List<String> inlines) throws MessagingException {
		JavaMailSender mailSender = getEmailSender();
		MimeMessage mailMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = null;
		if (attachements != null && attachements.size() > 0) {
			helper = new MimeMessageHelper(mailMessage, true, "utf-8");
			for (String attachment : attachements) {
				File file = new File(attachment);
				helper.addAttachment(file.getName(), file);
			}
		}
		if (inlines != null && inlines.size() > 0) {
			if (helper == null || !helper.isMultipart()) {
				helper = new MimeMessageHelper(mailMessage, true, "utf-8");
			}
			for (String inline : inlines) {
				File file = new File(inline);
				helper.addInline(file.getName(), file);
			}
		}
		if (helper == null) {
			helper = new MimeMessageHelper(mailMessage, "utf-8");
		}
		helper.setFrom(from);
		helper.setTo(to.split(EMAIL_ADDRESS_DILIMETER));

		if (StringUtils.hasText(cc)) {
			helper.setCc(cc.split(EMAIL_ADDRESS_DILIMETER));
		}
		if (StringUtils.hasText(bcc)) {
			helper.setCc(bcc.split(EMAIL_ADDRESS_DILIMETER));
		}
		helper.setSubject(subject);
		helper.setText(body, isHtml);
		mailSender.send(mailMessage);
	}

	private JavaMailSender getEmailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(smtpHost);
		mailSender.setUsername(userName);
		mailSender.setPassword(password);
		mailSender.setPort(port);
		if (useSSL) {
			mailSender.setProtocol("smtps");
		}
		return mailSender;
	}

	public void asynSendEmail(String from, String to, String subject, String body) {
		asynSendEmail(from, to, null, null, subject, body, false, null, null);
	}
}
