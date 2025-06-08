package com.store.app.petstore.Controllers.Staff;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.store.app.petstore.DAO.CustomerDAO;
import com.store.app.petstore.Models.Entities.*;
import com.store.app.petstore.Utils.VietQRGenerator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class HoaDonPDF {
    // Thông tin ngân hàng của cửa hàng
    private static final String BANK_CODE = "TPB";
    private static final String ACCOUNT_NUMBER = "07692472801";
    private static final String ACCOUNT_NAME = "NGUYEN THAI NGOC THAO";

    public static void xuatHoaDonPDF(String tenFile, Order order, ArrayList<OrderDetail> details,
                                     Map<Integer, Product> products, Map<Integer, Pet> pets, Discount discount) throws IOException {
        Document document = new Document();
        String qrPath = null;
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(tenFile));
            document.open();

            // Font setup
            BaseFont bf = BaseFont.createFont("src/main/resources/Font/Inter-Regular.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font fontTieuDe = new Font(bf, 18, Font.BOLD, new BaseColor(1, 127, 203));
            Font fontThuong = new Font(bf, 12, Font.NORMAL, BaseColor.GRAY);
            Font fontThuongDam = new Font(bf, 12, Font.BOLD, BaseColor.WHITE);
            Font fontThuongNghieng = new Font(bf, 12, Font.ITALIC, BaseColor.GRAY);
            Font fontTong = new Font(bf, 14, Font.BOLD, new BaseColor(169, 228, 77));

            // Title
            Paragraph title = new Paragraph("HÓA ĐƠN BÁN HÀNG", fontTieuDe);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Calculate total amount first (needed for QR code)
            double tongTien = 0;
            for (OrderDetail detail : details) {
                Product product = "product".equals(detail.getItemType()) ? products.get(detail.getItemId()) : null;
                Pet pet = "pet".equals(detail.getItemType()) ? pets.get(detail.getItemId()) : null;
                double giaBan = product != null ? product.getPrice() : (pet != null ? pet.getPrice() : 0);
                tongTien += giaBan * detail.getQuantity();
            }

            // Calculate discount amount
            double discountAmount = 0;
            if (discount != null) {
                if ("percent".equals(discount.getDiscountType())) {
                    discountAmount = tongTien * (discount.getValue() / 100.0);
                    if (discount.getMaxDiscountValue() > 0) {
                        discountAmount = Math.min(discountAmount, discount.getMaxDiscountValue());
                    }
                } else {
                    discountAmount = discount.getValue();
                }
            }
            double tongTienSauGiam = tongTien - discountAmount;

            // Generate VietQR and save to temp file
            try {
                qrPath = "vietqr_temp.png";
                String description = "Thanh toan don hang #" + order.getOrderId();
                VietQRGenerator.generateVietQR(BANK_CODE, ACCOUNT_NUMBER, ACCOUNT_NAME, tongTienSauGiam, description, 200, 200, qrPath);
            } catch (Exception e) {
                System.err.println("Không thể tạo mã QR VietQR: " + e.getMessage());
                qrPath = null;
            }

            // Customer and order information
            Customer customer = CustomerDAO.findById(order.getCustomerId());
            StringBuilder infoText = new StringBuilder();
            infoText.append("Mã hóa đơn: ").append(order.getOrderId()).append("\n");
            infoText.append("Thời gian lập: ").append(order.getOrderDate().toString()).append("\n");

            if (customer != null) {
                infoText.append("Khách hàng: ").append(customer.getFullName()).append("\n");
                infoText.append("Số điện thoại: ").append(customer.getPhone());
            } else {
                infoText.append("Khách hàng: Không rõ").append("\n");
                infoText.append("Số điện thoại: Không rõ");
            }

            Paragraph info = new Paragraph(infoText.toString(), fontThuong);
            info.setAlignment(Element.ALIGN_LEFT);
            info.setSpacingAfter(20);
            document.add(info);

            // Table setup
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.0f, 3.5f, 2.2f, 1.8f, 1.2f, 2.5f});
            table.setSpacingBefore(10);

            BaseColor headerColor = new BaseColor(1, 127, 203);

            // Table headers with styling
            String[] headers = {"STT", "Sản phẩm", "Loại sản phẩm", "Đơn giá", "Số lượng", "Thành tiền"};
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, fontThuongDam));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerCell.setPadding(8);
                headerCell.setBackgroundColor(headerColor);
                table.addCell(headerCell);
            }

            // Reset tongTien for table calculation
            tongTien = 0;

            // Table data
            for (int i = 0; i < details.size(); i++) {
                OrderDetail detail = details.get(i);
                Product product = "product".equals(detail.getItemType()) ? products.get(detail.getItemId()) : null;
                Pet pet = "pet".equals(detail.getItemType()) ? pets.get(detail.getItemId()) : null;

                // STT
                PdfPCell sttCell = new PdfPCell(new Phrase(String.valueOf(i + 1), fontThuong));
                sttCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                sttCell.setPadding(5);
                table.addCell(sttCell);

                // Product name
                String productName = product != null ? product.getName() :
                        pet != null ? pet.getName() : "Không rõ";
                PdfPCell nameCell = new PdfPCell(new Phrase(productName, fontThuong));
                nameCell.setPadding(5);
                table.addCell(nameCell);

                // Product type
                String type = "";
                if ("product".equals(detail.getItemType()) && product != null) {
                    String category = product.getCategory();
                    if ("đồ chơi".equals(category)) {
                        type = "Đồ chơi";
                    } else if ("phụ kiện".equals(category)) {
                        type = "Phụ kiện";
                    } else {
                        type = "Thức ăn";
                    }
                } else if ("pet".equals(detail.getItemType())) {
                    type = "Thú cưng";
                } else {
                    type = "Không rõ";
                }
                PdfPCell typeCell = new PdfPCell(new Phrase(type, fontThuong));
                typeCell.setPadding(5);
                table.addCell(typeCell);

                // Price
                double giaBan = product != null ? product.getPrice() :
                        (pet != null ? pet.getPrice() : 0);
                PdfPCell priceCell = new PdfPCell(new Phrase(String.format("%,.0f", giaBan), fontThuong));
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                priceCell.setPadding(5);
                table.addCell(priceCell);

                // Quantity
                PdfPCell quantityCell = new PdfPCell(new Phrase(String.valueOf(detail.getQuantity()), fontThuong));
                quantityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                quantityCell.setPadding(5);
                table.addCell(quantityCell);

                // Subtotal
                double thanhTien = giaBan * detail.getQuantity();
                tongTien += thanhTien;
                PdfPCell subtotalCell = new PdfPCell(new Phrase(String.format("%,.0f", thanhTien), fontThuong));
                subtotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                subtotalCell.setPadding(5);
                table.addCell(subtotalCell);
            }

            document.add(table);

            // Summary section
            document.add(new Paragraph(" ", fontThuong));

            // Subtotal
            Paragraph subtotal = new Paragraph("Tạm tính: " + String.format("%,.0f", tongTien), fontThuongDam);
            subtotal.setAlignment(Element.ALIGN_RIGHT);
            subtotal.setSpacingBefore(10);
            document.add(subtotal);

            // Discount information
            if (discount != null) {
                Paragraph discountCode = new Paragraph("Mã giảm giá: " + discount.getCode(), fontThuong);
                discountCode.setAlignment(Element.ALIGN_RIGHT);
                document.add(discountCode);

                if ("percent".equals(discount.getDiscountType())) {
                    Paragraph discountInfo = new Paragraph("Giảm giá: " + discount.getValue() + "% (-" +
                            String.format("%,.0f", discountAmount) + ")", fontThuong);
                    discountInfo.setAlignment(Element.ALIGN_RIGHT);
                    document.add(discountInfo);
                } else {
                    Paragraph discountInfo = new Paragraph("Giảm giá: -" +
                            String.format("%,.0f", discountAmount), fontThuong);
                    discountInfo.setAlignment(Element.ALIGN_RIGHT);
                    document.add(discountInfo);
                }
            }

            // Total amount
            Paragraph totalAmount = new Paragraph("TỔNG TIỀN: " + String.format("%,.0f VNĐ", tongTienSauGiam), fontTong);
            totalAmount.setAlignment(Element.ALIGN_RIGHT);
            totalAmount.setSpacingBefore(10);
            totalAmount.setSpacingAfter(20);
            document.add(totalAmount);

            // Payment information section with QR Code
            document.add(new Paragraph(" ", fontThuong));
            Paragraph paymentHeader = new Paragraph("THÔNG TIN THANH TOÁN", new Font(bf, 14, Font.BOLD, new BaseColor(1, 127, 203)));
            paymentHeader.setAlignment(Element.ALIGN_LEFT);
            paymentHeader.setSpacingBefore(10);
            document.add(paymentHeader);

            // Create a table for payment info and QR code layout
            PdfPTable paymentTable = new PdfPTable(2);
            paymentTable.setWidthPercentage(100);
            paymentTable.setWidths(new float[]{3f, 2f}); // Left column wider for text, right for QR
            paymentTable.setSpacingBefore(10);

            // Left cell - Payment information
            StringBuilder paymentInfo = new StringBuilder();
            paymentInfo.append("• Quét mã QR bằng app ngân hàng\n");
            paymentInfo.append("• Ngân hàng: TP Bank\n");
            paymentInfo.append("• Số tài khoản: ").append(ACCOUNT_NUMBER).append("\n");
            paymentInfo.append("• Chủ tài khoản: ").append(ACCOUNT_NAME).append("\n");
            paymentInfo.append("• Số tiền: ").append(String.format("%,.0f VNĐ", tongTienSauGiam)).append("\n");
            paymentInfo.append("• Nội dung: Thanh toan don hang #").append(order.getOrderId()).append("\n\n");
            paymentInfo.append("Hoặc chuyển khoản thủ công với thông tin trên");

            PdfPCell paymentInfoCell = new PdfPCell(new Phrase(paymentInfo.toString(), fontThuong));
            paymentInfoCell.setBorder(Rectangle.NO_BORDER);
            paymentInfoCell.setPadding(5);
            paymentTable.addCell(paymentInfoCell);

            // Right cell - QR Code Image
            if (qrPath != null) {
                Image img = Image.getInstance(qrPath);
                img.scaleToFit(150, 150);
                PdfPCell qrCell = new PdfPCell(img, true);
                qrCell.setBorder(Rectangle.NO_BORDER);
                qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                paymentTable.addCell(qrCell);
            } else {
                PdfPCell qrCell = new PdfPCell(new Phrase("Không có mã QR", fontThuongNghieng));
                qrCell.setBorder(Rectangle.NO_BORDER);
                qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                paymentTable.addCell(qrCell);
            }

            document.add(paymentTable);

            // Add some spacing
            document.add(new Paragraph(" ", fontThuong));

            // Footer
            Paragraph footer = new Paragraph("Cảm ơn quý khách đã mua hàng!", fontThuongNghieng);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (document.isOpen()) {
                document.close();
            }
        } finally {
            // Delete temp QR file if exists
            if (qrPath != null) {
                File temp = new File(qrPath);
                if (temp.exists()) temp.delete();
            }
        }

        openPDF(tenFile);
    }

    private static void openPDF(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                System.out.println("Desktop không được hỗ trợ trên hệ thống này.");
            }
        } else {
            System.out.println("Không tìm thấy file PDF để mở.");
        }
    }

    public static void main(String[] args) throws IOException {
        // Test data setup
        Order order = new Order();
        order.setOrderId(1);
        order.setCustomerId(1);
        order.setOrderDate(java.time.LocalDateTime.now());

        ArrayList<OrderDetail> details = new ArrayList<>();
        OrderDetail detail1 = new OrderDetail();
        detail1.setItemId(1);
        detail1.setItemType("product");
        detail1.setQuantity(2);
        details.add(detail1);
        OrderDetail detail2 = new OrderDetail();
        detail2.setItemId(2);
        detail2.setItemType("pet");
        detail2.setQuantity(1);
        details.add(detail2);

        Map<Integer, Product> products = new java.util.HashMap<>();
        Product product = new Product();
        product.setProductId(1);
        product.setName("Dog Food");
        product.setCategory("thức ăn");
        product.setPrice(50000);
        products.put(1, product);

        Map<Integer, Pet> pets = new java.util.HashMap<>();
        Pet pet = new Pet();
        pet.setPetId(2);
        pet.setName("Puppy");
        pet.setPrice(2000000);
        pets.put(2, pet);

        Discount discount = new Discount(1, "TEST10", "percent", 10.0, java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(10), 0, 100000);

        HoaDonPDF.xuatHoaDonPDF("test_hoadon.pdf", order, details, products, pets, discount);
        System.out.println("PDF generated: test_hoadon.pdf");
    }
}
