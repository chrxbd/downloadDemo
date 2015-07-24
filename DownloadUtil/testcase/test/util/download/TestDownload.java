package test.util.download;

import net.util.download.Download;
import junit.framework.TestCase;

public class TestDownload extends TestCase{
	
	public void testDownload(){
		//�͸���һ����ʹ
		String url = "http://d.hiphotos.baidu.com/image/pic/item/730e0cf3d7ca7bcb997158e9bb096b63f724a8ea.jpg";
		String savePath = "D://angel.jpg";
		
		Download dl = new Download(url,savePath);
		
		try {
			dl.download();
			System.out.println("��ʼ����");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");  
		
		while((100 - dl.getCompleteRate() * 100) > 0.00001){
			System.out.println("����� + " + df.format(dl.getCompleteRate() * 100) + "%");
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("�������");
	}

}
