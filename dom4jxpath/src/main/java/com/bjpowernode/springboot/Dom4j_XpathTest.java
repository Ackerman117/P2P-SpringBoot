package com.bjpowernode.springboot;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 * @author: gg
 * @create: 2021-08-30 19:32
 */
public class Dom4j_XpathTest {

    public static void main(String[] args) throws DocumentException {
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "\n" +
                "<bookstore>\n" +
                "\n" +
                "<book>\n" +
                "  <title lang=\"eng\">Harry Potter</title>\n" +
                "  <price>29.99</price>\n" +
                "</book>\n" +
                "\n" +
                "<book>\n" +
                "  <title lang=\"eng\">Learning XML</title>\n" +
                "  <price>39.95</price>\n" +
                "</book>\n" +
                "\n" +
                "</bookstore>";

        // 根据xml生成一个Document对象
        Document document = DocumentHelper.parseText(xml);

        // 选取属于 bookstore 子元素的第一个 book 元素。
        Node node = document.selectSingleNode("/bookstore/book[1]/price[1]");
        String text = node.getText();
        System.out.println(text);
    }




}
