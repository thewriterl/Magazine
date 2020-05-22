package com.pixelmags.repository.search;

import com.pixelmags.domain.Log;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Log} entity.
 */
public interface LogSearchRepository extends ElasticsearchRepository<Log, Long> {
}
