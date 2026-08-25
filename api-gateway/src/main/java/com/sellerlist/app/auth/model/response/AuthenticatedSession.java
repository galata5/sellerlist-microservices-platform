package com.sellerlist.app.auth.model.response;

public record AuthenticatedSession(String token, AuthenticationResponse response) {
}
