package com.oatuh.sso_backend.controllers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.oatuh.sso_backend.dtos.AzureUserDTO;
import com.oatuh.sso_backend.dtos.UserDTO;
import com.oatuh.sso_backend.entitites.User;
import com.oatuh.sso_backend.services.UserService;

@RestController
@RequestMapping("/azure")
public class AzureController {

    @Value("${azure.ad.client-id}")
    private String clientId;

    @Value("${azure.ad.client-secret}")
    private String clientSecret;

    @Value("${azure.ad.redirect-uri}")
    private String redirectUri;

    @Value("${azure.ad.token-endpoint}")
    private String tokenEndpoint;

    @Value("${azure.ad.userinfo-endpoint}")
    private String userinfoEndpoint;

    @Autowired
    private UserService userService;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String []  store = new String [2];
    public AzureController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @GetMapping("/url")
    public String auth() {
    	String codeVerifier = generateCodeVerifier();
	    String codeChallenge = generateCodeChallenge(codeVerifier);
	    String state = generateState();
	    this.store[0] = codeVerifier;
	    this.store[1] = state;
	    
	    String url=UriComponentsBuilder.fromHttpUrl("https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize")
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_mode", "query")
                .queryParam("scope", "openid profile email")
                .queryParam("state", state)
                .queryParam("code_challenge", codeChallenge)
                .queryParam("code_challenge_method", "S256")
                .build()
                .toUriString();
	    return  url;
    }

    @GetMapping("/getToken")
    public ResponseEntity<String> getToken(@RequestParam("codeValue") String codeValue) {
        try {
            System.out.print("Siamo dentro getToken\n");
            System.out.print("codeValue: \n" + codeValue+"\n");
            
            String codeVerifier = this.store[0];
            if (codeVerifier == null) {
                System.out.print("\nCODE VERIFIER NON ESISTE\n\n");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid state.");
            }

            String tokenResponse = webClient.post()
                    .uri("https://login.microsoftonline.com/consumers/oauth2/v2.0/token")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                    .bodyValue("code=" + codeValue +
                            "&client_id=" + clientId +
                            "&redirect_uri=" + redirectUri +
                            "&grant_type=authorization_code" +
                            "&code_verifier=" + codeVerifier)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
    		JsonNode jsonNode = objectMapper.readTree(tokenResponse);
    		String accessToken = jsonNode.get("access_token").asText();
            System.out.println("Token Response: " + accessToken);

            return ResponseEntity.ok(tokenResponse);
        } catch (WebClientResponseException e) {
            System.err.println("Error Response Body di getToken: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
  @GetMapping("/verifyToken")
public ResponseEntity<Map<String, Object>> verifyToken(@RequestParam("access_token") String token) {
    try {
        boolean isValid = checkAzureTokenValidity(token);
        Map<String, Object> response = new HashMap<>();
        System.out.print("controlliamo isValid:\n" + isValid + "\nsiamo dopo");
        response.put("valid", isValid);
        if (isValid) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

private boolean checkAzureTokenValidity(String token) throws IOException {
    String tokenInfoUrl = "https://graph.microsoft.com/oidc/userinfo";
    System.out.print("url:\n" + tokenInfoUrl + "\n\n");
    String tokenResponse = webClient.get()
            .uri(tokenInfoUrl)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToMono(String.class)
            .block();

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(tokenResponse);

    // Se non ci sono errori nella risposta, il token è valido
    return jsonNode.has("sub");
}

    @GetMapping("/userInfo")
    public ResponseEntity<UserDTO> userInfo(@RequestParam("accessToken") String accessToken) {
        try {
            UserDTO user = findUserInDB(accessToken);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private String generateState() {
        byte[] randomBytes = new byte[16];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String generateCodeVerifier() {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    private UserDTO findUserInDB(String accessToken) throws Exception {
        UserDTO userInfo = new UserDTO(webClient.get()
                .uri("https://graph.microsoft.com/oidc/userinfo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(AzureUserDTO.class)
                .block());
        userInfo.setProvider("microsoft");
        Optional<User> user = userService.findByEmail(userInfo.getEmail());
        if (user.isEmpty()) {
            userService.registerAccount(userInfo);
        }
        return userInfo;
    }

    
    
}