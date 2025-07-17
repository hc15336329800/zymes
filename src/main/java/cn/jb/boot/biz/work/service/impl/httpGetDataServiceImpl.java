package cn.jb.boot.biz.work.service.impl;

import cn.jb.boot.biz.tray.mapper.TrayManageInfoMapper;
import cn.jb.boot.biz.work.mapper.WorkOrderMapper;
import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * 接口形式获取数据
 *
 * @author lxl
 * @Description
 * @Copyright Copyright (c) 2024
 * @since 2024-04-17 21:52:39
 */
@Service
public class httpGetDataServiceImpl {

    @Resource
    WorkOrderMapper workOrderMapper;
    @Resource
    TrayManageInfoMapper trayManageInfoMapper;
    public static int globalVar = 0;

    /**
     * 获取所有设备的共享文件夹地址及文件
     *
     * @param
     * @retrun
     */
    public void getAllDeviceUrl() {
        List<Map<String, Object>> deviceUrlList = trayManageInfoMapper.getAllDeviceUrl();
        for (Map<String, Object> deviceUr : deviceUrlList) {
            String fileType = deviceUr.get("fileType").toString();//文件类型
            String deviceName = deviceUr.get("deviceName").toString();//设备名称
            String host = deviceUr.get("ip").toString();//ip
            String shareFolder = deviceUr.get("shareFolder").toString();//共享文件夹
            if (fileType.equals("pdf") && deviceName.contains("火焰切割机")) {
                getDataFromPDF(host, shareFolder, deviceName);
            } else if (fileType.equals("pdf") && deviceName.contains("切管机")) {
                getCutterFromPDF(host, shareFolder, deviceName);
            } else if (fileType.equals("csv") && !deviceName.contains("焊接")) {
                getDataFromCSV(host, shareFolder, deviceName);
            } else if (fileType.equals("csv") && deviceName.contains("焊接")) {
                getRobotDataFromCSV(host, shareFolder, deviceName);
            }
        }
    }

    /**
     * <b>将csv文件通过IO流解析，转化数组形式的集合<b>
     */
    public void getDataFromCSV(String ip, String shareFolder, String deviceName) {
        try {
//            String folderPath = "\\\\192.168.0.111\\共享文件"; // CSV文件的位置
            String folderPath = "\\\\" + ip + "\\" + shareFolder;
            File folder = new File(folderPath);
            File[] listOfFiles = folder.listFiles();

            //如果文件夹没有文件则不做处理
            if (listOfFiles == null || listOfFiles.length == 0) {
                return;
            }
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    if (!fileName.contains("csv")) {
                        continue;
                    }
                    Map<String, Object> csvMap = csv(folderPath + "/" + fileName);
                    List<String[]> csv = (List) csvMap.get("csvList");
                    InputStreamReader is = (InputStreamReader) csvMap.get("is");
                    CSVReader reader = (CSVReader) csvMap.get("reader");
                    for (String[] strings : csv) {
                        String partNo = strings[6].trim();//零件号
                        String[] partSplit = partNo.split("-");
                        String bomNo = partSplit[0].substring(0, partSplit[0].length() - 1);
                        String number = strings[7].trim();//数量
//                        String location = strings[7].trim();//库位
                        //查询对应的托盘
                        Map<String, Object> taryParam = new HashMap<>();
                        taryParam.put("deviceName", deviceName);
//                        taryParam.put("location",location);
                        Map<String, Object> taryData = trayManageInfoMapper.getTrayData(taryParam);
                        //根据设备和零件号对应已下达的物料
                        if (taryData != null) {
                            Map<String, Object> param = new HashMap<>();
                            param.put("deviceName", deviceName);
                            param.put("bomNo", bomNo);
                            param.put("location", taryData.get("location_code"));
                            param.put("realCount", number);
                            param.put("trayid", taryData.get("trayid"));
                            workOrderMapper.updateItemand(param);
                        }
                    }
                    //关闭流
                    reader.close();
                    is.close();
                    //将读取完的文件重命名
                    String newFileName = file.getName() + ".temp";
                    File newFile = new File(file.getParent() + File.separator + newFileName);
                    file.renameTo(newFile);
                    //读取完以后删除文件
//                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置编码格式、数据流等
    public static Map<String, Object> csv(String fileName) {
        List<String[]> csvList = new ArrayList<String[]>();
        Map<String, Object> returnData = new HashMap<>();
        if (null != fileName) {
            try {
                InputStreamReader is = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
                CSVParser csvParser = new CSVParserBuilder().build();
                CSVReader reader = new CSVReaderBuilder(is).withCSVParser(csvParser).build();
                csvList = reader.readAll();
                returnData.put("csvList", csvList);
                returnData.put("is", is);
                returnData.put("reader", reader);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return returnData;
    }

    public String getDataFromModbus(String ip) {
        String returnValue = "";
        //设置主机TCP参数
        TcpParameters tcpParameters = new TcpParameters();
        //设置TCP的ip地址和本地地址
        InetAddress address = null;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        tcpParameters.setHost(address);

        //Tcp设置长连接
        tcpParameters.setKeepAlive(true);
        //TCP设置端口，502
        tcpParameters.setPort(502);

        //创建一个主机
        ModbusMaster master = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
        Modbus.setAutoIncrementTransactionId(true);

        int slaveId = 1;//从基地址
        int offset = 757;//寄存器读取地址(已完成数量)
        int quantity = 1;//读取的寄存器数量

        System.out.println("准备执行开启连接");
        try {
            if (!master.isConnected()) {
                master.connect();
            }
            System.out.println("读取对应从机的数据");
            //功能码01 readCoils()
            //功能码02 readDiscreteInputs()
            //功能码03 readHoldingRegisters()
            //功能码04 readInputRegisters()
            int[] registerValues = master.readHoldingRegisters(slaveId, offset, quantity);
            System.out.println("成功！");
            //控制台输出
            for (int value : registerValues) {
                System.out.println("Address:" + offset++ + ",Value:" + value);
                returnValue = String.valueOf(value);
            }
        } catch (ModbusProtocolException e) {
            e.printStackTrace();
        } catch (ModbusNumberException e) {
            e.printStackTrace();
        } catch (ModbusIOException e) {
            e.printStackTrace();
        } finally {
            try {
                master.disconnect();
            } catch (ModbusIOException e) {
                e.printStackTrace();
            }
        }
        return returnValue;
    }

    //火焰切割机
    public void getDataFromPDF(String ip, String shareFolder, String deviceName) {
//        String folderPath = "\\\\192.168.0.120\\Users"; // 火焰切割机的路径
        String folderPath = "\\\\" + ip + "\\" + shareFolder; // 火焰切割机的路径
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        //如果文件夹没有文件则不做处理
        if (listOfFiles == null || listOfFiles.length == 0) {
            return;
        }
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.contains("pdf")) {
                    readPDF(folderPath + "\\" + fileName, deviceName);
                }
                //读取完以后删除文件
                file.delete();
            }
        }
    }

    public void readPDF(String pdfFilePath, String deviceName) {
        String result = "";
        try {
            // 加载PDF文档
            PDDocument load = PDDocument.load(new File(pdfFilePath));
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            for (int i = 1; i < load.getNumberOfPages() + 1; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String text = stripper.getText(load);
                //拼接不同页数的数据返回
                result += text;
            }
            //获取设备信息及数量
            int indexFrom = result.indexOf("零件索引");
            int indexEnd = result.indexOf("板 材 信 息");
            String str = result.substring(indexFrom, indexEnd).replace("套料报表", "");
            str = str.replace("零件索引", "");
            String[] split = str.split("\r\n");
            for (int j = 1; j < split.length; j++) {
                String[] deviceDataLine = split[j].split(" ");
                String partNo = deviceDataLine[1].trim();//零件号
                String[] partSplit = partNo.split("-");
                String number = deviceDataLine[3].trim();//零件数量
                //根据设备和零件号对应已下达的物料
                Map<String, Object> param = new HashMap<>();
                param.put("deviceName", deviceName);
                param.put("bomNo", partSplit[0]);
                param.put("realCount", number);
                workOrderMapper.updateItemandByBom(param);
            }
            load.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //激光切管机
    public void getCutterFromPDF(String ip, String shareFolder, String deviceName) {
//        String folderPath = "D:/中钰项目/设备对接/切割机PDF文件(1)/激光切管机/激光切管机"; // 火焰切割机的路径
        String folderPath = "\\\\" + ip + "\\" + shareFolder;
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        //如果文件夹没有文件则不做处理
        if (listOfFiles == null || listOfFiles.length == 0) {
            return;
        }
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (!fileName.contains("pdf")) {
                    continue;
                }
                getCutterReadPDF(folderPath + "\\" + fileName, deviceName);
                //读取完以后删除文件
                file.delete();
            }
        }
    }

    public void getCutterReadPDF(String pdfFilePath, String deviceName) {
        String result = "";
        try {
            // 加载PDF文档
            PDDocument load = PDDocument.load(new File(pdfFilePath));
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            for (int i = 1; i < load.getNumberOfPages() + 1; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String text = stripper.getText(load);
                //拼接不同页数的数据返回
                result += text;
            }
            //获取设备信息及数量
            int indexFrom = result.indexOf("零件长度");
            int indexEnd = result.indexOf("管材信息");
            String str = result.substring(indexFrom, indexEnd).replace("零件长度", "");
            String[] split = str.split("\r\n");
            for (int j = 1; j < split.length; j++) {
                String[] deviceDataLine = split[j].split(" ");
                String partNo = deviceDataLine[1].trim();//零件号
                if (partNo.contains(".jhb")) {
                    partNo = partNo.replace(".jhb", "");
                }
                String number = deviceDataLine[3].trim();//零件数量
                if (number.equals("/")) {
                    number = deviceDataLine[4].trim();//零件数量
                }
                //根据设备和零件号对应已下达的物料
                Map<String, Object> param = new HashMap<>();
                param.put("deviceName", deviceName);
                param.put("bomNo", partNo);
                param.put("realCount", number);
                workOrderMapper.updateItemandByBom(param);
            }
            load.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //焊接机器人
    public void getRobotDataFromCSV(String ip, String shareFolder, String deviceName) {
        try {
//            String folderPath = "\\\\192.168.0.111\\共享文件"; // CSV文件的位置
            String folderPath = "\\\\" + ip + "\\" + shareFolder;
            File folder = new File(folderPath);
            File[] listOfFiles = folder.listFiles();

            //如果文件夹没有文件则不做处理
            if (listOfFiles == null || listOfFiles.length == 0) {
                return;
            }
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    if (!fileName.contains("csv")) {
                        continue;
                    }
                    Map<String, Object> csvMap = csv(folderPath + "/" + fileName);
                    List<String[]> csv = (List) csvMap.get("csvList");
                    InputStreamReader is = (InputStreamReader) csvMap.get("is");
                    CSVReader reader = (CSVReader) csvMap.get("reader");
                    Map<String, Object> robotDataMap = new HashMap<>();
                    int itemnumber = 0;
                    if (globalVar > csv.size()) {
                        globalVar = 0;
                    }
                    for (int i = globalVar; i < csv.size(); i++) {
                        String[] csvData = csv.get(i);
                        //第一行为标题行不做解析
                        if (csvData[0].contains("Cell")) {
                            continue;
                        }
                        if (robotDataMap.containsKey(csvData[2])) {
                            itemnumber = 1 + Integer.valueOf(robotDataMap.get(csvData[2]).toString());
                            robotDataMap.put(csvData[2], itemnumber);
                        } else {
                            robotDataMap.put(csvData[2], "1");
                        }
                        globalVar = csv.size();
                    }
                    //根据key值确定物料件数
                    Set keyset = robotDataMap.keySet();
                    for (Object key : keyset) {
                        Object value = robotDataMap.get(key);
                        //根据设备和零件号对应已下达的物料
                        Map<String, Object> param = new HashMap<>();
                        param.put("deviceName", deviceName);
                        param.put("itemName", key);
                        param.put("realCount", value);
                        workOrderMapper.updateItemandByItemName(param);
                    }
                    //关闭流
                    reader.close();
                    is.close();
                    //将读取完的文件重命名
                    String newFileName = file.getName() + ".temp";
                    File newFile = new File(file.getParent() + File.separator + newFileName);
                    file.renameTo(newFile);
                    //读取完以后删除文件
//                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
