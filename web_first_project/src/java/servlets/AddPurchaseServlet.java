package servlets;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import db.*;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import utilities.Constants;

public class AddPurchaseServlet extends HttpServlet {

    private DBManager manager;

    @Override
    public void init() throws ServletException {
        this.manager = (DBManager) super.getServletContext().getAttribute(Constants.DB_ATTRIBUTE_NAME);
    }

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String productId = request.getParameter(Constants.PRODUCT_ID_PARAM_NAME);

        try {
            Product prod = manager.getProductById(Integer.valueOf(productId));
            if (prod.isOnSale()) {
                User seller = manager.getUserById(prod.getSeller());
                User buyer = (User) (request.getSession().getAttribute(Constants.USER_ATTRIBUTE_NAME));

                Category cat = manager.getCategoryById(prod.getCategory());
                String pdfUrl = generatePdf(prod, seller, buyer, cat);

                Purchase purchase = new Purchase();
                purchase.setBuyer(buyer.getId());
                purchase.setProduct(prod.getId());
                purchase.setQuantity(prod.getQuantity());
                purchase.setUrlPdf(pdfUrl + ".pdf");

                manager.addPurchase(purchase);

            }
        } catch (SQLException ex) {
            Logger.getLogger(AddPurchaseServlet.class.getName()).log(Level.SEVERE, null, ex);
        }


        //redirezione alla landing page
        response.sendRedirect(Constants.SM_LANDING_PAGE_BUYER);
    }

    // genera il file pdf e ne restituisce il nome (senza path ne estensione)
    private String generatePdf(Product p, User seller, User buyer, Category cat) {
        // nome del pdf generato
        String ris = null;

        String path = getServletContext().getRealPath("/pdf/");
        try {
            Document document = new Document();

            ByteArrayOutputStream pdfByteStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, pdfByteStream);
            document.open();

            createPdfDocument(document, p, seller, buyer, cat);

            document.close();

            byte[] pdfBytes = pdfByteStream.toByteArray();

            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.reset();
                md.update(pdfBytes);
                byte[] resultByte = md.digest();
                StringBuilder sb = new StringBuilder();
                for (byte b : resultByte) {
                    sb.append(Integer.toHexString((int) (b & 0xff)));
                }
                ris = sb.toString();
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(AddPurchaseServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                FileOutputStream outFile = new FileOutputStream(path + "\\" + ris + ".pdf");
                pdfByteStream.writeTo(outFile);
                outFile.close();
            } catch (IOException ex) {
                Logger.getLogger(AddPurchaseServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (DocumentException ex) {
            Logger.getLogger(AddPurchaseServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ris;
    }

    // metodo ausiliario che si occupa della compilazione del documento pdf
    private void createPdfDocument(Document d, Product prod, User seller, User buyer, Category cat) throws DocumentException {
        d.setMargins(60, 60, 75, 75);

        Phrase phrase;
        Paragraph paragraph;

        phrase = new Phrase("Receipt", new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD));

        d.add(phrase);
        d.add(Chunk.NEWLINE);
        d.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(new float[]{0.6f, 0.4f});

        table.getDefaultCell().setBorder(0);
        table.setWidthPercentage(100);

        PdfPCell cell;

        cell = new PdfPCell();
        cell.setBorder(0);
        cell.addElement(new Paragraph("Buyer Info:"));
        cell.addElement(new Paragraph("username: " + buyer.getUsername()));
        cell.addElement(Chunk.NEWLINE);
        table.addCell(cell);

        cell = new PdfPCell();
        cell.setBorder(0);
        cell.addElement(new Paragraph("Seller Info:"));
        cell.addElement(new Paragraph("username: " + seller.getUsername()));
        cell.addElement(Chunk.NEWLINE);
        table.addCell(cell);


        d.add(table);

        d.add(Chunk.NEWLINE);
        d.add(Chunk.NEWLINE);

        d.add(new Paragraph("Order summary:"));
        d.add(Chunk.NEWLINE);

        table = new PdfPTable(6);
        table.setWidthPercentage(100);
        
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.getDefaultCell().setMinimumHeight(25);

        table.addCell("Product");
        table.addCell("Category");
        table.addCell("U.M.");
        table.addCell("Unit Price");
        table.addCell("Quantity");
        table.addCell("Total");

        table.addCell(prod.getName());
        table.addCell(cat.getName());
        table.addCell(prod.getUm());
        DecimalFormat df = new DecimalFormat("0.00");
        table.addCell(df.format(prod.getPrice())+"€");
        table.addCell(Integer.toString(prod.getQuantity()));
        table.addCell(df.format(prod.getPrice()*prod.getQuantity())+"€");

        d.add(table);

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
