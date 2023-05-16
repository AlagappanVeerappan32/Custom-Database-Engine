package org.DatabaseSystem.querying;

import org.DatabaseSystem.querying.select.UserQueryInputs;

import java.io.IOException;
import java.util.List;

public interface SelectAndDeleteOperations {
    UserQueryInputs operatorConditions(String userQuery);
    String extractTableName(String userQuery);
    List<List<String>> checkDataTypes(UserQueryInputs userQueryInputs, List<String>tableValue, String metaTableLocation, String tableLocation,
                                      List<List<String>> addedValues) throws IOException;

}
