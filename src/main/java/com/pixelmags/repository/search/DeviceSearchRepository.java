package com.pixelmags.repository.search;

import com.pixelmags.domain.Device;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Device} entity.
 */
public interface DeviceSearchRepository extends ElasticsearchRepository<Device, Long> {
}
