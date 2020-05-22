package com.pixelmags.service;

import com.pixelmags.domain.Device;
import com.pixelmags.repository.DeviceRepository;
import com.pixelmags.repository.search.DeviceSearchRepository;
import com.pixelmags.service.dto.DeviceDTO;
import com.pixelmags.service.mapper.DeviceMapper;
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
 * Service Implementation for managing {@link Device}.
 */
@Service
@Transactional
public class DeviceService {

    private final Logger log = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceRepository deviceRepository;

    private final DeviceMapper deviceMapper;

    private final DeviceSearchRepository deviceSearchRepository;

    public DeviceService(DeviceRepository deviceRepository, DeviceMapper deviceMapper, DeviceSearchRepository deviceSearchRepository) {
        this.deviceRepository = deviceRepository;
        this.deviceMapper = deviceMapper;
        this.deviceSearchRepository = deviceSearchRepository;
    }

    /**
     * Save a device.
     *
     * @param deviceDTO the entity to save.
     * @return the persisted entity.
     */
    public DeviceDTO save(DeviceDTO deviceDTO) {
        log.debug("Request to save Device : {}", deviceDTO);
        Device device = deviceMapper.toEntity(deviceDTO);
        device = deviceRepository.save(device);
        DeviceDTO result = deviceMapper.toDto(device);
        deviceSearchRepository.save(device);
        return result;
    }

    /**
     * Get all the devices.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<DeviceDTO> findAll() {
        log.debug("Request to get all Devices");
        return deviceRepository.findAll().stream()
            .map(deviceMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one device by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DeviceDTO> findOne(Long id) {
        log.debug("Request to get Device : {}", id);
        return deviceRepository.findById(id)
            .map(deviceMapper::toDto);
    }

    /**
     * Delete the device by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Device : {}", id);

        deviceRepository.deleteById(id);
        deviceSearchRepository.deleteById(id);
    }

    /**
     * Search for the device corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<DeviceDTO> search(String query) {
        log.debug("Request to search Devices for query {}", query);
        return StreamSupport
            .stream(deviceSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(deviceMapper::toDto)
        .collect(Collectors.toList());
    }
}
