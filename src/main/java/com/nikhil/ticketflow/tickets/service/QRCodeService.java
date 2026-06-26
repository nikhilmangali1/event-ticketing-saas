package com.nikhil.ticketflow.tickets.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nikhil.ticketflow.common.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QRCodeService {

    public String generateQRCodeText(UUID ticketId, String eventTitle, LocalDateTime eventDate, String organizerEmail) {
        String json = String.format(
                "{\"id\":\"%s\",\"ev\":\"%s\",\"dt\":\"%s\",\"oe\":\"%s\"}",
                ticketId,
                eventTitle.replace("\"", "\\\""),
                eventDate.toString(),
                organizerEmail
        );
        log.info("qr info:\n{}", json);
        return Base64.getUrlEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    public byte[] generateQRCodeImage(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            var bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            var pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();

        } catch (WriterException | IOException e) {
            throw new BadRequestException("Failed to generate QR");
        }
    }
}
