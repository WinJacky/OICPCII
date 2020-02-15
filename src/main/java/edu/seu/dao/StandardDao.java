package edu.seu.dao;

import edu.seu.model.Standard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StandardDao {

    String SELECT_FIELDS = "occupancy,infrastructure,depository,production,traffic,green";

    void updateStandard(Standard standard);

    @Select({"select",SELECT_FIELDS,"from standard where type=#{type}"})
    Standard selectStandard(String type);

    @Select({"select",SELECT_FIELDS,"from standard"})
    Standard[] selectAll();

    @Select({"select",SELECT_FIELDS,"from standard where type=\"weight\""})
    Standard selectWeight();

}
