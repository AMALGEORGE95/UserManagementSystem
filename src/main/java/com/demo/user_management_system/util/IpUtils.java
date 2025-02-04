package com.demo.user_management_system.util;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IpUtils {

    private static final String IPIFY_URL = "https://api.ipify.org?format=json";
    private static final String IP_API_URL = "http://ip-api.com/json/";

    private final RestTemplate restTemplate;

    public IpUtils(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Fetch the user's public IP address
    public String getPublicIp() {
        IpifyResponse response = restTemplate.getForObject(IPIFY_URL, IpifyResponse.class);
        return response != null ? response.getIp() : null;
    }

    // Fetch the country associated with the IP address
    public String getCountryByIp(String ip) {
        IpApiResponse response = restTemplate.getForObject(IP_API_URL + ip, IpApiResponse.class);
        return response != null ? response.getCountry() : null;
    }

    // Inner class for IPify response
    private static class IpifyResponse {
        private String ip;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }

    // Inner class for IP-API response
    private static class IpApiResponse {
        private String country;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}
