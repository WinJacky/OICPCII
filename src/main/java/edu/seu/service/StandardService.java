package edu.seu.service;

import edu.seu.dao.StandardDao;
import edu.seu.model.Standard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StandardService {

    @Autowired
    private StandardDao standardDao;

    public Standard queryStandardByType(String type){
        return standardDao.selectStandard(type);
    }

    public Standard[] queryAll(){
        return standardDao.selectAll();
    }

    public Standard queryWeight(){
        return standardDao.selectWeight();
    }

    public void updateStandard(Standard standard){
        standardDao.updateStandard(standard);
    }

}
