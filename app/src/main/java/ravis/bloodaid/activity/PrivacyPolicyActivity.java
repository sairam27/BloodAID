package ravis.bloodaid.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import ravis.bloodaid.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        TextView text = findViewById(R.id.textView2);
        text.setText("This Privacy Policy governs the manner in which Blooaid collects, uses, maintains and discloses information collected from users (each, a \"User\") of the http://www.bloodaid.3eeweb.com/ website (\"Site\").\n" +
                "\n" +
                "Personal identification information\n" +
                "\n" +
                "We may collect personal identification information from Users in a variety of ways, including, but not limited to, when Users visit our site, register on the site, and in connection with other activities, services, features or resources we make available on our Site. Users may be asked for, as appropriate, name, email address, mailing address, phone number. Users may, however, visit our Site anonymously. We will collect personal identification information from Users only if they voluntarily submit such information to us. Users can always refuse to supply personally identification information, except that it may prevent them from engaging in certain Site related activities.\n" +
                "\n" +
                "Non-personal identification information\n" +
                "\n" +
                "We may collect non-personal identification information about Users whenever they interact with our Site. Non-personal identification information may include the browser name, the type of computer and technical information about Users means of connection to our Site, such as the operating system and the Internet service providers utilized and other similar information.\n" +
                "\n" +
                "Web browser cookies\n" +
                "\n" +
                "Our Site may use \"cookies\" to enhance User experience. User's web browser places cookies on their hard drive for record-keeping purposes and sometimes to track information about them. User may choose to set their web browser to refuse cookies, or to alert you when cookies are being sent. If they do so, note that some parts of the Site may not function properly.\n" +
                "\n" +
                "How we use collected information\n" +
                "\n" +
                "Blooaid may collect and use Users personal information for the following purposes:\n" +
                " - To run and operate our Site\n" +
                "We may need your information display content on the Site correctly.\n" +
                " - To improve customer service\n" +
                "Information you provide helps us respond to your customer service requests and support needs more efficiently.\n" +
                " - To personalize user experience\n" +
                "We may use information in the aggregate to understand how our Users as a group use the services and resources provided on our Site.\n" +
                " - To improve our Site\n" +
                "We may use feedback you provide to improve our products and services.\n" +
                " - To send periodic emails\n" +
                "We may use the email address to send User information and updates pertaining to their order. It may also be used to respond to their inquiries, questions, and/or other requests. \n" +
                "\n" +
                "How we protect your information\n" +
                "\n" +
                "We adopt appropriate data collection, storage and processing practices and security measures to protect against unauthorized access, alteration, disclosure or destruction of your personal information, username, password, transaction information and data stored on our Site.\n" +
                "\n" +
                "Sharing your personal information\n" +
                "\n" +
                "We do not sell, trade, or rent Users personal identification information to others. We may share generic aggregated demographic information not linked to any personal identification information regarding visitors and users with our business partners, trusted affiliates and advertisers for the purposes outlined above. \n" +
                "\n" +
                "Compliance with children's online privacy protection act\n" +
                "\n" +
                "Protecting the privacy of the very young is especially important. For that reason, we never collect or maintain information at our Site from those we actually know are under 13, and no part of our website is structured to attract anyone under 13.\n" +
                "\n" +
                "Changes to this privacy policy\n" +
                "\n" +
                "Blooaid has the discretion to update this privacy policy at any time. When we do, we will post a notification on the main page of our Site, revise the updated date at the bottom of this page and send you an email. We encourage Users to frequently check this page for any changes to stay informed about how we are helping to protect the personal information we collect. You acknowledge and agree that it is your responsibility to review this privacy policy periodically and become aware of modifications.\n" +
                "\n" +
                "Your acceptance of these terms\n" +
                "\n" +
                "By using this Site, you signify your acceptance of this policy. If you do not agree to this policy, please do not use our Site. Your continued use of the Site following the posting of changes to this policy will be deemed your acceptance of those changes. This policy was generated using www.privacypolicies.com\n" +
                "\n" +
                "Contacting us\n" +
                "\n" +
                "If you have any questions about this Privacy Policy, the practices of this site, or your dealings with this site, please contact us.\n" +
                "\n" +
                "This document was last updated on September 25, 2016");
        text.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
