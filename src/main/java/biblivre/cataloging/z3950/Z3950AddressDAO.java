package biblivre.cataloging.z3950;

import java.util.List;

public interface Z3950AddressDAO {
    Z3950AddressDTO get(int id);

    List<Z3950AddressDTO> list();
}
