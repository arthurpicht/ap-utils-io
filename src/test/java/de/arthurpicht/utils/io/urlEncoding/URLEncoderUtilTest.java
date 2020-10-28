package de.arthurpicht.utils.io.urlEncoding;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class URLEncoderUtilTest {

    @Test
    void xWWWFormEncodeEmpty() {
        Map<String, String> keyValuePairs = new HashMap<>();
        String xWWWFormEncoded = URLEncoderUtil.xWWWFormEncode(keyValuePairs);
        assertEquals("", xWWWFormEncoded);
    }

    @Test
    void xWWWFormEncodeOnePairAlphanumeric() {
        Map<String, String> keyValuePairs = new HashMap<>();
        keyValuePairs.put("key", "value");
        String xWWWFormEncoded = URLEncoderUtil.xWWWFormEncode(keyValuePairs);
        assertEquals("key=value", xWWWFormEncoded);
    }

    @Test
    void xWWWFormEncodeOnePairNonAlphanumeric() {
        Map<String, String> keyValuePairs = new HashMap<>();
        keyValuePairs.put("käy", "välue");
        String xWWWFormEncoded = URLEncoderUtil.xWWWFormEncode(keyValuePairs);
        assertEquals("k%C3%A4y=v%C3%A4lue", xWWWFormEncoded);
    }

    @Test
    void xWWWFormEncodeTwoPairsNonAlphanumeric() {
        Map<String, String> keyValuePairs = new HashMap<>();
        keyValuePairs.put("käy", "välue");
        keyValuePairs.put("dümmy", "*ÖÄ");
        String xWWWFormEncoded = URLEncoderUtil.xWWWFormEncode(keyValuePairs);
        assertEquals("k%C3%A4y=v%C3%A4lue&d%C3%BCmmy=*%C3%96%C3%84", xWWWFormEncoded);
    }

}