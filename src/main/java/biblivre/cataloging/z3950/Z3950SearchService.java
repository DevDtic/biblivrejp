package biblivre.cataloging.z3950;

import java.nio.file.Path;

public interface Z3950SearchService {
    Path search(Z3950AddressDTO address, String attribute, String query, int limit)
            throws Z3950SearchException;
}
