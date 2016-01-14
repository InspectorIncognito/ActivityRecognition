package api.activity.activityrecognition.email;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import api.activity.activityrecognition.R;

public class AutomaticEmailer {

    public static void sendEmail(final Context context, final String username, final String password) {
        class SendEmailAsyncTask extends AsyncTask<Void, Integer, String> {

            private String host = "smtp.gmail.com";

            @Override
            protected String doInBackground(Void... params) {

                /* using gmail smtp service */
                Properties props = System.getProperties();
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", host);
                props.put("mail.smtp.user", username);
                props.put("mail.smtp.password", password);
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.debug", "true");

                Session session = Session.getInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                session.setDebug(true);

                try {

                    /* email info: sender, recipients, date, subject */
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(context.getString(R.string.email_recipient_address)));
                    message.setSentDate(new Date());
                    message.setSubject(context.getString(R.string.email_subject));
                    //message.setText(context.getString(R.string.email_body));

                    /* add attachment to the email */
                    Multipart multipart = new MimeMultipart();

                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    String file = context.getFilesDir() + File.separator + context.getString(R.string.activity_log_filename);
                    String fileName = context.getString(R.string.activity_log_filename);
                    String body = String.format(
                            context.getString(R.string.email_body),
                            context.getString(R.string.app_name));
                    DataSource source = new FileDataSource(file);

                    //messageBodyPart.setFileName(fileName + ".txt");
                    messageBodyPart.setText(body);
                    messageBodyPart.setContent(body, "text/html");
                    multipart.addBodyPart(messageBodyPart);

                    messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(fileName + ".txt");
                    multipart.addBodyPart(messageBodyPart);


                    message.setContent(multipart);

                    Transport transport = session.getTransport("smtps");
                    transport.connect(host, username, password);
                    transport.sendMessage(message, message.getAllRecipients());
                    transport.close();

                } catch (MessagingException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }
        new SendEmailAsyncTask().execute();
    }
}