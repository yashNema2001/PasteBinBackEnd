package com.example.demo.entity;

import lombok.*;

@Data
public class Paste {
  private String id;
  private String content;
  private Long expiresAt; // Milliseconds since epoch
  private Integer remainingViews;
}
