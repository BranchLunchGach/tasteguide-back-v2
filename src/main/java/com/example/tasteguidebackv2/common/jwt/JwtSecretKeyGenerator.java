package com.example.tasteguidebackv2.common.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Base64;

public class JwtSecretKeyGenerator {
	public static void main(String[] args) {
		byte[] key = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
		String base64Key = Base64.getEncoder().encodeToString(key);
		System.out.println("Generated Secret Key: " + base64Key);
	}
}
