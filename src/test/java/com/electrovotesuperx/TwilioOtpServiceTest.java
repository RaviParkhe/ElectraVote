package com.electrovotesuperx;

import com.electrovotesuperx.service.OfflineService.TwilioOtpService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TwilioOtpServiceTest {

    @Test
    void testPhoneNumberFormatting() {
        assertEquals("+919876543210", TwilioOtpService.formatPhoneNumber("9876543210"));
        assertEquals("+919876543210", TwilioOtpService.formatPhoneNumber("09876543210"));
        assertEquals("+919876543210", TwilioOtpService.formatPhoneNumber("+919876543210"));
        assertEquals("+919876543210", TwilioOtpService.formatPhoneNumber("919876543210"));
        assertEquals("+14155552671", TwilioOtpService.formatPhoneNumber("+14155552671"));
        assertEquals("+919876543210", TwilioOtpService.formatPhoneNumber(" 98765-43210 "));
    }

    @Test
    void testPhoneNumberMasking() {
        String masked = TwilioOtpService.maskPhoneNumber("9876543210");
        assertTrue(masked.contains("•••••"));
        assertTrue(masked.endsWith("3210"));
    }
}
