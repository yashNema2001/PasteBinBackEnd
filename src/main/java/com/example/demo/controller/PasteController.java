package com.example.demo.controller;

import com.example.demo.entity.Paste;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;
import java.time.Instant;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;    
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class PasteController {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Value("${app.base-url}")
  private String baseUrl; // Set this to https://your-app.vercel.app in prod

  @GetMapping("/healthz")
  public ResponseEntity<Map<String, Object>> healthCheck() {
    try {
      boolean isUp = "PONG".equals(redisTemplate.getConnectionFactory().getConnection().ping());
      return ResponseEntity.ok(Map.of("ok", isUp)); // [cite: 27, 34]
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("ok", false));
    }
  }

  @PostMapping("/pastes")
  public ResponseEntity<?> createPaste(@RequestBody Map<String, Object> body) {
    String content = (String) body.get("content");
    if (content == null || content.trim().isEmpty()) {
      return ResponseEntity.status(400).body(Map.of("error", "Content required")); // [cite: 44, 53]
    }

    String id = UUID.randomUUID().toString();
    Integer ttl = (Integer) body.get("ttl_seconds");
    Integer maxViews = (Integer) body.get("max_views");

    Paste paste = new Paste();
    paste.setId(id);
    paste.setContent(content);
    paste.setRemainingViews(maxViews);
    if (ttl != null && ttl >= 1) {
      paste.setExpiresAt(System.currentTimeMillis() + (ttl * 1000L));
    }

    redisTemplate.opsForValue().set("paste:" + id, paste);

    // Fix: Use /p/ prefix and dynamic base URL [cite: 51, 94]
    String shareUrl = baseUrl + "/p/" + id;
    return ResponseEntity.status(201).body(Map.of("id", id, "url", shareUrl));
  }

  @GetMapping("/pastes/{id}")
  public ResponseEntity<?> getPaste(
      @PathVariable String id,
      @RequestHeader(value = "x-test-now-ms", required = false) Long testNow) {

    Paste paste = (Paste) redisTemplate.opsForValue().get("paste:" + id);

    // Deterministic Time Logic [cite: 79, 81]
    long now = (testNow != null && "1".equals(System.getenv("TEST_MODE"))) ? testNow : System.currentTimeMillis();

    if (paste == null)
      return ResponseEntity.status(404).build(); // [cite: 71]

    // Combined Constraint Logic [cite: 24, 109]
    if (paste.getExpiresAt() != null && now > paste.getExpiresAt()) {
      redisTemplate.delete("paste:" + id);
      return ResponseEntity.status(404).build();
    }

    if (paste.getRemainingViews() != null) {
      if (paste.getRemainingViews() <= 0) {
        redisTemplate.delete("paste:" + id);
        return ResponseEntity.status(404).build();
      }
      paste.setRemainingViews(paste.getRemainingViews() - 1); // [cite: 65]
      redisTemplate.opsForValue().set("paste:" + id, paste);
    }

    return ResponseEntity.ok(Map.of(
        "content", paste.getContent(),
        "remaining_views", paste.getRemainingViews() != null ? paste.getRemainingViews() : null,
        "expires_at", paste.getExpiresAt() != null ? Instant.ofEpochMilli(paste.getExpiresAt()).toString() : null));
  }
}