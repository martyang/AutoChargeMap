package com.gionee.autochargemap.utils;

import com.gionee.autochargemap.bean.DataBean;

import java.io.File;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * 创建表格，添加字段
 *
 * @author pbl
 *
 */
public class JxlUtil {

    /***
     * @param file 要写文档的文件
     */
    public static void writeAppinfoData(File file, ArrayList<DataBean> list) {
        WritableCellFormat wcfN;
        try {
            file.createNewFile();
            // 打开文件
            WritableWorkbook book  = Workbook.createWorkbook(file);
            WritableSheet    sheet = book.createSheet(Constants.SHEET_NAME, 0);
            // sheet = workbook.createSheet("touchResult", 0);
            WritableFont bold = new WritableFont(WritableFont.ARIAL,
                                                 12,
                                                 WritableFont.BOLD);// 设置字体种类和黑体显示,字体为Arial,字号大小为10,采用黑体显示
            WritableFont bold2 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD);
            bold2.setColour(Colour.BLACK);
            WritableCellFormat contentFormate = new WritableCellFormat(bold2);// 生成一个单元格样式控制对象
            contentFormate.setAlignment(jxl.format.Alignment.CENTRE);// 单元格中的内容水平方向居中
            contentFormate.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 单元格的内容垂直方向居中
            contentFormate.setBorder(Border.ALL, BorderLineStyle.THIN);
            //                contentFormate.setBackground(Colour.RED);

            WritableCellFormat titleFormate = new WritableCellFormat(bold);// 生成一个单元格样式控制对象
            titleFormate.setAlignment(jxl.format.Alignment.CENTRE);// 单元格中的内容水平方向居中
            titleFormate.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 单元格的内容垂直方向居中
            titleFormate.setBorder(Border.ALL, BorderLineStyle.THIN);


            WritableFont nf = new WritableFont(WritableFont.createFont("宋体"), 10);
            wcfN = new jxl.write.WritableCellFormat(nf);
            wcfN.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
            wcfN.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直对齐
            wcfN.setAlignment(Alignment.CENTRE);

            // 水平对齐
            wcfN.setWrap(true);
            // 生成名为“第一页”的工作表，参数0表示这是第一页
            // 在Label对象的构造子中指名单元格位置是第一列第一行(0,0)
            // 以及单元格内容为test
            Label applabel1     = new Label(0, 0, "时间", titleFormate);
            Label applabel2 = new Label(1, 0, "电流/mA", titleFormate);
            Label applabel3 = new Label(2, 0, "电压/mV", titleFormate);
            Label applabel4 = new Label(3, 0, "温度/℃", titleFormate);
            Label applabel5 = new Label(4, 0, "当前电量", titleFormate);
            // 将定义好的单元格添加到工作表中
            sheet.addCell(applabel1);
            sheet.addCell(applabel2);
            sheet.addCell(applabel3);
            sheet.addCell(applabel4);
            sheet.addCell(applabel5);
            sheet.setColumnView(0, 26);// 设置第0列的宽度为2600
            sheet.setColumnView(1, 13);
            sheet.setColumnView(2, 13);
            sheet.setColumnView(3, 13);
            sheet.setColumnView(4, 13);

            for (int i = 0; i < list.size(); i++) {
                DataBean bean = list.get(i);
                Label label1= new Label(0, i + 1, bean.getTime(), contentFormate);
                Label label2 = new Label(1, i + 1, bean.getCurrent(), contentFormate);
                Label  label3 = new Label(2, i + 1, bean.getVoltage(), contentFormate);
                Label  label4 = new Label(3, i + 1, bean.getTemp(), contentFormate);
                Label  label5 = new Label(4, i + 1, bean.getNewEnergy(), contentFormate);

                sheet.addCell(label1);
                sheet.addCell(label2);
                sheet.addCell(label3);
                sheet.addCell(label4);
                sheet.addCell(label5);

            }
            // 写入数据并关闭文件
            book.write();
            book.close();

        } catch (Exception e) {
            Log.i(e.toString());
        }
    }

}
