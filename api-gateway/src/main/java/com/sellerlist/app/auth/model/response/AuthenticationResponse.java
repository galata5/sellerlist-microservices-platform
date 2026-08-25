package com.sellerlist.app.auth.model.response;

public record AuthenticationResponse(Integer userId, String username, boolean authenticated) {
}
