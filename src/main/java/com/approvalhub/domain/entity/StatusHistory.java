package com.approvalhub.domain.entity;


import com.approvalhub.domain.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Data
@Entity
@Table(name = "status_history")
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status oldStatus;

    @Enumerated(EnumType.STRING)
    private Status newStatus;

    private String comment;

    @UpdateTimestamp
    private Instant changeAt;

    @ManyToOne
    @JoinColumn(name = "document_id")
    @NotNull
    private Document document;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User changedBy;
}
