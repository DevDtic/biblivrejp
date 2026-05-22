package biblivre.cataloging.z3950;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class Z3950AddressDAOImpl extends AbstractDAO implements Z3950AddressDAO {
    @Override
    public Z3950AddressDTO get(int id) {
        try (Connection con = datasource.getConnection()) {
            PreparedStatement pst =
                    con.prepareStatement(
                            "SELECT id, name, url, port, collection FROM z3950_addresses WHERE id = ?");

            pst.setInt(1, id);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return populateDTO(rs);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return null;
    }

    @Override
    public List<Z3950AddressDTO> list() {
        List<Z3950AddressDTO> list = new ArrayList<>();

        try (Connection con = datasource.getConnection()) {
            Statement st = con.createStatement();
            ResultSet rs =
                    st.executeQuery(
                            "SELECT id, name, url, port, collection FROM z3950_addresses ORDER BY name, id");

            while (rs.next()) {
                list.add(populateDTO(rs));
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }

        return list;
    }

    private Z3950AddressDTO populateDTO(ResultSet rs) throws SQLException {
        Z3950AddressDTO dto = new Z3950AddressDTO();

        dto.setId(rs.getInt("id"));
        dto.setName(rs.getString("name"));
        dto.setUrl(rs.getString("url"));
        dto.setPort(rs.getInt("port"));
        dto.setCollection(rs.getString("collection"));

        return dto;
    }
}
