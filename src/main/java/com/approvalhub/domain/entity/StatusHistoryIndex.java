package com.approvalhub.domain.entity;

import com.approvalhub.domain.enums.Status;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.time.Instant;

@Data
@Document(indexName = "status_history")
public class StatusHistoryIndex {

    @Id
    private String id;

    @NotNull
    @Field(type = FieldType.Text, analyzer = "french")
    private String documentTitle;

    @NotNull
    @Field(type = FieldType.Text, analyzer = "french")
    private String changeByUsername;

    @NotNull
    @Field(type = FieldType.Keyword)
    private String oldStatus;

    @NotNull
    @Field(type = FieldType.Keyword)
    private String newStatus;

    @NotNull
    @Field(type = FieldType.Date_Nanos)
    private String changedAt;

}
