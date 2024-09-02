package com.oatuh.sso_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oatuh.sso_backend.dtos.FacebookUserDTO;
import com.oatuh.sso_backend.dtos.UserDTO;
import com.oatuh.sso_backend.entitites.User;
import com.oatuh.sso_backend.services.UserService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/facebook")
public class FacebookController {

	@Autowired
	private UserService userService;
	
	@Value("${spring.security.oauth2.client.registration.facebook.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
	private String clientSecret;

	private static final String REDIRECT_URI = "http://localhost:4200/loading";

	private final WebClient webClient;
	public FacebookController(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.baseUrl("https://graph.facebook.com").build();
	}
	
	@GetMapping("/url")
	public String auth() {
		String url = "https://www.facebook.com/v12.0/dialog/oauth?" 
				+ "client_id=" + clientId
				+ "&redirect_uri="+ REDIRECT_URI 
				+ "&scope=email";
		return url;
	}

	
	@GetMapping("/verifyToken")
	public ResponseEntity<Map<String, Object>> verifyToken(@RequestParam("access_token") String token) {
		try {
			boolean isValid = checkFacebookTokenValidity(token);
			  Map<String, Object> response = new HashMap<>();
			System.out.print("controlliamo isValid:\n"+isValid+"\nsiamo dopo");
			if (isValid) {
			  
			    response.put("valid", isValid);
				return ResponseEntity.ok(response);
			} else {
				 response.put("valid", isValid);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
			}
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private boolean checkFacebookTokenValidity(String token) throws IOException {

		 String tokenInfoUrl = String.format("/v12.0/debug_token?input_token=%s&access_token=%s|%s", token, clientId, clientSecret);
	        System.out.print("url:\n"+tokenInfoUrl+"\n\n");
	        String tokenResponse = webClient.get()
	                .uri(tokenInfoUrl)
	                .retrieve()
	                .bodyToMono(String.class)
	                .block();

	        ObjectMapper objectMapper = new ObjectMapper();
	        JsonNode jsonNode = objectMapper.readTree(tokenResponse);
	        JsonNode dataNode = jsonNode.path("data");
	        System.out.print("token Response:\n"+tokenResponse+"\n");
	        return dataNode.path("is_valid").asBoolean(false);
	    }

	



    @GetMapping("/getToken")
    public ResponseEntity<String> getToken(@RequestParam("codeValue") String codeValue) {
        try {
            String tokenResponse = webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/v12.0/oauth/access_token")
                    .queryParam("client_id", clientId)
                    .queryParam("client_secret", clientSecret)
                    .queryParam("redirect_uri", REDIRECT_URI)
                    .queryParam("code", codeValue)
                    .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            System.out.println("siamo dentro l'errore: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/userInfo")
    public ResponseEntity<UserDTO> getUserInfo(@RequestParam("accessToken") String accessToken) {
        try {
            UserDTO user = findUserInDB(accessToken);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private UserDTO findUserInDB(String accessToken) throws Exception {
        UserDTO userInfo = new UserDTO(webClient.get()
            .uri(uriBuilder -> uriBuilder.path("/me")
                .queryParam("fields", "id,name,email")
                .queryParam("access_token", accessToken)
                .build())
            .retrieve()
            .bodyToMono(FacebookUserDTO.class)
            .block());

        System.out.println("email: " + userInfo.getEmail());

        userInfo.setProvider("facebook");
        Optional<User> user = userService.findByEmail(userInfo.getEmail());
        if (user.isEmpty()) {
            String response = userService.registerAccount(userInfo);
        }
        return userInfo;
    }
}
