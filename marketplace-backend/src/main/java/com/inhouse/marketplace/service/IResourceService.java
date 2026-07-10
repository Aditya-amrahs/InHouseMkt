package com.inhouse.marketplace.service;

import com.inhouse.marketplace.entity.Resource;
import java.util.List;

/**
 * Service contract for the unified Resource view (Requirements + Offers combined).
 */
public interface IResourceService {

    List<Resource> getAllResources(String category, String type);

    List<Resource> getAllResources(int empId);
}
