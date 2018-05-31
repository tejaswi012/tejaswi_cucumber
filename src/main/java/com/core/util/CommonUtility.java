package com.core.util;

import java.util.ArrayList;
import java.util.List;

public class CommonUtility {

	public boolean verifyRecords(List<ArrayList<String>> recordsBefore,
			List<ArrayList<String>> recordsAfter) {

		int cnt = 0;
		boolean isRecordsCorrect = true;
		for (ArrayList<String> list : recordsBefore) {
			isRecordsCorrect = verifyList(list, recordsAfter.get(cnt), cnt);
			cnt = cnt + 1;
		}

		return isRecordsCorrect;
	}

	public boolean verifyList(List<String> recordsExpected,
			List<String> recordsPresent, int rowNum) {
		boolean status = true;
		for (int i = 0; i < recordsExpected.size(); i++) {
			if (!recordsExpected
					.get(i)
					.replaceAll("\\s+", "")
					.replace("$", "")
					.replaceAll(",", "")
					.equals(recordsPresent.get(i).trim().replaceAll("//s+", "")
							.replace("$", "").replace(",", ""))) {
				status = false;
				int tmpLoc = i + 1;
				System.out.println("Grid Record comparision" + (rowNum + 1)
						+ "..at colum number" + tmpLoc + "of the lists"
						+ "records expected is" + recordsExpected.get(i)
						+ "But Present is " + recordsPresent.get(i) + "..");
				break;
			}
		}
		return status;
	}

	public boolean verifyList(List<String> recordsExpected,
			List<String> recordsPresent) {
		boolean status = true;
		for (int i = 0; i < recordsExpected.size(); i++) {
			if (!recordsExpected
					.get(i)
					.replaceAll("\\s+", "")
					.replace("$", "")
					.replaceAll(",", "")
					.equals(recordsPresent.get(i).trim().replaceAll("//s+", "")
							.replace("$", "").replace(",", ""))) {
				status = false;
				int tmpLoc = i + 1;
				System.out.println("Grid Record comparision" + (i + 1)
						+ "..at colum number" + tmpLoc + "of the lists"
						+ "records expected is" + recordsExpected.get(i)
						+ "But Present is " + recordsPresent.get(i) + "..");
				break;
			}
		}
		return status;
	}
}
