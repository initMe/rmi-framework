package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileTester {
	
	public FileTester() throws Exception {
		File f = new File("D:\\woyao\\woyao\\cfg\\mysql - 副本");
		exec(f);
	}
	private void exec(File f) {
		if(f.getName().equals(".svn")) {
			return;
		}
		if(f.isDirectory()) {
			File[] sonFs = f.listFiles();
			for(File sonFile : sonFs) {
				exec(sonFile);
			}
		}
		else if(f.isFile() && f.getName().indexOf(".xml")>0) {
			System.out.println("开始处理：" + f.getName());
			BufferedReader br = null;
			BufferedWriter bw = null;
			List<String> contextList = new ArrayList<String>();
			try {
				br = new BufferedReader(new FileReader(f));
				String line = null;
				while((line=br.readLine()) != null) {
					if(line.indexOf("<insert")>=0 
							|| line.indexOf("<update")>=0 
							|| line.indexOf("<select")>=0 
							|| line.indexOf("<delete")>=0
							|| line.indexOf("<if")>=0) {
					} else {
						List<String> parseList = new ArrayList<String>();
						int index=0;
						while(line.indexOf("#{") >= 0) {
							String parseStr = line.substring(line.indexOf("#{"), line.indexOf("}")+1);
							parseList.add(parseStr);
							line = line.replace(parseStr, "00000000"+index);
							index ++;
						}
						line = line.toLowerCase();
						for(int i=0; i<parseList.size(); i++) {
							String parseStr = parseList.get(i);
							line = line.replace("00000000"+i, parseStr);
						}
					}
					contextList.add(line);
				}
				br.close();
				bw = new BufferedWriter(new FileWriter(f));
				for(String context : contextList) {
					bw.write(context);
					bw.newLine();
					bw.flush();
				}
				bw.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(br != null) {
						br.close();
					}
					if(bw != null) {
						bw.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String[] args) {
		try {
			new FileTester();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
