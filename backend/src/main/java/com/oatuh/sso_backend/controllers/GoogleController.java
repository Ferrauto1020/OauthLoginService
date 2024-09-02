package com.oatuh.sso_backend.controllers;

import java.util.Arrays;
import java.util.Optional;

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
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.oatuh.sso_backend.dtos.GoogleUserDTO;
import com.oatuh.sso_backend.dtos.UserDTO;
import com.oatuh.sso_backend.entitites.User;
import com.oatuh.sso_backend.services.UserService;



@RestController
@RequestMapping("/google")
public class GoogleController {
	
	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String clientId;
	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String clientSecret;

	private static final String REDIRECT_URI = "http://localhost:4200/loading";
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/url")
	public String auth(){
		String url = new GoogleAuthorizationCodeRequestUrl(
				clientId,
				REDIRECT_URI,
				Arrays.asList("profile","email")
				).build();
		return url;
	}


    @GetMapping("/getToken")
    public ResponseEntity<String> getToken(@RequestParam("codeValue") String codeValue) {
        try {
        	System.out.print("siamo dentro getToken\n");
    		System.out.print("codeValue: \n"+codeValue);
            String tokenResponse = webClient.post()
                    .uri("https://oauth2.googleapis.com/token")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .bodyValue("code=" + codeValue +
                            "&client_id=" + clientId +
                            "&client_secret=" + clientSecret +
                            "&redirect_uri=" + REDIRECT_URI +
                            "&grant_type=authorization_code")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
        	System.out.print("\nsiamo dentro l'errore\n" + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

	private UserDTO findUserInDB(String accessToken) throws Exception
	{

		UserDTO userInfo = new UserDTO(webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/oauth2/v3/userinfo")
                        .queryParam("access_token", accessToken)
                        .build())
                .retrieve()
                .bodyToMono(GoogleUserDTO.class)
                .block());
		System.out.print("\n\nemail:"+userInfo.getEmail()+"\n\n");

		userInfo.setProvider("google");
		Optional<User> user = userService.findByEmail(userInfo.getEmail());
		if(user.isEmpty())
		{
			String response = userService.registerAccount(userInfo);
		}
		return userInfo;
	}
	
	private final WebClient webClient;
	   public GoogleController(WebClient.Builder webClientBuilder) {
	        this.webClient = webClientBuilder.baseUrl("https://www.googleapis.com").build();
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
}
