package com.pixelmags.repository.search;

import com.pixelmags.domain.Issue;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Issue} entity.
 */
public interface IssueSearchRepository extends ElasticsearchRepository<Issue, Long> {
}
