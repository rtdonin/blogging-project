/*
Created by: Margaret Donin
Date created: 10/19/20
Date revised:
 */
package masteryddwa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import masteryddwa.dto.Hashtag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class HashtagDaoDB implements HashtagDao {

    @Autowired
    JdbcTemplate jdbc;

    @Transactional
    @Override
    public Hashtag addHashtag(Hashtag hashtag) {
        final String ADD_HASHTAG = "INSERT INTO Hashtag (name) VALUES (?);";
        jdbc.update(ADD_HASHTAG, hashtag.getTag());
        int newId = jdbc.queryForObject("select LAST_INSERT_ID()", Integer.class);
        hashtag.setId(newId);
        return hashtag;
    }

    @Override
    public Hashtag getHashtagById(int id) {
        try {
            final String SELECT_HASHTAG_BY_ID = "SELECT * FROM Hashtag WHERE hashtagId = ?";
            return jdbc.queryForObject(SELECT_HASHTAG_BY_ID, new HashtagMapper(), id);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Hashtag> getAllHashtags() {
        final String SELECT_HASHTAGS = "SELECT * FROM Hashtag;";
        return jdbc.query(SELECT_HASHTAGS, new HashtagMapper());
    }

    @Override
    public List<Hashtag> getAllHashtagsByPostId(int postId) {
        try {
            final String SELECT_HASHTAG_BY_ID
                    = "SELECT h.* "
                    + "FROM Hashtag h "
                    + "     JOIN PostHashtag ph "
                    + "ON h.hashtagId = ph.hashtagId "
                    + "WHERE ph.postId = ?;";
            return jdbc.query(SELECT_HASHTAG_BY_ID, new HashtagMapper(), postId);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public Hashtag getAllHashtagByTag(String tag) {
        try {
            final String SELECT_HASHTAG_BY_TAG
                    = "SELECT h.* "
                    + "FROM Hashtag h "
                    + "WHERE h.name = ?;";
            return jdbc.queryForObject(SELECT_HASHTAG_BY_TAG, new HashtagMapper(), tag);
        } catch (DataAccessException ex) {
            return null;
        }
    }
    
    @Override
    public void editHashtag(Hashtag hashtag) {
        final String EDIT_HASHTAG = "UPDATE Hashtag SET name = ? WHERE hashtagId = ?";
        jdbc.update(EDIT_HASHTAG, hashtag.getTag(), hashtag.getId());

    }

    @Override
    public void deleteHashtagById(int id) {
        final String DELETE_POST_HASHTAG = "DELETE FROM PostHashtag WHERE hashtagId = ?;";
        final String DELETE_HASHTAG = "DELETE FROM Hashtag WHERE hashtagId = ?;";
        jdbc.update(DELETE_POST_HASHTAG, id);
        jdbc.update(DELETE_HASHTAG, id);

    }

    protected static final class HashtagMapper implements RowMapper<Hashtag> {

        @Override
        public Hashtag mapRow(ResultSet rs, int i) throws SQLException {
            Hashtag hashtag = new Hashtag();
            hashtag.setId(rs.getInt("hashtagId"));
            hashtag.setTag(rs.getString("name"));
            return hashtag;
        }
    }
}
