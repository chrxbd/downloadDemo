package net.util.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Download {
	//资源路径
	private String uri;
	//保存位置
	private String savePath;
	//线程数
	private int threadNum;
	//文件大小
	private long fileSize;
	//下载线程数组
	DownloadThread[] downloadThread;
	
	//保存文件
	RandomAccessFile file;
	
	public Download(String uri, String savePath, int threadNum) {
		this.uri = uri;
		this.savePath = savePath;
		this.threadNum = threadNum;
		downloadThread = new DownloadThread[threadNum];
	}
	
	public Download(String uri, String savePath) {
		//默认4个线程
		this(uri, savePath, 4);
	}
	
	public void download() throws IOException{
		URL url = new URL(uri);
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "image/gif,image/jpeg,image/pjpeg," + 
						"image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
						+ "application/x-shockwave-flash, application/xaml+xml, "
						+ "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
						+ "application/x-ms-application, application/vnd.ms-excel, "
						+ "application/vnd.ms-powerpoint, application/msword, */*");
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Connection", "Keep-Alive");
		
		fileSize = conn.getContentLengthLong();
		
		conn.disconnect();
		//if(fileSize <= 0) throws 
		file = new RandomAccessFile(savePath,"rw");
		file.setLength(fileSize);
		
		
		
		
		long partSize = fileSize / threadNum + 1;
		for(int i = 0; i < threadNum; ++i){
			
			long startPos = i * partSize;
			RandomAccessFile currentPart = new RandomAccessFile(savePath,"rw");
			currentPart.seek(startPos);
			downloadThread[i] = new DownloadThread(uri, currentPart, startPos, partSize);
			downloadThread[i].start();
		}
	}
	
	public float getCompleteRate(){
		long doneLength = 0;
		for(int i = 0; i < threadNum; ++i){
			doneLength += downloadThread[i].doneLength;
		}
		return (float) ((doneLength * 1.0) / fileSize);
	}
	
	
	class DownloadThread extends Thread{
		
		private String uri;
		
		private RandomAccessFile file;
		//文件开始位置
		private long startPos;
		//当前线程下载的块的大小
		private long partSize;
		
		//已完成长度
		private long doneLength = 0;
		
		public DownloadThread(String uri, RandomAccessFile file, long startPos,long partSize) {
			super();
			this.uri = uri;
			this.file = file;
			this.startPos = startPos;
			this.partSize = partSize;
		}

		@Override
		public void run(){
			try {
				URL url = new URL(uri);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5 * 1000);
				conn.setRequestMethod("GET");
				conn.addRequestProperty("Accept", "image/gif,image/jpeg,image/pjpeg," + 
						"image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
						+ "application/x-shockwave-flash, application/xaml+xml, "
						+ "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
						+ "application/x-ms-application, application/vnd.ms-excel, "
						+ "application/vnd.ms-powerpoint, application/msword, */*");
				conn.setRequestProperty("Accept-Language", "zh-CN");
				conn.setRequestProperty("Charset", "UTF-8");
				
				InputStream is = conn.getInputStream();
				
				is.skip(startPos);
				byte buffer[] = new byte[1024];
				int len = 0;
				while( doneLength < partSize 
						&& (len = is.read(buffer)) != -1){
					file.write(buffer, 0, len);
					doneLength += len;
				}
				file.close();
				is.close();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}
