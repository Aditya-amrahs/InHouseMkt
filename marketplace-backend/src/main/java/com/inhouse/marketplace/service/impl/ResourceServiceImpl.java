package com.inhouse.marketplace.service.impl;

import com.inhouse.marketplace.entity.Resource;
import com.inhouse.marketplace.repository.IResourceRepository;
import com.inhouse.marketplace.service.IResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of IResourceService — provides unified Resource browsing.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceServiceImpl implements IResourceService {

    private final IResourceRepository resourceRepository;

    @Override
    public List<Resource> getAllResources(String category, String type) {
        if (category != null && type != null) {
            return resourceRepository.findByCategoryAndType(category, type);
        }
        if (category != null) {
            return resourceRepository.findByCategory(category);
        }
        if (type != null) {
            return resourceRepository.findByType(type);
        }
        return resourceRepository.findAll();
    }

    @Override
    public List<Resource> getAllResources(int empId) {
        return resourceRepository.findAllByEmpId(empId);
    }
}
