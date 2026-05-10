package hms.interfaces;

import java.util.List;

/**
 * Generic DAO (Data Access Object) interface for CRUD operations
 * @param <T> the type of object this DAO handles
 * @param <ID> the type of the ID field
 */
public interface DataAccessObject<T, ID> {

    // Create operation
    boolean save(T entity);

    // Read operations
    T findById(ID id);
    List<T> findAll();
    List<T> findByProperty(String propertyName, Object value);

    // Update operation
    boolean update(T entity);

    // Delete operation
    boolean delete(ID id);

    // Utility method to check if an entity exists
    boolean exists(ID id);

    // Count the number of entities
    long count();
}

