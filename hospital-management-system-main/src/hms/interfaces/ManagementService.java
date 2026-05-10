package hms.interfaces;

import java.util.List;

/**
 * Generic interface for service-layer management operations
 */
public interface ManagementService<T, ID> {
    // Basic CRUD operations
    boolean add(T entity);
    T getById(ID id);
    List<T> getAll();
    boolean update(T entity);
    boolean delete(ID id);

    // Search functionality
    List<T> search(String query);
}
