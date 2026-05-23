package com.approvalhub.controller;

import com.approvalhub.dto.document.DocumentCreateDTO;
import com.approvalhub.dto.document.DocumentResponseDTO;
import com.approvalhub.dto.document.DocumentStatusUpdateDTO;
import com.approvalhub.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public DocumentCreateDTO createDocument(@RequestBody DocumentCreateDTO documentCreateDTO) throws Exception {
        return documentService.createDocument(documentCreateDTO);
    }

    @GetMapping
    public List<DocumentResponseDTO> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @GetMapping("/{id}")
    public DocumentResponseDTO getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id);
    }

//    @GetMapping("/{title}")
//    public DocumentResponseDTO getDocumentByTitle(@PathVariable String title) {
//        return documentService.getDocumentByTitle(title);
//    }

    @PatchMapping("/{id}/submit")
    public DocumentStatusUpdateDTO submitDocument(@PathVariable Long id, @RequestBody String comment) throws Exception {
        return documentService.submitDocument(id, comment);
    }

    @PatchMapping("/{id}/approve")
    public DocumentStatusUpdateDTO approveDocument(@PathVariable Long id, @RequestBody String comment) throws RuntimeException {
        return documentService.approveDocument(id, comment);
    }

    @PatchMapping("/{id}/reject")
    public DocumentStatusUpdateDTO rejectDocument(@PathVariable Long id, @RequestBody String comment) {
        return documentService.rejectDocument(id, comment);
    }
}
