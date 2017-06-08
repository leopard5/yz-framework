package com.yz.framework.io;

import java.io.IOException;

import com.yz.framework.util.FileUtil;

public class FileWriteRead {
	private String fileName;

	public FileWriteRead(String fileName) {
		this.fileName = fileName;
	}

	public void write(String str) throws IOException {
		FileUtil.writeUseBio(fileName, str);
	}

	public String read() throws IOException {
		return FileUtil.readStringUseNio(fileName);
	}

	public void write(long l) throws IOException {
		write(String.valueOf(l));
	}

	public long readLong() throws IOException {
		String str = read();
		if (str != null && str.length() > 0) {
			return Long.valueOf(str);
		}
		return 0;
	}
}
