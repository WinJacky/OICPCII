package edu.seu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import edu.seu.base.CodeEnum;
import edu.seu.base.CommonResponse;
import edu.seu.model.Standard;
import edu.seu.service.StandardService;
import edu.seu.util.ImportExcel;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wjx
 * @date 2020/2/14
 */
@RequestMapping("/calculate")
@Controller
public class CalculatorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculatorController.class);

    @Autowired
    StandardService standardService;

    /**
     * 进行用户填入的表中数据的计算并返回结果
     */
    @ResponseBody
    @RequestMapping("/table")
    public String calculateTable(HttpServletRequest request, HttpServletResponse response) {
        try {
            String customize = request.getParameter("customize");
            String arr = request.getParameter("array");
            String[] str = arr.substring(1, arr.length() - 1).split(",");
            double[] array = new double[str.length - 1];
            for (int i = 0; i < 6; i++) {
                array[i] = Double.parseDouble(str[i].substring(1, str[i].length() - 1));
            }
            //权重，作为goal函数计算的第二个参数
            Standard weight = new Standard();
            //标准值，作为goal函数计算的第一个参数
            Standard standard = new Standard();
            standard.setOccupancy(array[0]);
            standard.setInfrastructure(array[1]);
            standard.setDepository(array[2]);
            standard.setProduction(array[3]);
            standard.setTraffic(array[4]);
            standard.setGreen(array[5]);

            //自定义模式
            if ("是".equals(customize)) {
                standard.setType("其他园区");
                for (int i = 6; i < 12; i++) {
                    if (str[i].equals("")) {
                        array[i] = 0;
                    } else {
                        array[i] = Double.parseDouble(str[i].substring(1, str[i].length() - 1));
                    }
                }
                weight.setOccupancy(array[6]);
                weight.setInfrastructure(array[7]);
                weight.setDepository(array[8]);
                weight.setProduction(array[9]);
                weight.setTraffic(array[10]);
                weight.setGreen(array[11]);
            }
            //非自定义模式
            else {
                String type = request.getParameter("type");
                standard.setType(type);
                weight = standardService.queryWeight();
            }

            double goal = goal(standard,weight);
            response.addHeader("goal", String.valueOf(goal));
            return JSON.toJSONString(String.format("{'goal': %s}", String.format("%.4f", goal)));
        } catch (Exception e) {
            LOGGER.error("seu__" + e.getMessage());
            return new CommonResponse(CodeEnum.USER_ERROR.getValue(), e.getMessage()).toJSONString();
        }
    }

    /**
     * 根据用户所填表中相应数据输出文件到本地，再通过outputTableExcel函数返回给用户下载
     */
    @ResponseBody
    @RequestMapping("/outputTable")
    public void outputTable(HttpServletRequest request) {
        try {
            String customize = request.getParameter("customize");
            String arr = request.getParameter("array");

            String[] str = arr.substring(1, arr.length() - 1).split(",");
            double[] array = new double[str.length - 1];
            for (int i = 0; i < 6; i++) {
                array[i] = Double.parseDouble(str[i].substring(1, str[i].length() - 1));
            }

            //权重，作为goal函数计算的第二个参数
            Standard weight = new Standard();
            //标准值，作为goal函数计算的第一个参数
            Standard standard = new Standard();
            standard.setOccupancy(array[0]);
            standard.setInfrastructure(array[1]);
            standard.setDepository(array[2]);
            standard.setProduction(array[3]);
            standard.setTraffic(array[4]);
            standard.setGreen(array[5]);

            //获取当前文件路径
            ServletContext context = request.getSession().getServletContext();
            String realPath = context.getRealPath("/file");
            //若文件路径不存在，则创建
            File mkdir = new File(realPath);
            if (!mkdir.exists()) {
                mkdir.mkdirs();
            }

            //得到第一个shell
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("产城融合度评测表");

            //自定义模式
            if ("是".equals(customize)) {
                standard.setType("其他园区");
                for (int i = 6; i < 12; i++) {
                    if (str[i].equals("")) {
                        array[i] = 0;
                    } else {
                        array[i] = Double.parseDouble(str[i].substring(1, str[i].length() - 1));
                    }
                }
                weight.setOccupancy(array[6]);
                weight.setInfrastructure(array[7]);
                weight.setDepository(array[8]);
                weight.setProduction(array[9]);
                weight.setTraffic(array[10]);
                weight.setGreen(array[11]);
            }
            //非自定义模式
            else {
                String type = request.getParameter("type");
                standard.setType(type);
                weight = standardService.queryWeight();
            }

            //生成Excel表
            double goal = goal(standard,weight);
            tableExcelGenerator(sheet, Arrays.copyOfRange(array, 0, 6), weight, goal);
            //将文件输出到本地特定位置，供outputExcel返回给前端下载
            String fileName = "产城融合度评测表.xlsx";
            FileOutputStream fileOutputStream = new FileOutputStream(realPath + "/" + fileName);
            wb.write(fileOutputStream);
            fileOutputStream.close();

        } catch (Exception e) {
            LOGGER.error("wjx__" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Excel表数据填充函数
     */
    public void tableExcelGenerator(Sheet sheet, double[] data, Standard standard, double goal) {
        String[] str1 = {"指标", "数值", "权重", "产城融合指数"};
        String[] str2 = {"居住用地", "生活服务设施用地", "工业仓储用地", "生产配套设施用地", "道路与交通设施用地", "绿化用地"};

        double[] weight = new double[str2.length];
        weight[0] = standard.getOccupancy();
        weight[1] = standard.getInfrastructure();
        weight[2] = standard.getDepository();
        weight[3] = standard.getProduction();
        weight[4] = standard.getTraffic();
        weight[5] = standard.getGreen();

        Row row = sheet.createRow(0);
        for (int c = 0; c < 4; c++) {
            //生成第一行提示文字
            Cell cell = row.createCell(c);
            cell.setCellValue(str1[c]);
        }
        for (int r = 1; r <= 6; r++) {
            //生成最左列提示信息
            Row sheetRow = sheet.createRow(r);
            Cell cell = sheetRow.createCell(0);
            cell.setCellValue(str2[r - 1]);
            //生成数据信息
            Cell cellData = sheetRow.createCell(1);
            cellData.setCellValue(data[r - 1]);
            //生成权重信息
            Cell cellWeight = sheetRow.createCell(2);
            cellWeight.setCellValue(weight[r - 1]);
        }
        //生成计算结果
        row = sheet.getRow(1);
        Cell cellGoal = row.createCell(3);
        cellGoal.setCellValue(goal);
    }

    /**
     * 计算文件传输的结果，并且同时将用户可能要求输出的文件保存到本地
     */
    @ResponseBody
    @RequestMapping("/file")
    public String calculateFile(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam(value = "file") MultipartFile file, @RequestParam(value = "numCount") String numCount,
                                @RequestParam(value = "timeCount") String timeCount, @RequestParam(value = "typeCount") String typeCount, @RequestParam(value = "type") String type) throws IOException {
        //园区个数
        int park = Integer.parseInt(numCount);
        //时间序列数
        int year = Integer.parseInt(timeCount);
        //园区类型数
        int typeNum = Integer.parseInt(typeCount);

        /**
         * 读取上传文件
         */
        ImportExcel importExcel = new ImportExcel();
        //获取当前文件路径
        ServletContext context = request.getSession().getServletContext();
        String path = context.getRealPath("/fileupload");
        List<Standard> dataList = importExcel.read(path, file);
        List<Double> goalArray = new ArrayList<>();

        //错误判断
        if (dataList == null) {
            return new CommonResponse(CodeEnum.USER_ERROR.getValue(), "您输入的文件为空！").toJSONString();
        }
        if ((typeNum == 1) && (dataList.size() < year * park)) {
            return new CommonResponse(CodeEnum.USER_ERROR.getValue(), "文件中的数据条目不达标！请重新输入！").toJSONString();
        }
        if ((typeNum > 1) && (dataList.size() != (year + 2) * park)) {
            return new CommonResponse(CodeEnum.USER_ERROR.getValue(), "文件中的数据条目不达标！请重新输入！").toJSONString();
        }
        double goal;
        JSONArray array = new JSONArray();
        //如果为单个园区类型并省略权重信息时，则去数据库取出相应权重计算
        if ((typeNum == 1) && (dataList.size() == year * park)) {
            Standard weight = standardService.queryWeight();
            for (int i = 0; i < dataList.size(); i++) {
                //需要确定园区类型以计算
                dataList.get(i).setType(type);
                goal = goal(dataList.get(i), weight);
                JSONObject object = new JSONObject();
                object.put("year", year);
                object.put("zoneNum", "园区" + (i / year + 1));
                object.put("yearNum", (i % year + 1));
                object.put("goal", new DecimalFormat("#.0000").format(goal));
                array.add(object);
                goalArray.add(goal);
            }
        }
        //多园区类型(后面2*park个数据表示权重和标准)或单个园区类型且并未省略权重信息
        else {
            for (int i = 0; i < dataList.size() - 2 * park; i++) {
                //将园区类型设定为其他园区
                dataList.get(i).setType("其他园区");
                goal = goal(dataList.get(i), dataList.get(year * park + i / year));
                JSONObject object = new JSONObject();
                object.put("year", year);
                object.put("zoneNum", "园区" + (i / year + 1));
                object.put("yearNum", (i % year + 1));
                object.put("goal", new DecimalFormat("#.0000").format(goal));
                array.add(object);
                goalArray.add(goal);
            }
        }

        //生成文件到本地
        path = context.getRealPath("/file");
        //若文件路径不存在，则创建
        File mkdir = new File(path);
        if (!mkdir.exists()) {
            mkdir.mkdirs();
        }

        Workbook wb = new XSSFWorkbook();
        //得到第一个shell
        Sheet sheet = wb.createSheet("产城融合度评测表");
        fileExcelGenerator(sheet, dataList, goalArray);

        //将文件输出到本地特定位置，供outputExcel返回给前端下载
        String fileName = "产城融合度评测表.xlsx";
        FileOutputStream fileOutputStream = new FileOutputStream(path + "/" + fileName);
        wb.write(fileOutputStream);
        fileOutputStream.close();

        return JSON.toJSONString(array.toString());
    }

    /**
     * Excel文件数据填充函数
     */
    public void fileExcelGenerator(Sheet sheet, List<Standard> dataList, List<Double> goalArray) {
        String[] str = {"居住用地", "生活服务设施用地", "工业仓储用地", "生产配套设施用地", "道路与交通设施用地", "绿化用地", "产城融合指数"};

        for (int r = 0; r < str.length; r++) {
            //生成最左列提示信息
            Row row = sheet.createRow(r);
            Cell cell = row.createCell(0);
            cell.setCellValue(str[r]);

            for (int i = 1; i <= dataList.size(); i++) {
                Standard standard = dataList.get(i-1);
                cell = row.createCell(i);
                if (r == 0) {
                    cell.setCellValue(standard.getOccupancy());
                }else if (r == 1) {
                    cell.setCellValue(standard.getInfrastructure());
                }else if (r == 2) {
                    cell.setCellValue(standard.getDepository());
                }else if (r == 3) {
                    cell.setCellValue(standard.getProduction());
                }else if (r == 4) {
                    cell.setCellValue(standard.getTraffic());
                }else if (r == 5) {
                    cell.setCellValue(standard.getGreen());
                }else if (r == 6) {
                    if(i > goalArray.size()){
                        break;
                    }
                    cell.setCellValue(goalArray.get(i-1));
                }
            }
        }
    }

    /**
     * 返回给a标签以供用户下载
     */
    @ResponseBody
    @RequestMapping("/outputExcel")
    public ResponseEntity<byte[]> outputTableExcel(HttpServletRequest request) throws IOException {
        ServletContext context = request.getSession().getServletContext();
        String realPath = context.getRealPath("/file");

        String fileName = "产城融合度评测表.xlsx";
        File file = new File(realPath + "/" + fileName);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment", URLEncoder.encode(file.getName(), "UTF-8"));
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file), httpHeaders, HttpStatus.OK);

    }

    /**
     * 对于给定的计算公式归类出几种特定的计算模式
     */
    public double calculationMode(int mode,double proportion){
        double score = 0;
        if(mode == 0){
            if(proportion >= 0 && proportion < 10){
                score = proportion * 10;
            }else if(proportion >= 10 && proportion < 30){
                score = 150 - proportion * 5;
            }else if(proportion >= 30 && proportion <= 100){
                score = 0;
            }
        }else if(mode == 1){
            if(proportion >= 0 && proportion < 5){
                score = proportion * 20;
            }else if(proportion >= 5 && proportion < 15){
                score = 150 - proportion * 10;
            }else if(proportion >= 15 && proportion <= 100){
                score = 0;
            }
        }else if(mode == 2){
            if(proportion >= 0 && proportion < 60){
                score = proportion * 5 / 3;
            }else if(proportion >= 60 && proportion <= 100){
                score = 250 - proportion * 2.5;
            }
        }else if(mode == 3){
            if(proportion >= 0 && proportion < 3){
                score = proportion * 100 / 3;
            }else if(proportion >= 3 && proportion < 9){
                score = 150 - proportion * 100 / 6;
            }else if(proportion >= 9 && proportion <= 100){
                score = 0;
            }
        }else if(mode == 4){
            if(proportion >= 0 && proportion < 70){
                score = proportion * 10 / 7;
            }else if(proportion >= 70 && proportion <= 100){
                score = (100 - proportion) * 10 / 3;
            }
        }else if(mode == 5){
            if(proportion >= 0 && proportion < 7){
                score = proportion * 100 / 7;
            }else if(proportion >= 7 && proportion < 21){
                score = 150 - proportion * 50 / 7;
            }else if(proportion >= 21 && proportion <= 100){
                score = 0;
            }
        }else if(mode == 6){
            if(proportion >= 0 && proportion < 50){
                score = proportion * 2;
            }else if(proportion >= 50 && proportion <= 100){
                score = 200 - proportion * 2;
            }
        }else if(mode == 7){
            if(proportion >= 0 && proportion < 15){
                score = proportion * 20 / 3;
            }else if(proportion >= 15 && proportion < 45){
                score = 150 - proportion * 10 / 3;
            }else if(proportion >= 45 && proportion <= 100){
                score = 0;
            }
        }
        return score;
    }

    /**
     * 根据需求文档给定的计算公式进行相应指标层得分的计算
     */
    public double goal(Standard standard, Standard weight) {
        double occupancy = 0,infrastructure = 0,depository = 0,production = 0,traffic = 0,green = 0;
        String type = standard.getType();

        if(type.equals("经贸合作区") || type.equals("其他园区")){
            occupancy=calculationMode(0,standard.getOccupancy());
            infrastructure=calculationMode(1,standard.getInfrastructure());
            depository=calculationMode(2,standard.getDepository());
            production=calculationMode(1,standard.getProduction());
            traffic=calculationMode(0,standard.getTraffic());
            green=calculationMode(0,standard.getGreen());
        }else if(type.equals("加工制造园区") || type.equals("商贸物流园区")){
            occupancy=calculationMode(1,standard.getOccupancy());
            infrastructure=calculationMode(3,standard.getInfrastructure());
            depository=calculationMode(4,standard.getDepository());
            production=calculationMode(1,standard.getProduction());
            traffic=calculationMode(0,standard.getTraffic());
            green=calculationMode(5,standard.getGreen());
        }else if(type.equals("科技研发园区")){
            occupancy=calculationMode(0,standard.getOccupancy());
            infrastructure=calculationMode(0,standard.getInfrastructure());
            depository=calculationMode(6,standard.getDepository());
            production=calculationMode(1,standard.getProduction());
            traffic=calculationMode(0,standard.getTraffic());
            green=calculationMode(7,standard.getGreen());
        }
        return occupancy * weight.getOccupancy() +
                infrastructure * weight.getInfrastructure() +
                depository * weight.getDepository() +
                production * weight.getProduction() +
                traffic * weight.getTraffic() +
                green * weight.getGreen();
    }

//    //Json数据格式生成
//    public String toJsonString(int zoneNum,int yearNum,double goal){
//        return String.format("{'zoneNum':\"园区\"+%d, 'yearNum':\"第\"+%d+\"年\", 'goal':%.4f}",zoneNum,yearNum,goal);
//    }

}
