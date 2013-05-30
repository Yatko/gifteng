package com.venefica.dao;

import com.venefica.model.AddressWrapper;

/**
 * Data access interface for {@link AddressWrapper} entity.
 *
 * @author gyuszi
 */
public interface AddressWrapperDao {

    /**
     * Saves the address in the database.
     *
     * @param addressWrapper addressWrapper to save
     * @return id of the stored addressWrapper
     */
    Long save(AddressWrapper addressWrapper);
}
