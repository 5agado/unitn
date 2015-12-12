package utilities;

import db.DBManager;
import db.beans.Bid;
import db.beans.Product;
import db.beans.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import utilities.mail.Mail;
import utilities.mail.MailManager;

public class AuxiliaryMethods {

    public static void updateExpired(DBManager m, Product p) throws ServletException {
        try {
            if (!p.getExpired()) {
                String ris = m.updateExpiredAuction(p); 
                if (ris != null) {
                    p.setExpired(true);
                    if (ris!=DBManager.NO_WINNER) {
                        sendMailsToBidders(m, p, false);
                    } else {
                        sendMailsToBidders(m, p, true);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new ServletException(ex.getMessage());
        }
    }

    public static void sendMailBeatAuction(User bidder, Product prod) {
        String prodDesc = "Description: " + prod.getDescription() + "\n"
                + "Quantity: " + prod.getQuantity() + "\n"
                + "New price: " + prod.getPrice() + "\n"
                + "DeliveryPrice: " + prod.getDeliveryPrice();
        List<String> recps = new ArrayList<String>();
        String subject = "Beat offer";
        String message = "Sorry, " + bidder.getUsername() + "!!!\nanother user beat your offer for the product\n" + prodDesc;
        String destEmail = bidder.getEmail();
        recps.add(destEmail);
        Mail mail = new Mail();
        mail.setSubject(subject);
        mail.setText(message);
        mail.setRecipients(recps);
        List<Mail> mails = new ArrayList<Mail>();
        mails.add(mail);
        MailManager.sendMails(mails);

    }

    public static void sendMailsToBidders(DBManager m, Product p, boolean cancelled) throws SQLException {
        List<String> recps;
        String prod = "Description: " + p.getDescription() + "\n"
                + "Quantity: " + p.getQuantity() + "\n"
                + "Price: " + p.getPrice() + "\n"
                + "DeliveryPrice: " + p.getDeliveryPrice();
        List<Bid> bids = m.getBidsByProduct(p.getIdProd());

        if (bids.isEmpty()) {
            return;
        }

        List<Mail> mails = new ArrayList<Mail>();


        String subject;
        String message;
        String destEmail = null;

        Set<User> alreadySent = new HashSet<User>();

        if (!cancelled) {
            subject = "You have won the auction";
            message = "Congratulations " + bids.get(0).getBidder().getUsername() + "!!!\nYou have won the auction for the product:\n" + prod;
            destEmail = bids.get(0).getBidder().getEmail();
            String username = bids.get(0).getBidder().getUsername();
            recps = new ArrayList<String>();
            recps.add(destEmail);
            Mail mail = new Mail();
            mail.setSubject(subject);
            mail.setText(message);
            mail.setRecipients(recps);
            mails.add(mail);
            MailManager.sendMails(mails);
            alreadySent.add(bids.get(0).getBidder());
        }

        mails.clear();
        subject = "You have lost the auction";
        User bidder;
        for (Bid b : bids) {
            bidder = b.getBidder();
            if (!alreadySent.contains(bidder)) {
                destEmail = bidder.getEmail();
                message = "Sorry " + bidder.getUsername() + ", but you have lost the auction for the product:\n" + prod;
                recps = new ArrayList<String>();
                recps.add(destEmail);
                Mail mail = new Mail();
                mail.setSubject(subject);
                mail.setText(message);
                mail.setRecipients(recps);
                mails.add(mail);
                alreadySent.add(bidder);
            }
        }

        MailManager.sendMails(mails);
    }

    public static List<Bid> getUpdatedList(DBManager m, Product p) throws SQLException {
        List<Bid> bids = m.getBidsByProduct(p.getIdProd());
        if (bids.size() == 1) {
            bids.get(0).setBid(p.getInitPrice() + p.getMinIncrement());
        } else if (bids.size() > 1) {
            double price = Math.min(bids.get(1).getBid() + p.getMinIncrement(), bids.get(0).getBid());
            bids.get(0).setBid(price);
        }
        return bids;
    }
}
