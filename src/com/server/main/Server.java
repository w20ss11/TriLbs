package com.server.main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.FutureTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import com.server.util.ServerThread;

import java.awt.Font;


public class Server extends JFrame {
	private static final long serialVersionUID = 1L;
	static ServerSocket tcpSocket=null;
	private static ImageIcon icon0 = new ImageIcon("src\\com\\server\\util\\map.png");
	private static JTextPane current_state=new JTextPane();
	private static JTextPane res;

	public  Server(){
		setFont(new Font("Dialog", Font.PLAIN, 17));

		this.init();
	}
	public void init(){
		//ɾ��D��������txt�ļ�
		//deleteTxt();
		this.setTitle("��������");
		this.setBounds(100,100,563,382);
		this.createUI();
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	public void createUI(){
		JPanel panel=new JPanel();
		//��ӱ߿�
		panel.setLayout(null);
		getContentPane().add(panel);
		//������ַ
		JLabel nameLbl=new JLabel("��ǰ����");
		nameLbl.setFont(new Font("΢���ź�", Font.PLAIN, 18));
		nameLbl.setBounds(10,13,131,25);
		panel.add(nameLbl);

		JLabel label = new JLabel("״̬��");
		label.setFont(new Font("΢���ź�", Font.PLAIN, 20));
		label.setBounds(10, 284, 106, 25);
		panel.add(label);
		current_state.setFont(new Font("����", Font.PLAIN, 18));

		current_state.setBounds(77, 284, 454, 32);
		panel.add(current_state);
		
		JLabel mapLabel = new JLabel(icon0);
		mapLabel.setBounds(10, 39, 521, 232);
		panel.add(mapLabel);
		
		res = new JTextPane();
		res.setBounds(105, 12, 106, 26);
		panel.add(res);

	}


	public static void main(String[] args) throws Exception
	{

		Server sw=new Server();
		sw.setVisible(true);
		

		tcpSocket = new ServerSocket(10046);
		System.out.println("���������");
		while(true){
			Socket s;
			try {

				//���տͻ��˵����ݲ����� ��ȡ����ֵ
				s = tcpSocket.accept();
				System.out.println("�յ���Ϣ");
				// ʹ��FutureTask����װCallable����
				ServerThread st = new ServerThread(s);
				FutureTask<String> task = new FutureTask<String>(st);
				new Thread(task).start();

				//tempString ÿ�����˻�ȡ�����ݻ��߼��������ݷ���ֵ
				String tempString=task.get();
				char c=tempString.charAt(0);
				tempString=tempString.substring(1);
				
				//��serverthread�Ľ��tempString���͸��ͻ���
				OutputStream os=s.getOutputStream();
				os.write((tempString+"\r\n").getBytes("utf-8"));
				
				if(c=='0'){//count20s
					res.setText(tempString.substring(tempString.length()-1));
				}
				current_state.setText(tempString.substring(tempString.length()-1));


				// ��ȡ�̷߳���ֵ
				System.out.println("���̵߳ķ���ֵ��" + task.get());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}

