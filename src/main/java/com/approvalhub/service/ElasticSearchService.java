package com.approvalhub.service;

import com.approvalhub.domain.entity.StatusHistoryIndex;
import com.approvalhub.domain.entity.User;
import com.approvalhub.repository.StatusHistoryIndexRepository;
import com.approvalhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.approvalhub.domain.enums.Role.REVIEWER;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final StatusHistoryIndexRepository statusHistoryIndexRepository;
    private final UserRepository userRepository;

    public List<StatusHistoryIndex> searchHistory(String query) {

        Query nativeQuery = new NativeQueryBuilder()
                .withQuery(q -> q.multiMatch(builder -> {
                            builder.query(query);
                            builder.fields("documentTitle", "changeByUsername");
                            return builder;
                            }))
                .build();

        return elasticsearchOperations.search(nativeQuery, StatusHistoryIndex.class)
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }

    public StatusHistoryIndex findById(String id) {
        return  statusHistoryIndexRepository.findById(id).orElse(null);
    }

    public void deleteById(String id) throws Exception {
        StatusHistoryIndex statusHistoryIndex = statusHistoryIndexRepository.findById(id).orElse(null);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        if (statusHistoryIndex != null) {
            if (statusHistoryIndex.getChangeByUsername().equals(username) || user.getRole().equals(REVIEWER)) {
                statusHistoryIndexRepository.deleteById(id);
            } else {
                throw new RuntimeException("Permission denied");
            }
        } else {
            throw new RuntimeException("Index not found");
        }

    }

}
