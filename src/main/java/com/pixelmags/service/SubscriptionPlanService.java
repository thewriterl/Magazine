package com.pixelmags.service;

import com.pixelmags.domain.SubscriptionPlan;
import com.pixelmags.repository.SubscriptionPlanRepository;
import com.pixelmags.repository.search.SubscriptionPlanSearchRepository;
import com.pixelmags.service.dto.SubscriptionPlanDTO;
import com.pixelmags.service.mapper.SubscriptionPlanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link SubscriptionPlan}.
 */
@Service
@Transactional
public class SubscriptionPlanService {

    private final Logger log = LoggerFactory.getLogger(SubscriptionPlanService.class);

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    private final SubscriptionPlanMapper subscriptionPlanMapper;

    private final SubscriptionPlanSearchRepository subscriptionPlanSearchRepository;

    public SubscriptionPlanService(SubscriptionPlanRepository subscriptionPlanRepository, SubscriptionPlanMapper subscriptionPlanMapper, SubscriptionPlanSearchRepository subscriptionPlanSearchRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.subscriptionPlanMapper = subscriptionPlanMapper;
        this.subscriptionPlanSearchRepository = subscriptionPlanSearchRepository;
    }

    /**
     * Save a subscriptionPlan.
     *
     * @param subscriptionPlanDTO the entity to save.
     * @return the persisted entity.
     */
    public SubscriptionPlanDTO save(SubscriptionPlanDTO subscriptionPlanDTO) {
        log.debug("Request to save SubscriptionPlan : {}", subscriptionPlanDTO);
        SubscriptionPlan subscriptionPlan = subscriptionPlanMapper.toEntity(subscriptionPlanDTO);
        subscriptionPlan = subscriptionPlanRepository.save(subscriptionPlan);
        SubscriptionPlanDTO result = subscriptionPlanMapper.toDto(subscriptionPlan);
        subscriptionPlanSearchRepository.save(subscriptionPlan);
        return result;
    }

    /**
     * Get all the subscriptionPlans.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SubscriptionPlanDTO> findAll() {
        log.debug("Request to get all SubscriptionPlans");
        return subscriptionPlanRepository.findAll().stream()
            .map(subscriptionPlanMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }



    /**
     *  Get all the subscriptionPlans where Purchase is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true) 
    public List<SubscriptionPlanDTO> findAllWherePurchaseIsNull() {
        log.debug("Request to get all subscriptionPlans where Purchase is null");
        return StreamSupport
            .stream(subscriptionPlanRepository.findAll().spliterator(), false)
            .filter(subscriptionPlan -> subscriptionPlan.getPurchase() == null)
            .map(subscriptionPlanMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one subscriptionPlan by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SubscriptionPlanDTO> findOne(Long id) {
        log.debug("Request to get SubscriptionPlan : {}", id);
        return subscriptionPlanRepository.findById(id)
            .map(subscriptionPlanMapper::toDto);
    }

    /**
     * Delete the subscriptionPlan by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SubscriptionPlan : {}", id);

        subscriptionPlanRepository.deleteById(id);
        subscriptionPlanSearchRepository.deleteById(id);
    }

    /**
     * Search for the subscriptionPlan corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<SubscriptionPlanDTO> search(String query) {
        log.debug("Request to search SubscriptionPlans for query {}", query);
        return StreamSupport
            .stream(subscriptionPlanSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(subscriptionPlanMapper::toDto)
        .collect(Collectors.toList());
    }
}
