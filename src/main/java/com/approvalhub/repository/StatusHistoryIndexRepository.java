package com.approvalhub.repository;

import com.approvalhub.domain.entity.StatusHistoryIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
@EnableElasticsearchRepositories
public interface StatusHistoryIndexRepository extends ElasticsearchRepository<StatusHistoryIndex, String> {

    List<StatusHistoryIndex> findByDocumentTitleAndChangeByUsername(String query);

}
