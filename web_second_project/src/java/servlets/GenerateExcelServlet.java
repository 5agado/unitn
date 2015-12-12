package servlets;

import db.DBManager;
import db.beans.Product;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import utilities.AuxiliaryMethods;
import static utilities.Constants.*;

public class GenerateExcelServlet extends HttpServlet {

    private DBManager manager;

    @Override
    public void init() throws ServletException {
        this.manager = (DBManager) super.getServletContext().getAttribute(DB_ATTRIBUTE_NAME);
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
        try {
            List<Product> products = manager.getProductsToUpdate();
            for (Product p : products) {
                AuxiliaryMethods.updateExpired(manager, p);
            }

        } catch (SQLException ex) {
            throw new ServletException(ex.getMessage());
        }
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=sales.xls");
        HSSFWorkbook workbook = new HSSFWorkbook();
        generateExcel(workbook);
        workbook.write(response.getOutputStream());
    }

    private void generateExcel(HSSFWorkbook workbook) {
        HSSFSheet sheet = workbook.createSheet("Sales");
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        HSSFCellStyle headerCS = workbook.createCellStyle();
        headerCS.setFont(font);
        
        HSSFRow header = sheet.createRow(0);
        HSSFCell A = header.createCell(0);
        A.setCellStyle(headerCS);
        A.setCellValue("Description");
        HSSFCell B = header.createCell(1);
        B.setCellValue("Price");
        B.setCellStyle(headerCS);
        HSSFCell C = header.createCell(2);
        C.setCellValue("Seller");
        C.setCellStyle(headerCS);
        HSSFCell D = header.createCell(3);
        D.setCellValue("Tax");
        D.setCellStyle(headerCS);
        HSSFCell E = header.createCell(4);
        E.setCellValue("Invalid");
        E.setCellStyle(headerCS);
        try {
            List<Product> sales = manager.getTerminatedAuctions();
            int count = 1;
            for (Product s : sales) {
                if (!s.getCanceled()) {
                    boolean invalid = (s.getBuyer().getUsername() == null);
                    HSSFRow row1 = sheet.createRow(count);
                    HSSFCellStyle cs = workbook.createCellStyle();

                    if (invalid) {
                        cs.setFillForegroundColor(HSSFColor.AQUA.index);
                        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                    }

                    HSSFCell cellA1 = row1.createCell(0);
                    cellA1.setCellValue(s.getDescription());
                    cellA1.setCellStyle(cs);

                    HSSFCell cellB1 = row1.createCell(1);
                    cellB1.setCellValue(s.getPrice());
                    cellB1.setCellType(Cell.CELL_TYPE_NUMERIC);
                    cellB1.setCellStyle(cs);

                    HSSFCell cellC1 = row1.createCell(2);
                    cellC1.setCellValue(s.getSeller().getUsername());
                    cellC1.setCellStyle(cs);

                    HSSFCell cellD1 = row1.createCell(3);
                    cellD1.setCellType(Cell.CELL_TYPE_NUMERIC);
                    cellD1.setCellValue(s.getTax() + " $");
                    cellD1.setCellStyle(cs);

                    HSSFCell cellE1 = row1.createCell(4);
                    cellE1.setCellValue(invalid ? "X" : "");
                    cellE1.setCellStyle(cs);

                    count++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(GenerateExcelServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
