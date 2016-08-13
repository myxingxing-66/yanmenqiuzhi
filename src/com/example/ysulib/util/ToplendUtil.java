package com.example.ysulib.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.example.ysulib.bean.NoteList;

public class ToplendUtil {
	List<NoteList> notelist=new ArrayList<NoteList>();
	public List<NoteList> lendTop(){
		Document doc;
		String html = TopLendGetResponse.sendGet("http://202.206.242.99/top/top_lend.php","");
		doc=Jsoup.parse(html);
		Elements note=doc.getElementsByClass("table_line");
		Elements note_tr=note.get(0).getElementsByTag("tr");
		for (int i = 1; i < note_tr.size(); i++) {
			NoteList notetr = new NoteList();
			Elements notetd = note_tr.get(i).getElementsByTag("td");
			notetr.setNumber(notetd.get(0).text().toString());
			notetr.setTitle(notetd.get(1).text().toString());
			notetr.setBookhref(notetd.get(1).getElementsByTag("a").attr("href")
					.substring(2, 35).toString());
			notetr.setAuthor(notetd.get(2).text().toString());
			notetr.setPublish(notetd.get(3).text().toString());
			notetr.setIsbn(notetd.get(4).text().toString());
			notetr.setSav_num(notetd.get(5).text().toString());
			notetr.setLend_num(notetd.get(6).text().toString());
			notelist.add(notetr);
		}
	return notelist;
	}
}

