package api.activity.activityrecognition.email;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

import api.activity.activityrecognition.MainActivity;
import api.activity.activityrecognition.R;
import api.activity.activityrecognition.fragments.dialog.WarningDialog;
import api.activity.activityrecognition.utils.Constants;

public class AutomaticEmailSender {

    private static final String TAG = "EmailSender";

    public static void sendEmail(final Context context, final String username, final String password, boolean wait) {
        class SendEmailAsyncTask extends AsyncTask<Void, Integer, String> {

            private String host = "smtp.gmail.com";
            final Activity activity = (MainActivity)context;

            @Override
            protected String doInBackground(Void... params) {

                ConnectivityManager connManager =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

                if(activeNetwork == null) {
                    Log.e(TAG, "No network found");
                    WarningDialog.newInstance(context.getString(R.string.error_no_network_available_ES))
                            .show(activity.getFragmentManager(), "network error");
                    return null;
                }

                    /*use only WiFi*/
                if (activeNetwork.getType() != ConnectivityManager.TYPE_WIFI) {
                    Log.e(TAG, "WiFi is not turned on");
                    WarningDialog.newInstance(context.getString(R.string.error_not_connected_wifi_ES))
                            .show(activity.getFragmentManager(), "no WiFi");
                    return null;
                }

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

                    /* settings the body of the email*/
                    Multipart multipart = new MimeMultipart();

                    /* body text */
                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    String file = context.getFilesDir() + File.separator + context.getString(R.string.activity_log_filename);
                    String fileName = context.getString(R.string.activity_log_filename);
                    String body = String.format(
                            context.getString(R.string.email_body),
                            context.getString(R.string.app_name));
                    DataSource source = new FileDataSource(file);

                    messageBodyPart.setText(body);
                    messageBodyPart.setContent(body, "text/html");
                    multipart.addBodyPart(messageBodyPart);

                    /* attachments */
                    messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(fileName + ".txt");
                    multipart.addBodyPart(messageBodyPart);


                    message.setContent(multipart);

                    /* sending the email via authenticated transport */
                    Transport transport = session.getTransport("smtps");
                    transport.connect(host, username, password);
                    transport.sendMessage(message, message.getAllRecipients());
                    transport.close();

                    /* recreating the log file after sending it */
                    File temp = new File(file);
                    temp.delete();
                    temp.createNewFile();

                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        if(wait) {
            Log.d(TAG, "Waiting for task");
            try {
                new SendEmailAsyncTask().execute().get(Constants.TASK_WAITING_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        else
            new SendEmailAsyncTask().execute();
    }
}