package hms.dao;

import hms.interfaces.DataAccessObject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Abstract base class for file-based DAO implementations
 */
public abstract class FileBasedDAO<T, ID> implements DataAccessObject<T, ID> {

    protected String filePath;

    public FileBasedDAO(String filePath) {
        this.filePath = filePath;
        initializeFile();
    }

    // Initialize the file if it doesn't exist
    private void initializeFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to initialize data file: " + filePath, e);
            }
        }
    }

    @Override
    public boolean save(T entity) {
        List<T> entities = findAll();
        entities.add(entity);
        return writeToFile(entities);
    }

    @Override
    public T findById(ID id) {
        List<T> entities = findAll();
        for (T entity : entities) {
            if (getIdFromEntity(entity).equals(id)) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    T entity = parseEntity(line);
                    if (entity != null) {
                        entities.add(entity);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entities;
    }

    @Override
    public List<T> findByProperty(String propertyName, Object value) {
        List<T> entities = findAll();
        List<T> results = new ArrayList<>();

        for (T entity : entities) {
            if (matchesProperty(entity, propertyName, value)) {
                results.add(entity);
            }
        }

        return results;
    }

    @Override
    public boolean update(T entity) {
        List<T> entities = findAll();
        boolean updated = false;

        for (int i = 0; i < entities.size(); i++) {
            if (getIdFromEntity(entities.get(i)).equals(getIdFromEntity(entity))) {
                entities.set(i, entity);
                updated = true;
                break;
            }
        }

        if (updated) {
            return writeToFile(entities);
        }
        return false;
    }

    @Override
    public boolean delete(ID id) {
        List<T> entities = findAll();
        boolean removed = entities.removeIf(entity -> getIdFromEntity(entity).equals(id));

        if (removed) {
            return writeToFile(entities);
        }
        return false;
    }

    @Override
    public boolean exists(ID id) {
        return findById(id) != null;
    }

    @Override
    public long count() {
        return findAll().size();
    }

    // Helper method to write entities to file
    protected boolean writeToFile(List<T> entities) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (T entity : entities) {
                writer.write(entityToFileString(entity));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to find entities based on a predicate
    public List<T> findByPredicate(Predicate<T> predicate) {
        List<T> entities = findAll();
        List<T> results = new ArrayList<>();

        for (T entity : entities) {
            if (predicate.test(entity)) {
                results.add(entity);
            }
        }

        return results;
    }

    // Abstract methods to be implemented by subclasses
    protected abstract T parseEntity(String line);
    protected abstract String entityToFileString(T entity);
    protected abstract ID getIdFromEntity(T entity);
    protected abstract boolean matchesProperty(T entity, String propertyName, Object value);
}
