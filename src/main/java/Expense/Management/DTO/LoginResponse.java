package Expense.Management.DTO;

import Expense.Management.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String refreshToken;
    private Role role;


    public static class LoginResponseBuilder {
        private String token;
        private String refreshToken;
        private String username;
        private Role role;

        public LoginResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public LoginResponseBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public LoginResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public LoginResponseBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public LoginResponse build() {
            LoginResponse response = new LoginResponse();
            response.token = this.token;
            response.refreshToken = this.refreshToken;
            response.username = this.username;
            response.role = this.role;
            return response;
        }
    }
}
