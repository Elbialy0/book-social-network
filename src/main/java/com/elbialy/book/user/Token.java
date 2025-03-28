package com.elbialy.book.user;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String token;
    @ManyToOne
    @JoinColumn(name = "userId",nullable = false)
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime activatedAt;
}
