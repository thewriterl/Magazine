package com.pixelmags.repository.search;

import com.pixelmags.domain.Magazine;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Magazine} entity.
 */
public interface MagazineSearchRepository extends ElasticsearchRepository<Magazine, Long> {
}
