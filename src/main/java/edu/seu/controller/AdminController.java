package edu.seu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import edu.seu.base.CodeEnum;
import edu.seu.exceptions.OICPCIIExceptions;
import edu.seu.model.Standard;
import edu.seu.model.User;
import edu.seu.service.UserService;
import edu.seu.service.StandardService;
import edu.seu.util.ImportExcel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author wjx
 * @date 2020/2/15
 */
@RequestMapping("/admin")
@Controller
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    private String[] str = {"经贸合作区","加工制造园区","商贸物流园区","科技研发园区","其他园区","weight"};

    @Autowired
    private StandardService standardService;

    @Autowired
    private UserService userService;
    /**
     * 展示数据库中现存的权重和标准信息
     */
    @ResponseBody
    @RequestMapping("/showWS")
    public String showWS(){
        JSONArray array = new JSONArray();
        Standard[] standard = standardService.queryAll();
        for(int i = 0;i < 6;i++){
            JSONObject object = new JSONObject();
            object.put("occupancy", standard[i].getOccupancy());
            object.put("infrastructure", standard[i].getInfrastructure());
            object.put("depository", standard[i].getDepository());
            object.put("production", standard[i].getProduction());
            object.put("traffic", standard[i].getTraffic());
            object.put("green", standard[i].getGreen());
            array.add(object);
        }
        return JSON.toJSONString(array.toString());
    }

    /**
     * 是否管理员登录
     */
    public void adminAuth() throws OICPCIIExceptions {
        User user = userService.getCurrentUser();
        if (user == null || user.getIsAdmin() != 1) {
            throw new OICPCIIExceptions(CodeEnum.USER_ERROR, "此操作需要管理员权限！");
        }
    }

    /**
     * 更新数据库中的权重和标准信息(Excel文件输入)
     */
    @ResponseBody
    @RequestMapping("/updateWS")
    public String updateWS(MultipartFile file, HttpServletRequest request){

        try{
            //判断是否有管理员权限
            adminAuth();

            ImportExcel importExcel = new ImportExcel();
            //获取当前文件路径
            ServletContext context = request.getSession().getServletContext();
            String path = context.getRealPath("/fileupload");
            List<Standard> dataList = importExcel.read(path,file);

            //错误判断
            if (dataList == null) {
                LOGGER.error("管理员输入的文件为空！");
                return JSON.toJSONString("empty file");
            }
            Standard standard;
            for(int i = 0;i < dataList.size();i++){
                //更新信息
                standard = dataList.get(i);
                standard.setType(str[i]);

                standardService.updateStandard(standard);
            }
            return JSON.toJSONString("success");

        }catch(OICPCIIExceptions exception){
            LOGGER.error(exception.getMessage());
            return JSON.toJSONString("normal user");
        }catch(Exception e){
            LOGGER.error(e.getMessage());
            return JSON.toJSONString("unknown error");
        }
    }
}
