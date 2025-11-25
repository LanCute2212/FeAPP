package com.example.caloriesapp.dto.response;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostSummaryResponse {
    private Integer id;
    private String title;
    private String imageUrl;
    private String content;
    private String categoryName;
    private String authorName;
    private LocalDateTime createdAt;
}