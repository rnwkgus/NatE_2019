package rfcommunication;

import gnu.io.*;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.swing.JTextField;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;















import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RefineryUtilities;

import java.io.*;
import javax.swing.JLabel;

public class java_data_plot  {

	
	public int st;

	public int numbyte;
	public String str;
	public byte[] readBuffers;

	public String Filepath="D:\\Dropbox\\Work\\0_RFCom\\register_bank\\rx.txt";
	public File file = new File("D:\\Dropbox\\Work\\0_RFCom\\register_bank\\rx.txt");
		
	private int datay;
	private JFrame frame;

	private JComboBox<String> paritysel = new JComboBox<String>();
	private JComboBox<String> stopsel = new JComboBox<String>();
	private JComboBox<String> databitsel = new JComboBox<String>();
	private JComboBox<String> portsel = new JComboBox<String>();
	private JComboBox<String> speedsel = new JComboBox<String>();

	public JList<String> monitoring = new JList<String>();
	public JList<String> monitoring2 = new JList<String>();
	public JScrollPane scrollPane = new JScrollPane();
	public JScrollPane scrollPane2 = new JScrollPane();
	private JButton btnRefresh = new JButton("Refresh");
	private JButton btnReceiveData = new JButton("Receive Data");
	private JButton Connectbtn = new JButton("Connect");
	
	private CommPortIdentifier	commPortIdentifier	=null;
	private SerialPort			serialPort			=null;
	
	private OutputStream	serialOut;
	private InputStream		serialIn;

	private DefaultListModel<String> model = new DefaultListModel<String>();
	private DefaultListModel<String> model2 = new DefaultListModel<String>();
	private JTextField sendtext;
	private FileOutputStream output;
	private FileOutputStream output2;

	//private data data= new data();
	private datam datam= new datam();

    private boolean findpattern = false;
    private int findready = 0;
    private int datanum=0;
    private JTextField samplerate;
    private double data_before=0;

	public int num_monitor =0;
	public int num_pre =0;
	public String str_monitor ="";
	public Date today = new Date (); 
    TimeSeries series = new TimeSeries("Random Data", Millisecond.class);
    private JTextField sampletime;

	public int filter_num =1;
	public int filter_count =0;
	public double filter_sum =0;
	
    long dataf=0;
    long data_repattern2=0;
	int wstart=0;
	String printdata="";
	private JTextField filter;
	private final JLabel lblWindow_1 = new JLabel("window");
	private final JLabel lblFilter = new JLabel("filter");
    //public graphs graph = new graphs("");
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					java_data_plot window = new java_data_plot();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public java_data_plot() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 860, 842);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		makecombobox();
		
		
		// Connect Button
		// Connect to RS232 port
		Connectbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					connect();	// 연결
					datam=new datam();   // data process class
					datam.samplenum=Integer.parseInt(samplerate.getText());    // setting sampling rate
					datam.pattern_plus=Integer.parseInt(sampletime.getText());    // setting pattern plus
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});		
		Connectbtn.setBounds(12, 40, 86, 21);
		frame.getContentPane().add(Connectbtn);
		
		// Refresh Button
		// Refresh All RS232 Connection
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (Connectbtn.getText().equals("Connect")) {
					appendPortList();
				}
				else if (Connectbtn.getText().equals("Disconnect")) {
					serialPort.close();
					serialPort = null;  
					Connectbtn.setText("Connect");
					appendPortList();
					try {
						connect();	// 연결
						datam=new datam();   // data process class
						datam.samplenum=Integer.parseInt(samplerate.getText());    // setting sampling rate
						datam.pattern_plus=Integer.parseInt(sampletime.getText());    // setting pattern plus
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btnRefresh.setBounds(114, 40, 86, 21);
		frame.getContentPane().add(btnRefresh);
		
		
		// data save file
		sendtext = new JTextField();
		sendtext.setText("D:\\\\Dropbox\\\\Work\\\\0_RFCom\\\\register_bank\\\\rx.txt");
		sendtext.setBounds(12, 71, 502, 21);
		frame.getContentPane().add(sendtext);
		sendtext.setColumns(10);
		
				
		// ReceiveData Button
		btnReceiveData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnReceiveData.getText()=="Receive Data"){

					btnReceiveData.setText("Stop Receive Data");
				} else if (btnReceiveData.getText()=="Stop Receive Data"){

					btnReceiveData.setText("Receive Data");
				}
	            
			}
		});
		btnReceiveData.setBounds(212, 40, 107, 21);
		frame.getContentPane().add(btnReceiveData);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 392, 820, 402);
		frame.getContentPane().add(scrollPane);
		scrollPane.setViewportView(monitoring);
		monitoring.setModel(model);
		
		samplerate = new JTextField();
		samplerate.setBounds(376, 40, 38, 21);
		frame.getContentPane().add(samplerate);
		samplerate.setColumns(10);
		samplerate.setText("37");
		

        // sampling rate
        sampletime = new JTextField();
        sampletime.setText("10");
        sampletime.setBounds(476, 40, 38, 21);
        frame.getContentPane().add(sampletime);
        sampletime.setColumns(10);
        
		
		JPanel graph_pannel = new JPanel();
		graph_pannel.setBounds(12, 102, 502, 280);
		frame.getContentPane().add(graph_pannel);
		
		
        final TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        final JFreeChart chart = CreateChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        graph_pannel.add(chartPanel);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        
        

		scrollPane2 = new JScrollPane();
		scrollPane2.setBounds(530, 102, 302, 280);
		frame.getContentPane().add(scrollPane2);
		scrollPane2.setViewportView(monitoring2);
		monitoring2.setModel(model2);
		
		filter = new JTextField();
		filter.setText("10");
		filter.setColumns(10);
		filter.setBounds(476, 9, 38, 21);
		frame.getContentPane().add(filter);
		
		JLabel lblWindow = new JLabel("sample");
		lblWindow.setBounds(331, 43, 51, 15);
		frame.getContentPane().add(lblWindow);
		lblWindow_1.setBounds(422, 43, 51, 15);
		
		frame.getContentPane().add(lblWindow_1);
		lblFilter.setBounds(435, 12, 38, 15);
		
		frame.getContentPane().add(lblFilter);
	}

    public void adddata(String num){
    	final double factor = Double.valueOf(num);
        final Millisecond now = new Millisecond();
        //System.out.println(num);
    	if (filter_count>=filter_num) {
            series.add(new Millisecond(), filter_sum/filter_num);
    		filter_sum=0;
    		filter_count=0;
    	}else if (filter_count<filter_num) {
    		filter_sum=filter_sum+factor;
    		filter_count++;
    	}
        //data_before=factor;
    }
    
    private JFreeChart CreateChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            "Sensor Monitoring", 
            "Time", 
            "Code",
            dataset, 
            true, 
            true, 
            false
        );
        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(32768.0);  // 60 seconds
        axis = plot.getRangeAxis();
        axis.setRange(0.0, 32768.0); 
        return result;
    }
    
	// 포트 목록을 재 설정한다.
	private void makecombobox(){

		// COM Port ComboBox
		portsel.setBounds(12, 9, 73, 21);
		frame.getContentPane().add(portsel);
		appendPortList();
		
		
		// Speed ComboBox		
		speedsel.setBounds(97, 9, 73, 21);
		frame.getContentPane().add(speedsel);
		speedsel.addItem("1200");
		speedsel.addItem("2400");
		speedsel.addItem("4800");
		speedsel.addItem("9600");
		speedsel.addItem("19200");
		speedsel.addItem("38400");
		speedsel.addItem("57600");
		speedsel.addItem("115200");
		speedsel.addItem("230400");
		speedsel.addItem("380400");
		speedsel.setSelectedIndex(8);


		// Parity ComboBox
		paritysel.setBounds(182, 9, 73, 21);
		frame.getContentPane().add(paritysel);
		paritysel.addItem("NONE");
		paritysel.addItem("ODD");
		paritysel.addItem("EVEN");
		paritysel.setSelectedIndex(0);

		// STOP ComboBox
		stopsel.setBounds(267, 9, 73, 21);
		frame.getContentPane().add(stopsel);
		stopsel.addItem("1 STOP");
		stopsel.addItem("2 STOP");
		stopsel.setSelectedIndex(0);
		
		// Databit ComboBox
		databitsel.setBounds(352, 9, 67, 21);
		frame.getContentPane().add(databitsel);
		databitsel.addItem("7 BIT");
		databitsel.addItem("8 BIT");
		databitsel.setSelectedIndex(1);

	}
    public void appendPortList(){  
  
        Enumeration<?> enumeration = null;  
  
        // 기존 목록을 모두 지운다.   
        portsel.removeAllItems();
          
        // 통신 포트 목록을 구한다.  
        enumeration = CommPortIdentifier.getPortIdentifiers();  
        while ( enumeration.hasMoreElements() )   
        {  
            // 다음 포트의 식별자 정보를 받아온다.  
            commPortIdentifier = (CommPortIdentifier) enumeration.nextElement();  
            // 포트가 시리얼 포트 타입만 리스트에  집어 넣는다.  
            if( commPortIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL )  
                portsel.addItem(commPortIdentifier.getName());  
        }  
       
        if( portsel.getItemCount() <= 0 ){  
            portsel.addItem("NO PORT");  
        }  
        else{  
            portsel.setSelectedIndex(portsel.getItemCount()-1);  
        }  
    }  
    
    public void connect() throws IOException{

		if (Connectbtn.getText().equals("Connect")) {

			Filepath=sendtext.getText();	// 저장 파일경로 가져오기
			String Filepath2 = "D:\\\\Dropbox\\\\Work\\\\0_RFCom\\\\register_bank\\\\rx2.txt";	// 저장 파일경로 가져오기

			// Port 가져오기
			try {  
	            commPortIdentifier = CommPortIdentifier.getPortIdentifier(portsel.getItemAt(portsel.getSelectedIndex()));  
	        } catch (NoSuchPortException e1) {  
	              
	            e1.printStackTrace();  
	            return;  
	        }  
			
			// Port 열기
	        try {  
	            serialPort = (SerialPort) commPortIdentifier.open(  
	                                "SerialTransferReceiver", 2000); 
	            monitoring.clearSelection();
	            if (file.exists()==false)
	            {
	            	file.createNewFile();
	            }

	        	output = new FileOutputStream(Filepath);
	        	output2 = new FileOutputStream(Filepath2);
	        	wstart=0;
	            	
	        } catch (PortInUseException e1) {  
	            e1.printStackTrace();  
	            serialPort = null;  
	            return;  
	        }
	        
	        // RS-232 Parameter 지정
	        // 파라메터 설정   
	        try {  
	            int baud;  
	            int parity;  
	            int stop;  
	            int data;  
	              
	            baud = Integer.parseInt(speedsel.getItemAt(speedsel.getSelectedIndex()));  
	            switch( paritysel.getSelectedIndex()){  
	            case 0 : parity = SerialPort.PARITY_NONE;   break;  
	            case 1 : parity = SerialPort.PARITY_ODD;    break;  
	            case 2 : parity = SerialPort.PARITY_EVEN;   break;  
	            default: parity = SerialPort.PARITY_NONE;   break;    
	            }  
	            switch( stopsel.getSelectedIndex()){  
	            case 0 : stop = SerialPort.STOPBITS_1;  break;  
	            case 1 : stop = SerialPort.STOPBITS_2;  break;  
	            default: stop = SerialPort.STOPBITS_1;  break;    
	            }  
	            switch( databitsel.getSelectedIndex()){  
	            case 0 : data = SerialPort.DATABITS_7;  break;  
	            case 1 : data = SerialPort.DATABITS_8;  break;  
	            default: data = SerialPort.DATABITS_7;  break;    
	            }
	            serialPort.setSerialPortParams( baud, data, stop, parity);  
	        }   
	        catch (UnsupportedCommOperationException e1) {  
	            e1.printStackTrace();   
	        }
	        
	        
	        // 출력 스트림 설정  
	        try {  
	            serialOut = serialPort.getOutputStream();  
	        } catch (IOException e1) {  
	            e1.printStackTrace();  
	        }  
	          
	        // 입력 스트림 설정  
	        try {  
	            serialIn = serialPort.getInputStream();  
	        } catch (IOException e1) {  
	            e1.printStackTrace();  
	        }  

	        // 수신 이벤트 처리 설정   
	        serialPort.notifyOnDataAvailable(true);  
			try {  
			    serialPort.addEventListener( new SerialPortEventListener(){  
			        

					public void serialEvent(SerialPortEvent event ) {  
			            if( event.getEventType() == SerialPortEvent.DATA_AVAILABLE ){  
			              byte[] readBuffer = new byte[1];  							// 받은 데이터 저장할 버퍼

			                try {  
			                    while (serialIn.available() > 0) {  					// 받은 데이터 있는지 확인

			                    	numbyte = serialIn.read(readBuffer);				// 1byte 씩 받은걸 읽음

									datay = (int)readBuffer[0];
									datam.addbyte(readBuffer);

									//  파일에 byte 단위로 쓰기
					    			for (int n=0; n<8;n++) {
					    				if ((datay & 0x1)==1){	
											output.write("1".getBytes());						// 파일에 쓰기
					        			}
					        			else if ((datay & 0x1)==0){ 	

					        				output.write("0".getBytes());						// 파일에 쓰기
					        			}

					    				datay=datay>>>1;
					    			}
					    			output.write("/".getBytes());
					    			
									pattern3();
			                    }
			                }
			                catch (IOException e) {  
			                }  
			            } 
			            
			    }});  
			}   
			catch (TooManyListenersException e2) {  
			    e2.printStackTrace();  
			}
											// 수신중 표시
			Connectbtn.setText("Disconnect");											// Disconnect로 변환
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
			model.clear();
			model2.clear();
			model.addElement("수신 중");		

			// filter
			filter_num = Integer.getInteger(filter.getText());
			
	        	        
		} else if (Connectbtn.getText().equals("Disconnect")) {
			serialPort.close();
			serialPort = null;  
			output.close();  
			output2.close();
			model.addElement("수신 중단");												// 수신중 표시
			Connectbtn.setText("Connect");	
			// Connect로 변환
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());

	        
		}
    }
 // 열려진 시리얼 포트로 데이터를  써 넣는다.   
    public void SendData( String str ){  
        try {
        	if (Connectbtn.getText()=="Disconnect"){
	            serialOut.write( str.getBytes() );  
	            serialOut.flush();  
        	}
        } catch (IOException e) {  
            e.printStackTrace();  
        }    
    }
 // 열려진 시리얼 포트로 데이터를  써 넣는다.   
    public void SendNum( byte num ){  
        try {
        	if (Connectbtn.getText()=="Disconnect"){
	            serialOut.write( num );  
	            serialOut.flush();  
        	}
        } catch (IOException e) {  
            e.printStackTrace();  
        }    
    }
    
    public void pattern2() throws IOException{

	    	if (datam.repattern2()){
	    		
	
				if (datam.data_prebit ==1){ 
					data_repattern2=data_repattern2 >>> 1;
					data_repattern2=data_repattern2 | 0x80000000;
				}
				else if (datam.data_prebit ==0){
					data_repattern2=data_repattern2 >>> 1;
				}
				
	    		if (num_monitor<110) {
	    			str_monitor=str_monitor + datam.data_receivebit;
	    			num_monitor++;
	    		} else {
	
					model.addElement(str_monitor);
					scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
					str_monitor="";
		    		num_monitor=0;
	    		}
	    		
	
	
	    		if (datam.compare_print(data_repattern2)) {
	    			dataf=data_repattern2;
	    			printdata=" ";
	
	    			for (int n=0; n<32;n++) {
	    				if ((dataf & 0x1)==1){
	    					printdata=printdata + "1";
	        			}
	        			else if ((dataf & 0x1)==0){ 
	    					printdata=printdata + "0";
	        			}
	
	    				if (n==15) printdata=printdata + " ; "; 
	    				dataf=dataf>>>1;
	    			}
	    			
	    			
					scrollPane2.getVerticalScrollBar().setValue(scrollPane2.getVerticalScrollBar().getMaximum());
	    		}
	    		
	    		if (findready<=0) {									// 첫패턴 못찻았을 때
	    			if (datam.compare(data_repattern2)){							// 첫패턴인지 확인
	    				if (!findpattern){
	    					
	
	    	    			dataf=data_repattern2;
	    	    			datam.data_receive=0;
	
	    	    			for (int n=0; n<16;n++) {
	    	    				dataf=dataf>>>1;
	    	    			}
	    	    			for (int n=0; n<15;n++) {
	    	    				if ((dataf & 0x1)==1){
	        	    				datam.data_receive=datam.data_receive << 1;
	        	    				datam.data_receive++;
	    	        			}
	    	        			else if ((dataf & 0x1)==0){ 
	        	    				datam.data_receive=datam.data_receive << 1;
	    	        			}
	
	    	    				dataf=dataf>>>1;
	    	    			}
	    	    			
	    					adddata(Integer.toString(datam.data_receive));
	    					model2.addElement(printdata + ";" + Integer.toString(datam.data_receive));
	    					
	    					if (wstart==0) {
	    						wstart=1;
	    					} else{
	    						output2.write("\n".getBytes());						// 파일에 쓰기
	    					}
	    					output2.write(Integer.toString(datam.data_receive).getBytes());
							output2.write(",".getBytes());						// 파일에 쓰기
	    					output2.write(Long.toString(System.currentTimeMillis()).getBytes());
	    					
	    					
	
	    					datam.data_receive=0;
						findready=1;								// 첫팻턴 확인하면 다음 비트 확인 안하는 변수
						}
	    			} 
	    		} else {
	    			if (!findpattern){
	    				if (findready==30) { 							// 첫팻턴 확인하고 10샘플 지났는지 확인
	    					findready=0;
	    				} else{
	    					findready++;								
	    				}
	    			}
	    		}
	    	
    		
    	}

    }


public void pattern3() throws IOException{

	for (int nn=0; nn<8;nn++) {
		datam.dataraw=datam.dataraw>>>1;
		if (datam.repattern3()){

			long mkpattern1=1;
			mkpattern1=mkpattern1<<63;
			data_repattern2=datam.data_repattern;
			if (num_monitor<110) {
				str_monitor=str_monitor + datam.data_receivebit;
				num_monitor++;
			} else {
	
				model.addElement(str_monitor);
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
				str_monitor="";
	    		num_monitor=0;
			}
			
	
			
			
			if (findready<=0) {									
				if (datam.compare(data_repattern2)){					

		    			dataf=data_repattern2;
		    			printdata="";

		    			for (int n=0; n<32;n++) {
		    				if ((dataf & 0x1)==1){
		    					printdata=printdata + "1";
		        			}
		        			else if ((dataf & 0x1)==0){ 
		    					printdata=printdata + "0";
		        			}
		
		    				if (n==15) printdata=printdata + " ; "; 
		    				dataf=dataf>>>1;
		    			}

		    			dataf=data_repattern2;
		    			datam.data_receive=0;
		    			for (int n=0; n<16;n++) {
		    				dataf=dataf>>>1;
		    			}
		    			for (int n=0; n<15;n++) {
		    				if ((dataf & 0x1)==1){
	    	    				datam.data_receive=datam.data_receive << 1;
	    	    				datam.data_receive++;
		        			}
		        			else if ((dataf & 0x1)==0){ 
	    	    				datam.data_receive=datam.data_receive << 1;
		        			}
	
		    				dataf=dataf>>>1;
		    			}
		    			if ((datam.data_receive<num_pre*1.5) & (datam.data_receive>num_pre/2) ) {
		    				model.clear();
		    				adddata(Integer.toString(datam.data_receive));
							model2.addElement(printdata + ";" + Integer.toString(datam.data_receive));
							scrollPane2.getVerticalScrollBar().setValue(scrollPane2.getVerticalScrollBar().getMaximum());
							
							if (wstart==0) {
								wstart=1;
							} else{
								output2.write("\n".getBytes());						// 파일에 쓰기
							}
							output2.write(Integer.toString(datam.data_receive).getBytes());
							output2.write(",".getBytes());						// 파일에 쓰기
							output2.write(Long.toString(System.currentTimeMillis()).getBytes());
							num_pre=datam.data_receive;
		    			} else {
		    				model.clear();
							model2.addElement("x "+printdata + ";" + Integer.toString(datam.data_receive));
							scrollPane2.getVerticalScrollBar().setValue(scrollPane2.getVerticalScrollBar().getMaximum());
							
							num_pre=datam.data_receive;
		    				
		    			}
						
						
	
						datam.data_receive=0;
					findready=1;	
	
				} else if (datam.compare_print(data_repattern2)) {				//  10100000으로 시작되는 문자열 찾으면 출력하기
					dataf=data_repattern2;
					printdata=" ";
	
					for (int n=0; n<32;n++) {
						if ((dataf & 0x1)==1){
							printdata=printdata + "1";
		    			}
		    			else if ((dataf & 0x1)==0){ 
							printdata=printdata + "0";
		    			}
	
						if (n==15) printdata=printdata + " ; "; 
						dataf=dataf>>>1;
					}
					
					
					model2.addElement(printdata);
					scrollPane2.getVerticalScrollBar().setValue(scrollPane2.getVerticalScrollBar().getMaximum());
				}
				
			} else {
				if (!findpattern){
					if (findready==10) { 							// 첫팻턴 확인하고 10샘플 지났는지 확인
						findready=0;
					} else{
						findready++;								
					}
				}
			}
		}
		
		
	}

}
}


public class datam {
	/**
	 * Launch the application.
	 */
	public long dataraw=0;
	public int length=0;						// 데이터 패턴 거리 모니터링
	public int length_c=0;						// 현제 데이터 패턴 거리
	public int length_statistic[]= new int[5500];
	public int data_receive=0;					// 받은 데이터 
	public String data_receivebit="";					// 받은 데이터 
	public int data_length=0;					// 한 비트의 샘플링 개수 
	public int data_bit=0;						// 받는게 몇번째 비트인지
	public int samplenum=5;
	public long mkpattern=0;
	public int mkpattern2=0;
	public long data_repattern=0;
	public int data_prebit=0;
	public int nodata=0;
	public int pattern_plus=0;
	
	public boolean samepattern =true;
	public boolean samepattern_before =true;
	public int samepattern_num =0;
	public long mkpattern1=1;
	public long data_byte2=0;	
	public long mkp=1;

	/**
	 * Create the application.
	 */
	public datam() {

		mkpattern1=1;
		mkpattern1=mkpattern1<<63;
		mkp=0;
		
		
		mkp=1;
		mkp=mkp<<31;
		
		
		mkp=mkp|0xFFFF;
	}
	public void add(String str) {
		if (str.equals("1")) {
			dataraw=dataraw >>> 1;
			dataraw=dataraw | 0x80000000;
		}			
		else if (str.equals("0")) {
			dataraw=dataraw >>> 1;
		}
	}
	public void addbyte(byte[] data_byte) {
		
		data_byte2=data_byte[0];
		data_byte2=data_byte2 << 56;
		dataraw=dataraw|data_byte2;

	}
	public static final long toLong (byte[] byteArray, int offset, int len)
	{
	   long val = 0;
	   len = Math.min(len, 8);
	   for (int i = (len - 1); i >= 0; i--)
	   {
	      val <<= 8;
	      val |= (byteArray [offset + i] & 0x00FF);
	   }
	   return val;
	}
	public int maxl() { 
		int maxnum=0;
		int maxint=0;
		for (int i=0; i<=5500-1; i++){
			if (maxnum < length_statistic[i]){
				maxint=i;
				maxnum=length_statistic[i];
			}
		}
		return maxint;
	}

	public boolean repattern2(){
		int i=0;
		
		if (length==0){

			if ((data_repattern & 0x1)==1){ 
				data_prebit=1;
			}
			else if ((data_repattern & 0x1)==0){
				data_prebit=0;
			}
			
			mkpattern=0;
			i=0;
			while(i<samplenum+2){
				mkpattern=mkpattern << 1;
				mkpattern++;
				i++;
			}


			if ((dataraw & mkpattern)==mkpattern){
				length=samplenum-1;
				data_repattern=data_repattern >>> 1;
				data_repattern=data_repattern | 0x80000000;
				data_receivebit="1";	// 받은 데이터

				return true;
					
			} else if ((dataraw & mkpattern)==0){
				length=samplenum-1;
				data_repattern=data_repattern >>> 1;
				data_receivebit="0";					// 받은 데이터

				
				return true;
			}

			i=samplenum+1;
			if ((dataraw & mkpattern)==mkpattern){
				length=samplenum-1;
				data_repattern=data_repattern >>> 1;
				data_repattern=data_repattern | 0x80000000;
				data_receivebit="1";	// 받은 데이터

				return true;
					
			} else if ((dataraw & mkpattern)==0){
				length=samplenum-1;
				data_repattern=data_repattern >>> 1;
				data_receivebit="0";					// 받은 데이터

				
				return true;
			}
			
			i=samplenum;
			while(i>0){
				mkpattern=mkpattern >>> 1;
				if ((dataraw & mkpattern)==mkpattern){
					if ((samplenum-2<=i)&(i<=samplenum)){

						data_repattern=data_repattern >>> 1;
						data_repattern=data_repattern | 0x80000000;
						data_receivebit="1";					// 받은 데이터
						length=i-1;
						
						return true;
					} else {
						length=i-1;
						data_receivebit="";					// 받은 데이터
						return false;
					}
				} else if ((dataraw & mkpattern)==0){
					if ((samplenum-2<=i)&(i<=samplenum)){
						data_repattern=data_repattern >>> 1;
						data_receivebit="0";					// 받은 데이터
						length=i-1;
						return true;
					} else {
						length=i-1;
						data_receivebit="";					// 받은 데이터
						return false;
					}
				} 
				i--;
			}
		} else {
			length--;
			data_receivebit="";					// 받은 데이터
			return false;
		}

		return false;
		
	}

	public boolean repattern_sub(){

		int i=0;		
			
			// pattern 만들기 
			mkpattern=0;
			i=0;
			while(i<samplenum+pattern_plus+1){
				mkpattern=mkpattern << 1;
				mkpattern++;
				i++;
			}
			long mkpattern2=mkpattern;


			// 원하는 pattern인지 확인 (원하는 패턴보다 개수(samplenum) 보다 많은 경우)
			for (int n=0; n<pattern_plus;n++) {
				mkpattern=mkpattern >>> 1;			// pattern 한칸 땡기기
				mkpattern2=mkpattern2 << 1;
				mkpattern2=mkpattern2&mkpattern;
				if ((dataraw & mkpattern)==mkpattern){
					return false;
						
				} else if ((dataraw & mkpattern)==0){
					return false;
				}
			}

			// 원하는 pattern인지 확인 (원하는 패턴보다 개수(samplenum) 보다 적은 경우)
			i=samplenum;
			while(i>0){
				mkpattern=mkpattern >>> 1;
				mkpattern2=mkpattern2 << 1;
				mkpattern2=mkpattern2&mkpattern;
			
				if ((dataraw & mkpattern)==mkpattern){
					if ((samplenum-pattern_plus<=i)&(i<=samplenum)){
						return false;
					} else if (i<samplenum-pattern_plus){
						dataraw=dataraw & ~mkpattern;
						return true;
					} else {
						return false;
					}
				} else if ((dataraw & mkpattern)==0){
					if ((samplenum-pattern_plus<=i)&(i<=samplenum)){
						return false;
					} else if ((pattern_plus<=i)&(i<samplenum-pattern_plus)){
						
						return true;
					} else {
						return false;
					}
				} 
				i--;
			}

		return false;
		
	}
	public boolean repattern3(){
		
		int i=0;		
		if (length==0){
			repattern_sub();
			if ((data_repattern & 0x1)==1){ 
				data_prebit=1;
			}
			else if ((data_repattern & 0x1)==0){
				data_prebit=0;
			}
			
			
			// pattern 만들기 
			mkpattern=0;
			i=0;
			while(i<samplenum+pattern_plus+1){
				mkpattern=mkpattern << 1;
				mkpattern++;
				i++;
			}



			// 원하는 pattern인지 확인 (원하는 패턴보다 개수(samplenum) 보다 많은 경우)
			for (int n=0; n<pattern_plus;n++) {
				mkpattern=mkpattern >>> 1;			// pattern 한칸 땡기기
				if ((dataraw & mkpattern)==mkpattern){
					length=samplenum-1;
					data_repattern=data_repattern >>> 1;
					data_repattern=data_repattern | mkpattern1;
					data_receivebit="1";	// 받은 데이터

					return true;
						
				} else if ((dataraw & mkpattern)==0){
					length=samplenum-1;
					data_repattern=data_repattern >>> 1;
					data_receivebit="0";					// 받은 데이터

					
					return true;
				}
			}

			// 원하는 pattern인지 확인 (원하는 패턴보다 개수(samplenum) 보다 적은 경우)
			i=samplenum;
			while(i>0){
				mkpattern=mkpattern >>> 1;
				if ((dataraw & mkpattern)==mkpattern){
					if ((samplenum-pattern_plus<=i)&(i<=samplenum)){

						data_repattern=data_repattern >>> 1;
						data_repattern=data_repattern | mkpattern1;
						data_receivebit="1";					// 받은 데이터
						length=i-1;
						
						
						return true;
					} else {
						length=i-1;
						data_receivebit="";					// 받은 데이터
						return false;
					}
				} else if ((dataraw & mkpattern)==0){
					if ((samplenum-pattern_plus<=i)&(i<=samplenum)){
						data_repattern=data_repattern >>> 1;
						data_receivebit="0";					// 받은 데이터
						length=i-1;
						return true;
					} else {
						length=i-1;
						data_receivebit="";					// 받은 데이터
						return false;
					}
				} 
				i--;
			}
		} else {
			length--;
			data_receivebit="";					// 받은 데이터
			return false;
		}

		return false;
		
	}
	public boolean compare(long data_repattern2){
		if ((data_repattern2 & mkp)==0x8005 ){

			return true;
		} else {
			return false;
		}
		
	}

	public boolean compare_print(long data_repattern2){
		
		if ((data_repattern2 & 0xFF)==0x05 ){
			return true;
		} else {
			return false;
		}
				
	}
	
	public String delayctr(){
		if ((data_repattern & 0x7FFF)==0x4005 ){
			return "-";
		} else if ((data_repattern & 0x1FFFF)==0x10005 ){
			return "+";
		} 
		return "y";
				
	}
	public int data_firstone(){

		if((dataraw & 0x0F)==0x01 ){
			return 1;
		}else if((dataraw & 0x0F)==0x03 ){
			return 2;
		}else if((dataraw & 0x0F)==0x05 ){
			return 1;
		}else if((dataraw & 0x0F)==0x07 ){
			return 3;
		}else if((dataraw & 0x0F)==0x09 ){
			return 1;
		}else if((dataraw & 0x0F)==0x0B ){
			return 2;
		}else if((dataraw & 0x0F)==0x0D ){
			return 1;
		}else if((dataraw & 0x0F)==0x0F ){
			return 2;
		}
		return 0;
	}
	public boolean datain(){
		if((dataraw & 0x0F)==0x00 ){
			data_receive=data_receive << 1;
			data_length=2;
			data_bit++;
			data_receivebit="0";
			return true;
		}
		else if((dataraw & 0x0F)==0x02 ){
			data_receive=data_receive << 1;
			data_length=1;
			data_bit++;
			data_receivebit="0";
			return true;
		}
		return false;
	}
}
