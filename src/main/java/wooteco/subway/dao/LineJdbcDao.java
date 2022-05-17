package wooteco.subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.domain.Line;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class LineJdbcDao implements LineDao {

    private final JdbcTemplate jdbcTemplate;

    public LineJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Line> lineRowMapper() {
        return (rs, rowNum) -> new Line(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("color"),
                rs.getInt("extraFare"));
    }

    @Override
    public Line save(Line line) {
        final String sql = "insert into Line (name, color, extraFare) values (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, line.getName());
            ps.setString(2, line.getColor());
            ps.setInt(3, line.getExtraFare());
            return ps;
        }, keyHolder);
        return new Line(keyHolder.getKey().longValue(), line.getName(), line.getColor(), line.getExtraFare());
    }

    @Override
    public boolean isExistById(Long id) {
        final String sql = "select exists(select * from line where id = (?)) ";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    @Override
    public boolean isExistByName(String name) {
        final String sql = "select exists(select * from line where name = (?)) ";
        return jdbcTemplate.queryForObject(sql, Boolean.class, name);
    }

    @Override
    public Line findById(Long id) {
        final String sql = "select * from line where id = (?)";
        return jdbcTemplate.queryForObject(sql, lineRowMapper(), id);
    }

    @Override
    public List<Line> findAll() {
        final String sql = "select * from line";
        return jdbcTemplate.query(sql, lineRowMapper());
    }

    @Override
    public int update(Long id, Line line) {
        final String sql = "update line set (name, color) = (?, ?) where id = ?";
        return jdbcTemplate.update(sql, line.getName(), line.getColor(), id);
    }

    @Override
    public int delete(Long id) {
        final String sql = "delete from line where id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
