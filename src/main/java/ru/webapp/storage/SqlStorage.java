package ru.webapp.storage;

import com.google.gson.Gson;
import ru.webapp.exception.NotExistStorageException;
import ru.webapp.exception.StorageException;
import ru.webapp.model.*;
import ru.webapp.sql.SqlHelper;
import ru.webapp.util.JsonParser;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SqlStorage implements Storage {
    public final SqlHelper sqlHelper;
    private final Gson gson = JsonParser.GSON;


    public SqlStorage(String dbUrl, String dbUser, String dbPassword) {
        sqlHelper = new SqlHelper(() -> DriverManager.getConnection(dbUrl, dbUser, dbPassword));
    }

    @Override
    public int size() {
        return sqlHelper.execute("SELECT COUNT(*) as size FROM resume", ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("size");
            }
        });
    }

    @Override
    public void clear() {
        sqlHelper.execute("TRUNCATE TABLE resume CASCADE");
    }

    @Override
    public void update(Resume r) {
        sqlHelper.transactionalExecute(conn -> {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE resume SET full_name = ? WHERE uuid = ?::uuid")) {
                ps.setString(1, r.getFullName());
                ps.setString(2, r.getUuid());
                int countUpdate = ps.executeUpdate();
                if (countUpdate == 0) {
                    throw new NotExistStorageException(r.getUuid());
                }
            }
            deleteContacts(r, conn);
            insertContacts(r, conn);
            deleteSections(r, conn);
            insertSections(r, conn);
            return null;
        });
    }

    @Override
    public void save(Resume r) {
        sqlHelper.transactionalExecute(conn -> {
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO resume(uuid, full_name) VALUES (?::uuid,?) ")) {
                        ps.setString(1, (r.getUuid()));
                        ps.setString(2, r.getFullName());
                        ps.executeUpdate();
                    }
                    insertContacts(r, conn);
                    insertSections(r, conn);
                    return null;
                }
        );
    }

    @Override
    public Resume get(String uuid) {
        return sqlHelper.execute("" +
                        "SELECT * " +
                        "FROM resume AS r " +
                        "LEFT JOIN contact c " +
                        "ON r.uuid = c.resume_uuid " +
                        "LEFT JOIN section s " +
                        "ON r.uuid = s.resume_uuid " +
                        "WHERE r.uuid =?::uuid",
                ps -> {
                    ps.setString(1, uuid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new NotExistStorageException(uuid);
                        }
                        Resume r = new Resume(uuid, rs.getString("full_name"));
                        do {
                            addContact(rs, r);
                            addSection(rs, r);
                        } while (rs.next());
                        return r;
                    }
                });
    }

    @Override
    public void delete(String uuid) {
        sqlHelper.execute("DELETE FROM resume WHERE uuid = ?::uuid ", ps -> {
            ps.setString(1, uuid);
            int countUpdate = ps.executeUpdate();
            if (countUpdate == 0) {
                throw new NotExistStorageException(uuid);
            }
            return null;
        });
    }

    @Override
    public List<Resume> getAllSorted() {
        return sqlHelper.execute("" +
                "SELECT * " +
                "FROM resume AS r " +
                "LEFT JOIN contact AS c " +
                "ON c.resume_uuid = r.uuid " +
                "LEFT JOIN section s " +
                "ON r.uuid = s.resume_uuid " +
                "ORDER BY full_name, uuid", ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, Resume> resumes = new LinkedHashMap<>();
                while (rs.next()) {
                    String uuid = rs.getString("uuid");
                    Resume r = resumes.get(uuid);
                    if (r == null) {
                        r = new Resume(uuid, rs.getString("full_name"));
                        resumes.put(uuid, r);
                    }
                    addContact(rs, r);
                    addSection(rs, r);
                }
                return new ArrayList<>(resumes.values());
            } catch (SQLException e) {
                throw new StorageException(e);
            }
        });
    }

    private void insertContacts(Resume r, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO contact(resume_uuid, type_contact, value_contact) VALUES (?::uuid,?::contact_type,?) ")) {
            for (Map.Entry<ContactType, String> e : r.getContacts().entrySet()) {
                ps.setString(1, r.getUuid());
                ps.setString(2, e.getKey().name());
                ps.setString(3, e.getValue());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteContacts(Resume r, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM contact WHERE resume_uuid = ?::uuid")) {
            ps.setString(1, r.getUuid());
            ps.execute();
        }
    }

    private void addContact(ResultSet rs, Resume r) throws SQLException {
        String t = rs.getString("type_contact");
        if (t != null) {
            ContactType type = ContactType.valueOf(t);
            String value = rs.getString("value_contact");
            r.addContact(type, value);
        }
    }

    private void insertSections(Resume r, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO section(resume_uuid, type_section, value_section) VALUES (?::uuid,?::section_type,?::jsonb) ")) {
            for (Map.Entry<SectionType, Section> e : r.getSections().entrySet()) {
                ps.setString(1, r.getUuid());
                SectionType type = e.getKey();
                ps.setString(2, type.name());
                ps.setString(3, gson.toJson(e.getValue()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteSections(Resume r, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM section WHERE resume_uuid = ?::uuid")) {
            ps.setString(1, r.getUuid());
            ps.execute();
        }
    }

    private void addSection(ResultSet rs, Resume r) throws SQLException {
        String t = rs.getString("type_section");
        if (t != null) {
            SectionType st = SectionType.valueOf(t);
            switch (st) {
                case PERSONAL, OBJECTIVE ->
                        r.addSection(st, gson.fromJson(rs.getString("value_section"), TextSection.class));
                case ACHIEVEMENT, QUALIFICATIONS ->
                        r.addSection(st, gson.fromJson(rs.getString("value_section"), ListSection.class));
                case EXPERIENCE, EDUCATION ->
                        r.addSection(st, gson.fromJson(rs.getString("value_section"), OrganizationSection.class));
            }
        }
    }
}

