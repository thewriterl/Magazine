package com.pixelmags.service;

import com.pixelmags.domain.Publisher;
import com.pixelmags.repository.PublisherRepository;
import com.pixelmags.repository.search.PublisherSearchRepository;
import com.pixelmags.service.dto.PublisherDTO;
import com.pixelmags.service.mapper.PublisherMapper;
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
 * Service Implementation for managing {@link Publisher}.
 */
@Service
@Transactional
public class PublisherService {

    private final Logger log = LoggerFactory.getLogger(PublisherService.class);

    private final PublisherRepository publisherRepository;

    private final PublisherMapper publisherMapper;

    private final PublisherSearchRepository publisherSearchRepository;

    public PublisherService(PublisherRepository publisherRepository, PublisherMapper publisherMapper, PublisherSearchRepository publisherSearchRepository) {
        this.publisherRepository = publisherRepository;
        this.publisherMapper = publisherMapper;
        this.publisherSearchRepository = publisherSearchRepository;
    }

    /**
     * Save a publisher.
     *
     * @param publisherDTO the entity to save.
     * @return the persisted entity.
     */
    public PublisherDTO save(PublisherDTO publisherDTO) {
        log.debug("Request to save Publisher : {}", publisherDTO);
        Publisher publisher = publisherMapper.toEntity(publisherDTO);
        publisher = publisherRepository.save(publisher);
        PublisherDTO result = publisherMapper.toDto(publisher);
        publisherSearchRepository.save(publisher);
        return result;
    }

    /**
     * Get all the publishers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PublisherDTO> findAll() {
        log.debug("Request to get all Publishers");
        return publisherRepository.findAll().stream()
            .map(publisherMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one publisher by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PublisherDTO> findOne(Long id) {
        log.debug("Request to get Publisher : {}", id);
        return publisherRepository.findById(id)
            .map(publisherMapper::toDto);
    }

    /**
     * Delete the publisher by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Publisher : {}", id);

        publisherRepository.deleteById(id);
        publisherSearchRepository.deleteById(id);
    }

    /**
     * Search for the publisher corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PublisherDTO> search(String query) {
        log.debug("Request to search Publishers for query {}", query);
        return StreamSupport
            .stream(publisherSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(publisherMapper::toDto)
        .collect(Collectors.toList());
    }
}
