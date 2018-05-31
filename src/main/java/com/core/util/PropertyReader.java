package com.core.util;

import static com.core.util.Constants.APPLICATION_PROPERTIES;
import static com.core.util.Constants.DB_PROPERTIES;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class PropertyReader {

	public String getDBproperty(String sKey) throws IOException {
		Properties props = new Properties();
		String sFilePath = System.getProperty("user.dir");
		sFilePath = sFilePath + File.separator + DB_PROPERTIES;
		FileInputStream fs = new FileInputStream(sFilePath);
		String sVal = "";
		try {
			props.load(fs);
			sVal = props.getProperty(sKey);
			if (sVal == "") {
				return null;
			}

		} catch (IOException e) {
			// TODO: handle exception
		} finally {

			fs.close();
		}

		return sVal;

	}

	public String getApplicationproperty(String sKey) throws IOException {
		Properties props = new Properties();
		String sFilePath = System.getProperty("user.dir");
		sFilePath = sFilePath + File.separator + APPLICATION_PROPERTIES;
		FileInputStream fs = new FileInputStream(sFilePath);
		String sVal = "";
		try {
			props.load(fs);
			sVal = props.getProperty(sKey);
			if (sVal == "") {
				return null;
			}

		} catch (IOException e) {
			// TODO: handle exception
		} finally {

			fs.close();
		}
		// System.out.println("value from propery files" + sVal);
		return sVal;

	}

	public HashMap<String, String> getProperty(String fileName) throws IOException {

		HashMap<String, String> map = new HashMap<String, String>();

		Properties props = new Properties();

		FileInputStream fs = new FileInputStream(fileName);
		try {
			props.load(fs);
			for (final Entry<Object, Object> entry : props.entrySet()) {
				map.put((String) entry.getKey(), (String) entry.getValue());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			fs.close();
		}
		return map;

	}

	public Map<String, String> loadMapsFromPropertyFile(File savedHasMaps, HashMap<String, String> map) {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(savedHasMaps));
			if (!prop.isEmpty()) {
				map.putAll((Map) prop);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public String getProperty(String fileName, String sKey) throws IOException {
		Properties props = new Properties();
		FileInputStream fs = new FileInputStream(fileName);
		String sVal = "";

		try {

			props.load(fs);
			sVal = props.getProperty(sKey);
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			fs.close();
		}

		return sVal;
	}
}
