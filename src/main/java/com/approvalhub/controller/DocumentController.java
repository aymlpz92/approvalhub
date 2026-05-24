package com.approvalhub.controller;

import com.approvalhub.domain.entity.Document;
import com.approvalhub.dto.document.DocumentCreateDTO;
import com.approvalhub.dto.document.DocumentResponseDTO;
import com.approvalhub.dto.document.DocumentStatusUpdateDTO;
import com.approvalhub.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.approvalhub.domain.enums.Role.SUBMITTER;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentCreateDTO> createDocument(@RequestBody DocumentCreateDTO documentCreateDTO) throws Exception {
        return new ResponseEntity<>(documentService.createDocument(documentCreateDTO), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponseDTO>> getAllDocuments() {
        return new ResponseEntity<>(documentService.getAllDocuments(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> getDocumentById(@PathVariable Long id) {
        return new ResponseEntity<>(documentService.getDocumentById(id), HttpStatus.OK);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<DocumentResponseDTO> getDocumentByTitle(@PathVariable String title) {
        return new ResponseEntity<>(documentService.getDocumentByTitle(title), HttpStatus.OK);
    }

    @PatchMapping("/{id}/submit")
    public ResponseEntity<DocumentStatusUpdateDTO> submitDocument(@PathVariable Long id, @RequestBody String comment) {
        return new ResponseEntity<>(documentService.submitDocument(id, comment), HttpStatus.OK);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<DocumentStatusUpdateDTO> approveDocument(@PathVariable Long id, @RequestBody String comment) {
        return new ResponseEntity<>(documentService.approveDocument(id, comment), HttpStatus.OK);
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<DocumentStatusUpdateDTO> rejectDocument(@PathVariable Long id, @RequestBody String comment) {
        return new ResponseEntity<>(documentService.rejectDocument(id, comment), HttpStatus.OK);
    }
}
