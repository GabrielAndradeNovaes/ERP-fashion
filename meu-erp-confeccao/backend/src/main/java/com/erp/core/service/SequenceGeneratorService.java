package com.erp.core.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class SequenceGeneratorService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public String getNextSequence(String tableName, String columnName) {
        String sql = "SELECT COALESCE(MAX(CAST(NULLIF(REGEXP_REPLACE(" + columnName + ", '[^0-9]', '', 'g'), '') AS INTEGER)), 0) + 1 " +
                     "FROM " + tableName;
                     
        Query query = entityManager.createNativeQuery(sql);
        
        Object result = query.getSingleResult();
        if (result == null) {
            return "1";
        }
        return result.toString();
    }
}
