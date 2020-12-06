package com.server.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;


public class HandleMysql {
	/**声明conn为全部静态变量，在ServerThread中经常调用插入和查询功能
	 * 否者每次调用本类中任一个方法都会调用一遍getConnection来获取连接
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Connection conn;
	public static String stableName_statical;
	public HandleMysql() throws FileNotFoundException, IOException {
		ReadProperties readProperties=new ReadProperties();
		Map<String,String> sqlInfo=readProperties.readAll("src\\com\\server\\util\\sql.properties");
		
		String driver = sqlInfo.get("driverClassName");
	    String url = sqlInfo.get("url");
	    String username = sqlInfo.get("username");
	    String password = sqlInfo.get("password");
	    stableName_statical=sqlInfo.get("stableName_statical");
	    
	    
	    try {
	        Class.forName(driver); //classLoader,加载对应驱动
	      //调用DriverManager对象的getConnection()方法，获得一个Connection对象
	        conn = (Connection) DriverManager.getConnection(url, username, password);
	        System.out.println("----------conn---------");
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public void createTable(String tableName) throws FileNotFoundException, IOException, SQLException{
		Statement sql_statement = (Statement) conn.createStatement();
		//如果同名数据库存在，删除
		//sql_statement.executeUpdate("drop table if exists student");
		//执行了一个sql语句生成了一个名为student的表
		sql_statement.executeUpdate("create table "+tableName+" (id int(11) not null auto_increment, wifiName varchar(255), MacAddress varchar(255),wifiStrength varchar(255),label varchar(255),time varchar(255), primary key (id) ); ");
		System.out.println("——————————————————建表————————————");
	}
	
	
	public int insert(String tableName,Map<String, String> perWifi,String label) throws FileNotFoundException, IOException {
	    int i = 0;
	    String sql = "insert into "+tableName+" (wifiName,MacAddress,wifiStrength,label,time) values(?,?,?,?,?)";
	    PreparedStatement pstmt;
	    try {
	        pstmt = (PreparedStatement) conn.prepareStatement(sql);
	        pstmt.setString(1, perWifi.get("wifiName"));
	        pstmt.setString(2, perWifi.get("MacAddress"));
	        pstmt.setString(3, perWifi.get("wifiStrength"));
	        pstmt.setString(4, label);
	        pstmt.setString(5, GetDate.getTime());
	        i = pstmt.executeUpdate();
	        pstmt.close();
//	        System.out.println("---------------insert-----------");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return i;
	}
	
	public ArrayList<String> getListFromeDB(String biaoName,String MacAddress,String label) throws FileNotFoundException, IOException {
	    if(label.equals("save"))
	    	biaoName=stableName_statical;
		String sql = "select * from "+biaoName+" where MacAddress='"+MacAddress+"'"+" and label='"+label+"'";
	    ArrayList<String> list=new ArrayList<String>();
	    PreparedStatement pstmt;
	    try {
	        pstmt = (PreparedStatement)conn.prepareStatement(sql);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	String str=rs.getString("wifiStrength");
	            //System.out.println(rs.getString("wifiName")+"的"+"****强度："+str);
	            list.add(str);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    System.out.println("getListFromeDB"+list.toString());
		return list;
	}
	
	public HashMap<String, ArrayList<String>> getLocationFromeDB(String biaoName,String label) throws FileNotFoundException, IOException {
	    if(label.equals("save"))
	    	biaoName=stableName_statical;
		String sql = "select * from "+biaoName+" limit 36";
	    HashMap<String, ArrayList<String>> location_map=new HashMap<String, ArrayList<String>>();
	    PreparedStatement pstmt;
	    try {
	        pstmt = (PreparedStatement)conn.prepareStatement(sql);
	        ArrayList<String> x_List=new ArrayList<String>();
	        ArrayList<String> y_List=new ArrayList<String>();
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	String xString=rs.getString("x");
	        	String yString=rs.getString("y");
	        	
	        	String regEx="[^0-9]";   
	        	Pattern p = Pattern.compile(regEx);   
	        	Matcher m = p.matcher(yString);   
	        	String temp=m.replaceAll("").trim();
	        	
	            x_List.add(xString);
	            y_List.add(temp);
	        }
	        location_map.put("x", x_List);
	        location_map.put("y", y_List);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return location_map;
	}
	
	
	public int getColNumber(String biaoName,String MacAddress,String label){
	    String sql = "select * from "+biaoName+" where MacAddress='"+MacAddress+"'"+" and label='"+label+"'";
	    int num = 0;
	    PreparedStatement pstmt;
	    try {
	        pstmt = (PreparedStatement)conn.prepareStatement(sql);
	        ResultSet rs = pstmt.executeQuery();
//	        int col = rs.getMetaData().getColumnCount();
//	        System.out.println("============================");
//	        while (rs.next()) {
//	            for (int i = 1; i <= col; i++) {
//	                System.out.print(rs.getString(i) + "\t");
//	                if ((i == 2) && (rs.getString(i).length() < 8)) {
//	                    System.out.print("\t");
//	                }
//	             }
//	            System.out.println("");
//	        }
	        rs.last();
	        num = rs.getRow();
//	        System.out.println("============================");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return num;
	}
	
	public static boolean getStaticData(){
		return false;
	}
}
