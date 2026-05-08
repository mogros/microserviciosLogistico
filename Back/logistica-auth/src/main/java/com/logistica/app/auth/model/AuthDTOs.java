package com.logistica.app.auth.model;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Set;

public class AuthDTOs {
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
        public String getUsername(){return username;} public void setUsername(String u){this.username=u;}
        public String getPassword(){return password;} public void setPassword(String p){this.password=p;}
    }
    public static class RegisterRequest {
        @NotBlank @Size(min=3,max=50) private String username;
        @NotBlank @Email private String email;
        @NotBlank @Size(min=8,max=40) private String password;
        private Set<String> roles;
        public String getUsername(){return username;} public void setUsername(String u){this.username=u;}
        public String getEmail(){return email;} public void setEmail(String e){this.email=e;}
        public String getPassword(){return password;} public void setPassword(String p){this.password=p;}
        public Set<String> getRoles(){return roles;} public void setRoles(Set<String> r){this.roles=r;}
    }
    public static class JwtResponse {
        private String token; private final String type="Bearer";
        private Long id; private String username; private String email; private List<String> roles;
        public JwtResponse(String t,Long i,String u,String e,List<String> r){token=t;id=i;username=u;email=e;roles=r;}
        public String getToken(){return token;} public String getType(){return type;}
        public Long getId(){return id;} public String getUsername(){return username;}
        public String getEmail(){return email;} public List<String> getRoles(){return roles;}
    }
    public static class MessageResponse {
        private String message;
        public MessageResponse(String m){this.message=m;}
        public String getMessage(){return message;} public void setMessage(String m){this.message=m;}
    }
}