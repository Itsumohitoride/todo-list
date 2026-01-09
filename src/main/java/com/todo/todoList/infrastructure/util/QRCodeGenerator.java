package com.todo.todoList.infrastructure.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Utility class for generating QR codes using ZXing library
 */
public class QRCodeGenerator {

    private QRCodeGenerator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generates a QR code image as a byte array (PNG format)
     *
     * @param text The text/URL to encode in the QR code
     * @param width QR code width in pixels
     * @param height QR code height in pixels
     * @return QR code image as byte array
     * @throws RuntimeException if QR code generation fails
     */
    public static byte[] generateQRCodeImage(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }

    /**
     * Generates a QR code image with default size (300x300 pixels)
     *
     * @param text The text/URL to encode in the QR code
     * @return QR code image as byte array
     */
    public static byte[] generateQRCodeImage(String text) {
        return generateQRCodeImage(text, 300, 300);
    }
}
