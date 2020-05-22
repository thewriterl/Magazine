package com.pixelmags.web.rest;

import com.pixelmags.PixelmagsApp;
import com.pixelmags.domain.Magazine;
import com.pixelmags.repository.MagazineRepository;
import com.pixelmags.repository.search.MagazineSearchRepository;
import com.pixelmags.service.MagazineService;
import com.pixelmags.service.dto.MagazineDTO;
import com.pixelmags.service.mapper.MagazineMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link MagazineResource} REST controller.
 */
@SpringBootTest(classes = PixelmagsApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class MagazineResourceIT {

    private static final String DEFAULT_CODIGO_REVISTA = "AAAAAAAAAA";
    private static final String UPDATED_CODIGO_REVISTA = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRECO = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRECO = new BigDecimal(2);

    @Autowired
    private MagazineRepository magazineRepository;

    @Autowired
    private MagazineMapper magazineMapper;

    @Autowired
    private MagazineService magazineService;

    /**
     * This repository is mocked in the com.pixelmags.repository.search test package.
     *
     * @see com.pixelmags.repository.search.MagazineSearchRepositoryMockConfiguration
     */
    @Autowired
    private MagazineSearchRepository mockMagazineSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMagazineMockMvc;

    private Magazine magazine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Magazine createEntity(EntityManager em) {
        Magazine magazine = new Magazine()
            .codigoRevista(DEFAULT_CODIGO_REVISTA)
            .preco(DEFAULT_PRECO);
        return magazine;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Magazine createUpdatedEntity(EntityManager em) {
        Magazine magazine = new Magazine()
            .codigoRevista(UPDATED_CODIGO_REVISTA)
            .preco(UPDATED_PRECO);
        return magazine;
    }

    @BeforeEach
    public void initTest() {
        magazine = createEntity(em);
    }

    @Test
    @Transactional
    public void createMagazine() throws Exception {
        int databaseSizeBeforeCreate = magazineRepository.findAll().size();
        // Create the Magazine
        MagazineDTO magazineDTO = magazineMapper.toDto(magazine);
        restMagazineMockMvc.perform(post("/api/magazines").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(magazineDTO)))
            .andExpect(status().isCreated());

        // Validate the Magazine in the database
        List<Magazine> magazineList = magazineRepository.findAll();
        assertThat(magazineList).hasSize(databaseSizeBeforeCreate + 1);
        Magazine testMagazine = magazineList.get(magazineList.size() - 1);
        assertThat(testMagazine.getCodigoRevista()).isEqualTo(DEFAULT_CODIGO_REVISTA);
        assertThat(testMagazine.getPreco()).isEqualTo(DEFAULT_PRECO);

        // Validate the Magazine in Elasticsearch
        verify(mockMagazineSearchRepository, times(1)).save(testMagazine);
    }

    @Test
    @Transactional
    public void createMagazineWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = magazineRepository.findAll().size();

        // Create the Magazine with an existing ID
        magazine.setId(1L);
        MagazineDTO magazineDTO = magazineMapper.toDto(magazine);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMagazineMockMvc.perform(post("/api/magazines").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(magazineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Magazine in the database
        List<Magazine> magazineList = magazineRepository.findAll();
        assertThat(magazineList).hasSize(databaseSizeBeforeCreate);

        // Validate the Magazine in Elasticsearch
        verify(mockMagazineSearchRepository, times(0)).save(magazine);
    }


    @Test
    @Transactional
    public void getAllMagazines() throws Exception {
        // Initialize the database
        magazineRepository.saveAndFlush(magazine);

        // Get all the magazineList
        restMagazineMockMvc.perform(get("/api/magazines?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(magazine.getId().intValue())))
            .andExpect(jsonPath("$.[*].codigoRevista").value(hasItem(DEFAULT_CODIGO_REVISTA)))
            .andExpect(jsonPath("$.[*].preco").value(hasItem(DEFAULT_PRECO.intValue())));
    }
    
    @Test
    @Transactional
    public void getMagazine() throws Exception {
        // Initialize the database
        magazineRepository.saveAndFlush(magazine);

        // Get the magazine
        restMagazineMockMvc.perform(get("/api/magazines/{id}", magazine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(magazine.getId().intValue()))
            .andExpect(jsonPath("$.codigoRevista").value(DEFAULT_CODIGO_REVISTA))
            .andExpect(jsonPath("$.preco").value(DEFAULT_PRECO.intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingMagazine() throws Exception {
        // Get the magazine
        restMagazineMockMvc.perform(get("/api/magazines/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMagazine() throws Exception {
        // Initialize the database
        magazineRepository.saveAndFlush(magazine);

        int databaseSizeBeforeUpdate = magazineRepository.findAll().size();

        // Update the magazine
        Magazine updatedMagazine = magazineRepository.findById(magazine.getId()).get();
        // Disconnect from session so that the updates on updatedMagazine are not directly saved in db
        em.detach(updatedMagazine);
        updatedMagazine
            .codigoRevista(UPDATED_CODIGO_REVISTA)
            .preco(UPDATED_PRECO);
        MagazineDTO magazineDTO = magazineMapper.toDto(updatedMagazine);

        restMagazineMockMvc.perform(put("/api/magazines").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(magazineDTO)))
            .andExpect(status().isOk());

        // Validate the Magazine in the database
        List<Magazine> magazineList = magazineRepository.findAll();
        assertThat(magazineList).hasSize(databaseSizeBeforeUpdate);
        Magazine testMagazine = magazineList.get(magazineList.size() - 1);
        assertThat(testMagazine.getCodigoRevista()).isEqualTo(UPDATED_CODIGO_REVISTA);
        assertThat(testMagazine.getPreco()).isEqualTo(UPDATED_PRECO);

        // Validate the Magazine in Elasticsearch
        verify(mockMagazineSearchRepository, times(1)).save(testMagazine);
    }

    @Test
    @Transactional
    public void updateNonExistingMagazine() throws Exception {
        int databaseSizeBeforeUpdate = magazineRepository.findAll().size();

        // Create the Magazine
        MagazineDTO magazineDTO = magazineMapper.toDto(magazine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMagazineMockMvc.perform(put("/api/magazines").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(magazineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Magazine in the database
        List<Magazine> magazineList = magazineRepository.findAll();
        assertThat(magazineList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Magazine in Elasticsearch
        verify(mockMagazineSearchRepository, times(0)).save(magazine);
    }

    @Test
    @Transactional
    public void deleteMagazine() throws Exception {
        // Initialize the database
        magazineRepository.saveAndFlush(magazine);

        int databaseSizeBeforeDelete = magazineRepository.findAll().size();

        // Delete the magazine
        restMagazineMockMvc.perform(delete("/api/magazines/{id}", magazine.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Magazine> magazineList = magazineRepository.findAll();
        assertThat(magazineList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Magazine in Elasticsearch
        verify(mockMagazineSearchRepository, times(1)).deleteById(magazine.getId());
    }

    @Test
    @Transactional
    public void searchMagazine() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        magazineRepository.saveAndFlush(magazine);
        when(mockMagazineSearchRepository.search(queryStringQuery("id:" + magazine.getId())))
            .thenReturn(Collections.singletonList(magazine));

        // Search the magazine
        restMagazineMockMvc.perform(get("/api/_search/magazines?query=id:" + magazine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(magazine.getId().intValue())))
            .andExpect(jsonPath("$.[*].codigoRevista").value(hasItem(DEFAULT_CODIGO_REVISTA)))
            .andExpect(jsonPath("$.[*].preco").value(hasItem(DEFAULT_PRECO.intValue())));
    }
}
