package com.pixelmags.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PurchaseSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PurchaseSearchRepositoryMockConfiguration {

    @MockBean
    private PurchaseSearchRepository mockPurchaseSearchRepository;

}
