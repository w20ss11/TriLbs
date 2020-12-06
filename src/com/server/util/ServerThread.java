package com.server.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ServerThread implements Callable<String> {

	// 定义当前线程所处理的Socket
	Socket s = null;
	// 该线程所处理的Socket所对应的输入流
	private static BufferedReader br = null;
	private static Gson gson;
	private static String content;
	private static Map<String,String> apMacAddress=new HashMap<String,String>();
	private static Map<String,ArrayList<String>> map_staticInfo=new HashMap<String,ArrayList<String>>();
	private static final String fileName=GetDate.getTime();
	private static HandleMysql handleMysql;
	private static int num;
	private static Double[] double_count;
	private static HashMap<String, ArrayList<String>> xy_map=new HashMap<String, ArrayList<String>>();
//	private static PrintWriter pw = null;  


	//静态代码块加载apnamelist 这样只执行一次 来获取不变的apname
	//判断是否存在表
	static{
		try {
			handleMysql=new HandleMysql();//建立连接
			ReadProperties readProperties=new ReadProperties();
			//获取所有apMacAddress
			apMacAddress=readProperties.readAll("src\\com\\server\\util\\apMacAddress.properties");
			num=Integer.parseInt(apMacAddress.get("num"));
			double_count=new Double[num];
			System.out.println("apMacAddress:"+apMacAddress.toString());
			for(int i=0;i<num;i++)
				map_staticInfo.put("apMacAddress"+i,handleMysql.getListFromeDB("static_data",apMacAddress.get("apMacAddress"+i),"save"));
			for(String key : map_staticInfo.keySet()){
				System.out.println(key+"="+map_staticInfo.get(key));  
			}  
			handleMysql.createTable(fileName);//建表
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ServerThread(Socket s) throws IOException {
		this.s = s;
		// 初始化该Socket对应的输入流
		br = new BufferedReader(new InputStreamReader(s.getInputStream(),
				"utf-8"));
	}

	@Override
	public String call() throws Exception {

		try {
			content = null;
			List<Map<String, String>> list = new ArrayList<Map<String,String>>();
			int numofSave=handleMysql.getColNumber(fileName,apMacAddress.get("apMacAddress1"),"save");
			System.out.println("查询结果："+numofSave);
			//采用循环不断从Socket中读取客户端发送过来的数据 循环
			while ((content = readFromClient()) != null) {
				//System.out.println(content);
				if(!content.equals("count")){
					list=readJsonString(content);
					savetList2db(list,"count");
					System.out.println("list:"+list.toString());
					//从list<map<string,string>>中获取四个ap的现在count rss值
					for(Map<String, String> map:list){
						for(int i=0;i<num;i++){
							if(apMacAddress.get("apMacAddress"+i).equals(map.get("MacAddress"))){
								double_count[i]=Double.parseDouble(map.get("wifiStrength"));
								System.out.println("double_count"+Arrays.toString(double_count));
							}
						}
					}				
//					pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())),true);  
//					pw.write("res");
//					pw.flush();
					
					
					//计算
					xy_map=handleMysql.getLocationFromeDB("static_data", "save");
					System.out.println("xy_map"+xy_map.toString());
					Count count=new Count(map_staticInfo, double_count, num,xy_map);
					
					int at=count.countFun(3);
					return "0RESULT："+at;//+at;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("出现异常时 接收到的数据是： "+content);
		return "3出现异常";
	}



	// 定义读取客户端数据的方法
	private String readFromClient() {
		try {
			return br.readLine();
		}
		// 如果捕捉到异常，表明该Socket对应的客户端已经关闭
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<Map<String, String>> readJsonString(String gsonString) {
		List<Map<String, String>> readList = null;
		gson = new Gson();
		if (gson != null) {
			readList = gson.fromJson(gsonString,
					new TypeToken<List<Map<String, String>>>() {
			}.getType());
		}
		return readList;
	}

	private void savetList2db(List<Map<String,String>> list,String label) throws FileNotFoundException, IOException{
		Iterator<Map<String,String>> iterator=list.iterator();
		//TODO 判断list是否有所有的apMacAddress
		while(iterator.hasNext()){
			Map<String, String>map=iterator.next();
			map.toString();
			handleMysql.insert(fileName,map,label);
		}
	}

}
