package com.store.app.petstore.Utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class VietQRGenerator {

    public static void generateVietQR(String bankCode, String accountNumber, String accountName, double amount, String note, int width, int height, String outputPath) throws Exception {
        String qrUrl = buildQRData(bankCode, accountNumber, accountName, amount, note);
        java.net.URL url = new java.net.URL(qrUrl);
        BufferedImage qrImage = ImageIO.read(url);
        if (qrImage == null) throw new Exception("Không thể tải ảnh QR từ đường link: " + qrUrl);
        File outputFile = new File(outputPath);
        ImageIO.write(qrImage, "png", outputFile);
    }

    private static String buildQRData(String bankCode, String accountNumber, String accountName,
                                      double amount, String note) {

        return "https://img.vietqr.io/image/" + bankCode + "-" + accountNumber +
                "-compact.png?amount=" + (long) amount + "&addInfo=" + note.replace(" ", "%20") +
                "&accountName=" + accountName.replace(" ", "%20");
    }
}
