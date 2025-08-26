package youtube;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class YouCrawler {

	static String videosBegin = "\"title\":\"Videos\"";
	static String titleSearch = "\"title\":{\"runs\":[{\"text\":\"";
	static String labelSearch = "\"accessibility\":{\"accessibilityData\":{\"label\":\"";
	static String watchSearch = "/watch?v=";
	static String pubTimeSearch = "\"publishedTimeText\":{\"simpleText\":\"";
	static String fileName = "youtubers.html";

	public static void main(String[] args) throws IOException {
		ArrayList<YouTuber> youTubers = new ArrayList<YouTuber>();
		String file = "youtubers.csv";
		String str;

		try (FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
				BufferedReader reader = new BufferedReader(isr)) {
			while ((str = reader.readLine()) != null) {
				String[] data = str.split(",");
				if (data[2].equals("1")) {
					System.out.println("link: " + data[1]);
					youTubers.add(new YouTuber(data[0], data[1], data[2]));
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ArrayList<Video> allVideos = new ArrayList<Video>();
		ArrayList<Video> firstVideos = new ArrayList<Video>();
		ArrayList<Video> allYoutuberVideos = new ArrayList<Video>();

		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		writer.write("<HTML>\n");
		writer.write(generateFunctions());
		writer.write("<BODY>\n");

		for (YouTuber youTuber : youTubers) {
			allVideos = getAllVideos(youTuber.getUrlString(), youTuber.getName());
			try {
				firstVideos.add(allVideos.get(0));

				allVideos.remove(0);
				allYoutuberVideos.addAll(allVideos);

				System.out.println(
						"<a href=" + allVideos.get(0).getWatchLink() + ">" + allVideos.get(0).getTitle() + "</a>");
			} catch (Exception e) {
			}
		}

		writer.write(Convert2Table(firstVideos, "New Videos"));
		writer.write(Convert2Table(allYoutuberVideos, "Old Videos"));

		writer.write("</BODY></HTML>\n");
		writer.flush();
		writer.close();
	}

	private static String generateFunctions() {
		return "<script type=\"text/javascript\">\r\n" + "function myFunction(id) {\r\n"
				+ "  var eb = document.getElementById('b' + id).getElementsByTagName('button')[0];\r\n"
				+ "  var et = document.getElementById(id).getElementsByTagName('a')[0];\r\n"
				+ "  var author = eb.innerHTML;\r\n" + "  var href = et.href;\r\n" + "  var title = et.innerHTML;\r\n"
				+ "  var rowText = \"<tr><td>\" + author + \"</td>\";\r\n"
				+ "  rowText = rowText + \"<td><a href=\" + href + \">\";\r\n"
				+ "  rowText = rowText + title + \"</td></tr>\";\r\n"
				+ "  var link = document.createElement(\"a\");\r\n" + "  link.href = \"data:text/html,\" + rowText;\r\n"
				+ "  link.download = \"favorate.html\";\r\n" + "  link.click();\r\n" + "  delete link;\r\n" + "}\r\n"
				+ "</script>\r\n";
	}

	private static String Convert2Table(ArrayList<Video> videos, String tableName) {
		int i = 1;
		StringBuilder builder = new StringBuilder();
		builder.append("<TABLE border=\"1px solid black\"; style=\"margin-top:10px; margin-left:30px;\">\n");

		for (Video video : videos) {
			builder.append("<tr>");
//			builder.append("<td id=\"b" + i + "\"> <button onclick=\"myFunction(\'" + i + "\')\">");
//			builder.append(video.getName());
//			builder.append("</button> </td>");
			builder.append("<td>" + video.getName() + "</td>");
			builder.append("<td>" + video.getPubTime() + "</td>");
			builder.append("<td id=\"" + i + "\"><a href=");
			builder.append(video.getWatchLink() + ">" + video.getTitle());
			builder.append("</a><br></td>");
			builder.append("</tr>\n");

			i++;
		}

		builder.append("</TABLE>\n");

		return builder.toString();
	}

	@SuppressWarnings("deprecation")
	private static ArrayList<Video> getAllVideos(String urlString, String aName) {
		URL url = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		ArrayList<Video> allVideos = new ArrayList<Video>();

		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		try {
			isr = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

		br = new BufferedReader(isr);
		String line = null;
		String restString = null;
		try {
			while ((line = br.readLine()) != null) {
				if (line.indexOf(videosBegin) > 0) {
					restString = line.substring(line.indexOf(videosBegin));
				} else {
					restString = line;
				}

				while (restString.indexOf(titleSearch) >= 0 && restString.indexOf(watchSearch) >= 0) {
					int watchStart = restString.indexOf(watchSearch) + watchSearch.length() + 11;
					String watchIndex = restString.substring(restString.indexOf(watchSearch),
							restString.indexOf(watchSearch) + watchSearch.length() + 11);
					String labelString = restString.substring(restString.indexOf(titleSearch) + titleSearch.length(),
							restString.indexOf(titleSearch) + restString.substring(restString.indexOf(titleSearch))
									.indexOf("\"", titleSearch.length()));

					int iPubTime = restString.indexOf(pubTimeSearch);
					String pubTimeString = "Vor Unbekannt";
					if (iPubTime >= 0) {
						pubTimeString = restString.substring(restString.indexOf(pubTimeSearch) + pubTimeSearch.length(),
								restString.indexOf(pubTimeSearch)
										+ restString.substring(restString.indexOf(pubTimeSearch)).indexOf("\"",
												pubTimeSearch.length()));
					}

					restString = restString.substring(restString.indexOf(watchSearch, watchStart) + watchSearch.length() + 11);

					String link = "https://www.youtube.com" + watchIndex;

					allVideos.add(new Video(link, aName, pubTimeString.substring(4), labelString));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return allVideos;
	}
}
