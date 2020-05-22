package com.pixelmags.repository.search;

import com.pixelmags.domain.SubscriptionPlan;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link SubscriptionPlan} entity.
 */
public interface SubscriptionPlanSearchRepository extends ElasticsearchRepository<SubscriptionPlan, Long> {
}
