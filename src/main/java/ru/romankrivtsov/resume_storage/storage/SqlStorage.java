package ru.romankrivtsov.resume_storage.storage;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.romankrivtsov.resume_storage.exception.NotExistStorageException;
import ru.romankrivtsov.resume_storage.exception.StorageException;
import ru.romankrivtsov.resume_storage.model.*;
import ru.romankrivtsov.resume_storage.sql.SqlHelper;
import ru.romankrivtsov.resume_storage.util.JsonParser;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SqlStorage implements Storage {
    private static final Logger log = LoggerFactory.getLogger(SqlStorage.class);
    public final SqlHelper sqlHelper;
    private final Gson gson = JsonParser.GSON;

    public SqlStorage(String dbUrl, String dbUser, String dbPassword) {
        log.info("Initializing SqlStorage with URL={} user={}", dbUrl, dbUser);
        try {
            Class.forName("org.postgresql.Driver");
            log.debug("PostgreSQL Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            log.error("PostgreSQL Driver not found", e);
            throw new IllegalStateException(e);
        }
        sqlHelper = new SqlHelper(() -> {
            try {
                log.debug("Opening database connection");
                return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            } catch (SQLException e) {
                log.error("Failed to open database connection", e);
                throw new StorageException(e);
            }
        });
        log.info("SqlHelper initialized");
    }

    @Override
    public int size() {
        log.debug("Executing size() query");
        int result = sqlHelper.execute("SELECT COUNT(*) as size FROM resume", ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                int size = rs.getInt("size");
                log.debug("Size query result: {} rows", size);
                return size;
            }
        });
        log.info("Total resumes count: {}", result);
        return result;
    }

    @Override
    public void clear() {
        log.info("Clearing all resumes from database");
        sqlHelper.execute("TRUNCATE TABLE resume CASCADE");
        log.debug("Database cleared");
    }

    @Override
    public void update(Resume r) {
        log.info("Updating resume: {}", r.getUuid());
        sqlHelper.transactionalExecute(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE resume SET full_name = ? WHERE uuid = ?::uuid")) {
                ps.setString(1, r.getFullName());
                ps.setString(2, r.getUuid());
                int countUpdate = ps.executeUpdate();
                log.debug("Update resume affected rows: {}", countUpdate);
                if (countUpdate == 0) {
                    log.warn("Resume not found for update: {}", r.getUuid());
                    throw new NotExistStorageException(r.getUuid());
                }
            }
            deleteContacts(r, conn);
            insertContacts(r, conn);
            deleteSections(r, conn);
            insertSections(r, conn);
            log.debug("Update transaction completed for resume: {}", r.getUuid());
            return null;
        });
        log.info("Resume updated successfully: {}", r.getUuid());
    }

    @Override
    public void save(Resume r) {
        log.info("Saving new resume: {}", r.getUuid());
        sqlHelper.transactionalExecute(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO resume(uuid, full_name) VALUES (?::uuid,?)")) {
                ps.setString(1, r.getUuid());
                ps.setString(2, r.getFullName());
                ps.executeUpdate();
                log.debug("Inserted resume row: uuid={}, fullName={}", r.getUuid(), r.getFullName());
            }
            insertContacts(r, conn);
            insertSections(r, conn);
            log.debug("Save transaction completed for resume: {}", r.getUuid());
            return null;
        });
        log.info("Resume saved successfully: {}", r.getUuid());
    }

    @Override
    public Resume get(String uuid) {
        log.info("Retrieving resume: {}", uuid);
        Resume result = sqlHelper.execute(
                "SELECT * FROM resume AS r LEFT JOIN contact c ON r.uuid = c.resume_uuid " +
                        "LEFT JOIN section s ON r.uuid = s.resume_uuid WHERE r.uuid =?::uuid",
                ps -> {
                    ps.setString(1, uuid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            log.warn("Resume not found: {}", uuid);
                            throw new NotExistStorageException(uuid);
                        }
                        Resume r = new Resume(uuid, rs.getString("full_name"));
                        do {
                            addContact(rs, r);
                            addSection(rs, r);
                        } while (rs.next());
                        log.debug("Retrieved resume: {} with {} contacts and {} sections",
                                uuid, r.getContacts().size(), r.getSections().size());
                        return r;
                    }
                });
        log.info("Resume retrieved successfully: {}", uuid);
        return result;
    }

    @Override
    public void delete(String uuid) {
        log.info("Deleting resume: {}", uuid);
        sqlHelper.execute("DELETE FROM resume WHERE uuid = ?::uuid", ps -> {
            ps.setString(1, uuid);
            int countUpdate = ps.executeUpdate();
            log.debug("Delete resume affected rows: {}", countUpdate);
            if (countUpdate == 0) {
                log.warn("Resume not found for delete: {}", uuid);
                throw new NotExistStorageException(uuid);
            }
            return null;
        });
        log.info("Resume deleted successfully: {}", uuid);
    }

    @Override
    public List<Resume> getAllSorted() {
        log.info("Retrieving all resumes sorted");
        List<Resume> resumes = sqlHelper.execute(
                "SELECT * FROM resume AS r LEFT JOIN contact AS c ON c.resume_uuid = r.uuid " +
                        "LEFT JOIN section s ON r.uuid = s.resume_uuid ORDER BY full_name, uuid",
                ps -> {
                    try (ResultSet rs = ps.executeQuery()) {
                        Map<String, Resume> map = new LinkedHashMap<>();
                        while (rs.next()) {
                            String uuid = rs.getString("uuid");
                            Resume r = map.get(uuid);
                            if (r == null) {
                                r = new Resume(uuid, rs.getString("full_name"));
                                map.put(uuid, r);
                            }
                            addContact(rs, r);
                            addSection(rs, r);
                        }
                        log.debug("Retrieved {} resumes", map.size());
                        return new ArrayList<>(map.values());
                    } catch (SQLException e) {
                        log.error("Error retrieving all resumes", e);
                        throw new StorageException(e);
                    }
                });
        log.info("All resumes retrieved successfully, total count: {}", resumes.size());
        return resumes;
    }

    private void insertContacts(Resume r, Connection conn) throws SQLException {
        log.debug("Inserting {} contacts for resume: {}", r.getContacts().size(), r.getUuid());
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO contact(resume_uuid, type_contact, value_contact) VALUES (?::uuid,?::contact_type,?)")) {
            for (Map.Entry<ContactType, String> e : r.getContacts().entrySet()) {
                ps.setString(1, r.getUuid());
                ps.setString(2, e.getKey().name());
                ps.setString(3, e.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
            log.trace("Contacts batch executed for resume: {}", r.getUuid());
        }
    }

    private void deleteContacts(Resume r, Connection conn) throws SQLException {
        log.debug("Deleting contacts for resume: {}", r.getUuid());
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM contact WHERE resume_uuid = ?::uuid")) {
            ps.setString(1, r.getUuid());
            ps.execute();
            log.trace("Contacts deleted for resume: {}", r.getUuid());
        }
    }

    private void addContact(ResultSet rs, Resume r) throws SQLException {
        String t = rs.getString("type_contact");
        if (t != null) {
            ContactType type = ContactType.valueOf(t);
            String value = rs.getString("value_contact");
            r.setContact(type, value);
            log.trace("Added contact {}={} to resume {}", type, value, r.getUuid());
        }
    }

    private void insertSections(Resume r, Connection conn) throws SQLException {
        log.debug("Inserting {} sections for resume: {}", r.getSections().size(), r.getUuid());
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO section(resume_uuid, type_section, value_section) VALUES (?::uuid,?::section_type,?::jsonb)")) {
            for (Map.Entry<SectionType, Section> e : r.getSections().entrySet()) {
                ps.setString(1, r.getUuid());
                SectionType type = e.getKey();
                ps.setString(2, type.name());
                ps.setString(3, gson.toJson(e.getValue()));
                ps.addBatch();
            }
            ps.executeBatch();
            log.trace("Sections batch executed for resume: {}", r.getUuid());
        }
    }

    private void deleteSections(Resume r, Connection conn) throws SQLException {
        log.debug("Deleting sections for resume: {}", r.getUuid());
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM section WHERE resume_uuid = ?::uuid")) {
            ps.setString(1, r.getUuid());
            ps.execute();
            log.trace("Sections deleted for resume: {}", r.getUuid());
        }
    }

    private void addSection(ResultSet rs, Resume r) throws SQLException {
        String t = rs.getString("type_section");
        if (t != null) {
            SectionType st = SectionType.valueOf(t);
            switch (st) {
                case PERSONAL, OBJECTIVE -> {
                    r.setSection(st, gson.fromJson(rs.getString("value_section"), TextSection.class));
                    log.trace("Added text section {} to resume {}", st, r.getUuid());
                }
                case ACHIEVEMENT, QUALIFICATIONS -> {
                    r.setSection(st, gson.fromJson(rs.getString("value_section"), ListSection.class));
                    log.trace("Added list section {} to resume {}", st, r.getUuid());
                }
                case EXPERIENCE, EDUCATION -> {
                    r.setSection(st, gson.fromJson(rs.getString("value_section"), OrganizationSection.class));
                    log.trace("Added organization section {} to resume {}", st, r.getUuid());
                }
            }
        }
    }
}
