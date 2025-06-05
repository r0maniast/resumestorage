package ru.webapp.storage;

import ru.webapp.exception.NotExistStorageException;
import ru.webapp.exception.StorageException;
import ru.webapp.model.ContactType;
import ru.webapp.model.Resume;
import ru.webapp.sql.SqlHelper;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlStorage implements Storage {
    public final SqlHelper sqlHelper;

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

        sqlHelper.execute("UPDATE resume SET full_name =? WHERE uuid =?", ps -> {
            ps.setString(1, r.getFullName());
            ps.setString(2, r.getUuid());
            int countUpdate = ps.executeUpdate();
            if (countUpdate == 0) {
                throw new NotExistStorageException(r.getUuid());
            }
            return null;
        });
    }

    @Override
    public void save(Resume r) {
        sqlHelper.transactionalExecute(conn -> {
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO resume(uuid, full_name) VALUES (?,?) ")) {
                        ps.setString(1, r.getUuid());
                        ps.setString(2, r.getFullName());
                        ps.execute();
                    }
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO contact(resume_uuid, type, value) VALUES (?,?,?) ")) {
                        for (Map.Entry<ContactType, String> e : r.getContacts().entrySet()) {
                            ps.setString(1, r.getUuid());
                            ps.setString(2, e.getKey().name());
                            ps.setString(3, e.getValue());
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
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
                        "ON c.resume_uuid = r.uuid " +
                        "WHERE r.uuid =?",
                ps -> {
                    ps.setString(1, uuid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new NotExistStorageException(uuid);
                        }
                        Resume r = new Resume(uuid, rs.getString("full_name"));
                        do {
                            ContactType type = ContactType.valueOf(rs.getString("type"));
                            String value = rs.getString("value");
                            r.addContact(type, value);
                        } while (rs.next());
                        return r;
                    }
                });
    }

    @Override
    public void delete(String uuid) {
        sqlHelper.execute("DELETE FROM resume WHERE uuid = ? ", ps -> {
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
        return sqlHelper.execute("SELECT * FROM resume ORDER BY full_name, uuid", ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                List<Resume> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new Resume(rs.getString("uuid"), rs.getString("full_name")));
                }
                return list;
            } catch (SQLException e) {
                throw new StorageException(e);
            }
        });
    }

}
