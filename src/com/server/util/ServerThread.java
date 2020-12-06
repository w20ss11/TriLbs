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

	// ���嵱ǰ�߳��������Socket
	Socket s = null;
	// ���߳��������Socket����Ӧ��������
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


	//��̬��������apnamelist ����ִֻ��һ�� ����ȡ�����apname
	//�ж��Ƿ���ڱ�
	static{
		try {
			handleMysql=new HandleMysql();//��������
			ReadProperties readProperties=new ReadProperties();
			//��ȡ����apMacAddress
			apMacAddress=readProperties.readAll("src\\com\\server\\util\\apMacAddress.properties");
			num=Integer.parseInt(apMacAddress.get("num"));
			double_count=new Double[num];
			System.out.println("apMacAddress:"+apMacAddress.toString());
			for(int i=0;i<num;i++)
				map_staticInfo.put("apMacAddress"+i,handleMysql.getListFromeDB("static_data",apMacAddress.get("apMacAddress"+i),"save"));
			for(String key : map_staticInfo.keySet()){
				System.out.println(key+"="+map_staticInfo.get(key));  
			}  
			handleMysql.createTable(fileName);//����
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ServerThread(Socket s) throws IOException {
		this.s = s;
		// ��ʼ����Socket��Ӧ��������
		br = new BufferedReader(new InputStreamReader(s.getInputStream(),
				"utf-8"));
	}

	@Override
	public String call() throws Exception {

		try {
			content = null;
			List<Map<String, String>> list = new ArrayList<Map<String,String>>();
			int numofSave=handleMysql.getColNumber(fileName,apMacAddress.get("apMacAddress1"),"save");
			System.out.println("��ѯ�����"+numofSave);
			//����ѭ�����ϴ�Socket�ж�ȡ�ͻ��˷��͹��������� ѭ��
			while ((content = readFromClient()) != null) {
				//System.out.println(content);
				if(!content.equals("count")){
					list=readJsonString(content);
					savetList2db(list,"count");
					System.out.println("list:"+list.toString());
					//��list<map<string,string>>�л�ȡ�ĸ�ap������count rssֵ
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
					
					
					//����
					xy_map=handleMysql.getLocationFromeDB("static_data", "save");
					System.out.println("xy_map"+xy_map.toString());
					Count count=new Count(map_staticInfo, double_count, num,xy_map);
					
					int at=count.countFun(3);
					return "0RESULT��"+at;//+at;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("�����쳣ʱ ���յ��������ǣ� "+content);
		return "3�����쳣";
	}



	// �����ȡ�ͻ������ݵķ���
	private String readFromClient() {
		try {
			return br.readLine();
		}
		// �����׽���쳣��������Socket��Ӧ�Ŀͻ����Ѿ��ر�
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
		//TODO �ж�list�Ƿ������е�apMacAddress
		while(iterator.hasNext()){
			Map<String, String>map=iterator.next();
			map.toString();
			handleMysql.insert(fileName,map,label);
		}
	}

}
