package api.activity.activityrecognition.email;

import android.os.AsyncTask;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by brahim on 13-01-16.
 */
public class AutomaticEmailer {

    public static AutomaticEmailer emailerInstance = null;

    private AutomaticEmailer(){}

    public static AutomaticEmailer getInstance() {
        if (emailerInstance == null) {
            emailerInstance = new AutomaticEmailer();
        }

        return emailerInstance;
    }


}
